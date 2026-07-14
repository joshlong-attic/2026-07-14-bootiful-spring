package com.example.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class GraphqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }

}


record Customer(int id, String name) {
}

@Service
class CustomerService {

    private final Map<Integer, Customer> customers = Map.of( //
            1, new Customer(2, "John"), //
            2, new Customer(1, "Jane"), //
            3, new Customer(3, "Bob")//
    );


    Collection<Customer> customers() {
        return customers.values();
    }

    Profile profile(String profileName) {
        return new Profile(profileName);
    }
}

record Profile(String profileName) {
}
/*
   subscriptions
   queries
   mutations
 */

@Controller
class CustomerController {


    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @MutationMapping
    Customer createCustomer (@Argument int id) {
        return new Customer(id, "new customer");
    }

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customers) {
        IO.println("resolving profile for customers " + customers);
        // select * from profile where customer_id IN  (1,2,3)
        var m = new HashMap<Customer, Profile>();
        for (var c : customers) {
            var profile = customerService.profile(c.name());
            IO.println("adding " + profile);
            m.put(c, profile);
        }
        return m;
    }

    /*
    @SchemaMapping(typeName = "Customer")
    Profile profile(Customer customer) {
        IO.println("resolving profile for customer " + customer);
        return customerService.profile(customer.name());
    }

     */

    @QueryMapping
    Collection<Customer> customers() {
        return this.customerService.customers();
    }

}