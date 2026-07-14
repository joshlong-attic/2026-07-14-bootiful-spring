package com.example.integration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

@SpringBootApplication
public class IntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationApplication.class, args);
    }

}

@Controller
@ResponseBody
class HttpLoggingController {

    private final MessageChannel loggingChannel;
    private final LoggingGateway loggingGateway;

    HttpLoggingController(@Qualifier(IntegrationConfiguration.LOGGING) MessageChannel loggingChannel, LoggingGateway loggingGateway) {
        this.loggingChannel = loggingChannel;
        this.loggingGateway = loggingGateway;
    }

    @GetMapping("/anotherone")
    void anotherone() {
        loggingGateway.log("i hope this message finds you well");
    }

    @GetMapping("/")
    void log(@RequestParam String payload) {
        var build = MessageBuilder
                .withPayload(payload)
                .build();
        this.loggingChannel.send(build);
    }
}

@Configuration
@IntegrationComponentScan
class IntegrationConfiguration {

    // POP
    // IMAP-IDLE

    static final String LOGGING = "logging";

    @Bean(LOGGING)
    DirectChannelSpec loggingChannel() {
        return MessageChannels.direct();
    }

    @Bean
    IntegrationFlow fileToConsoleFlow(
            @Qualifier(LOGGING) MessageChannel loggingChannel,
            @Value("file://${HOME}/Desktop/in") File file) {
        var fileInboundAdapter = Files
                .inboundAdapter(file)
                .autoCreateDirectory(true);
        return IntegrationFlow
                .from(fileInboundAdapter, p -> p.poller(pm -> pm.fixedDelay(1000)))
                .transform(new FileToStringTransformer())
//                .route()
//                .split()
//                .aggregate()
//                .filter( new GenericFilter<String>(payload -> payload.contains("test")))
                .channel(loggingChannel)
                .get();
    }

    private static String dump(String file,
                               MessageHeaders headers) {
        IO.println("==============================");
        headers.forEach((k, v) -> IO.println(k + " -> " + v));
        IO.println(file);
        return file;
    }

    @Bean
    IntegrationFlow loggingFlow(
            AmqpTemplate template,
            @Qualifier(LOGGING) MessageChannel loggingChannel) {
        return IntegrationFlow
                .from(loggingChannel)
                .handle(IntegrationConfiguration::dump)
                .handle(Amqp.outboundAdapter(template).routingKey("logging").exchangeName("logging"))
                .get();
    }

    final static String LOGGING_AMQP = "logging";

    @Bean
    Queue queue() {
        return QueueBuilder.durable(LOGGING_AMQP).build();
    }

    @Bean
    Exchange exchange() {
        return ExchangeBuilder.directExchange(LOGGING_AMQP).build();
    }

    @Bean
    Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LOGGING_AMQP).noargs();
    }

}

@MessagingGateway(defaultRequestChannel = IntegrationConfiguration.LOGGING_AMQP)
interface LoggingGateway {

    void log(String file);
}

@Component
class Listener {

    @RabbitListener(queues = IntegrationConfiguration.LOGGING_AMQP)
    void listen(String file) {
        IO.println("got a message:" + file);
    }
}