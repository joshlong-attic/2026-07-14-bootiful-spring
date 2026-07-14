package com.example.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.annotation.ClientRegistrationId;
import org.springframework.security.oauth2.client.web.client.support.OAuth2RestClientHttpServiceGroupConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(MessageClient.class)
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    OAuth2RestClientHttpServiceGroupConfigurer auth2RestClientHttpServiceGroupConfigurer(OAuth2AuthorizedClientManager s) {
        return OAuth2RestClientHttpServiceGroupConfigurer.from(s);
    }
}

@Controller
@ResponseBody
class MeController {

    private final MessageClient messageClient;

    MeController(MessageClient messageClient) {
        this.messageClient = messageClient;
    }

    @GetMapping("/")
    Message message(
//            @RegisteredOAuth2AuthorizedClient("spring") OAuth2AuthorizedClient authorizedClient
//            Principal principal
    ) {
        return this.messageClient.get();//(authorizedClient.getAccessToken().getTokenValue());
    }
}

@ClientRegistrationId("spring")
interface MessageClient {

    @GetExchange("http://localhost:8081/message")
    Message get();
}
record Message(String message) {
}

//@Component
//class MessageClient {
//
//    private final RestClient http;
//
//    MessageClient(RestClient.Builder http,
//                  OAuth2AuthorizedClientManager auth2AuthorizedClientManager
//    ) {
//        this.http = http
//                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(auth2AuthorizedClientManager))
//                .build();
//    }
//
//    Message get() {
//        return this.http.get()
//                .uri("http://localhost:8081/message")
//                .attributes(ClientAttributes.clientRegistrationId("spring"))

/// /                .headers(a -> a.setBearerAuth(token))
//                .retrieve()
//                .body(Message.class);
//    }
//}
