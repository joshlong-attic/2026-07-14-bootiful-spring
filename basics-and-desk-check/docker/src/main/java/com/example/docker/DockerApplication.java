package com.example.docker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@SpringBootApplication
public class DockerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
    }

}

@Controller
@ResponseBody
class HelloController {

    HelloController(@Value("${saas.login}") String login,
                    @Value("${saas.password}") String password
    ) {
        IO.println("login: " + login + ":" + password);
    }

    @GetMapping("/")
    Map<String, String> hello() {
        return Map.of("message", "Hello World!!!!!!!!");
    }

}