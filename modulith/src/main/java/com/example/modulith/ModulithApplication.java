package com.example.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class ModulithApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
    }

}

//@Component
class Replayer {

    private final IncompleteEventPublications eventPublications;

    Replayer(IncompleteEventPublications eventPublications) {
        this.eventPublications = eventPublications;
    }

    @Scheduled(fixedRate = 1000)
    void schedule() {
        this.eventPublications
                .resubmitIncompletePublications(e -> true);
    }
}