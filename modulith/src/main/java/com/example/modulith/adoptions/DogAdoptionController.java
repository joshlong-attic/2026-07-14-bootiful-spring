package com.example.modulith.adoptions;


import com.example.modulith.adoptions.validation.Validation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
class DogAdoptionController {


    private final DogAdoptionService dogAdoptionService;

    DogAdoptionController(DogAdoptionService dogAdoptionService) {
        this.dogAdoptionService = dogAdoptionService;
    }

    @GetMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId, @RequestParam String owner) {
        this.dogAdoptionService.adopt(dogId, owner);
    }
}

@Service
@Transactional
class DogAdoptionService {

    private final Validation validation ;
    private final DogRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    DogAdoptionService(Validation validation, DogRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.validation = validation;
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    void adopt(int dogId, String owner) {
        repository.findById(dogId).ifPresent(dog -> {
            var updated = this.repository.save(
                    new Dog(dog.id(), dog.name(), dog.description(), owner));
            IO.println("adopted " + updated);
            this.applicationEventPublisher.publishEvent(new DogAdoptedEvent(dogId));
        });
    }

}

interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String description, String owner) {
}