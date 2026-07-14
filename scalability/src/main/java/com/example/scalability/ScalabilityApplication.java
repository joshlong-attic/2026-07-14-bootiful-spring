package com.example.scalability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class ScalabilityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScalabilityApplication.class, args);
    }

}

class ServletServer {


    void serve() throws Exception {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        ServerSocket socket = null;
        while (true) {
            try (var client = socket.accept();) {
                executor.execute(() -> {
                    try {
                        processRequest(client.getInputStream(), client.getOutputStream());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

    }

    private void processRequest(InputStream is, OutputStream os) throws Exception {
        try (var i = is) {
            var next = -1;
            while ((next = i.read()) != -1) {
                //
            }
        }
    }

}

@Controller
@ResponseBody
class DelayController {

    private final RestClient http;

    DelayController(RestClient.Builder http) {
        this.http = http.build();
    }

    @GetMapping("/delay")
    String delay() {
        var note = Thread.currentThread() + ":";
        var response = this.http
                .get()
                .uri("http://localhost:9000/delay/5")
                .retrieve()
                .body(String.class);
        note += Thread.currentThread();
        IO.println(note);
        return response;
    }
}