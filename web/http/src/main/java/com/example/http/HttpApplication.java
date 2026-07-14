package com.example.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Collection;
import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
@ImportHttpServices(CatFactsClient.class)
public class HttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> serverResponseRouterFunction(Greetings greetings) {
        return route()
                .GET("/hi", _ -> ServerResponse.ok().body(greetings.hello()))
                .build();
    }
}

@Component
class Greetings {

    Map<String, String> hello() {
        return Map.of("message", "Hello, World!");
    }
}

interface CatFactsClient {

    @GetExchange("https://www.catfacts.net/api")
    CatFacts facts();
}
/*
@Component
class CatFactsClient {

    private final RestClient http;

    CatFactsClient(RestClient.Builder http) {
        this.http = http.build();
    }

    CatFacts facts() {
        return this.http.get()
                .uri("https://www.catfacts.net/api")
                .retrieve()
                .body(CatFacts.class);

    }

} */

record CatFact(String fact) {
}

record CatFacts(Collection<CatFact> facts) {
}

@Controller
@ResponseBody
class CatFactsController {

    private final CatFactsClient catFactsClient;

    CatFactsController(CatFactsClient catFactsClient) {
        this.catFactsClient = catFactsClient;
    }

    @GetMapping("/cats")
    CatFacts facts() {
        return catFactsClient.facts();
    }
}

@Controller
@ResponseBody
class HelloController {

    private final Greetings greetings;

    HelloController(Greetings greetings) {
        this.greetings = greetings;
    }

    @GetMapping("/hello")
    Map<String, String> hello() {
        return greetings.hello();
    }
}