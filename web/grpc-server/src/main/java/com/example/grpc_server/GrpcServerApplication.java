package com.example.grpc_server;

import com.example.service.grpc.GreetingRequest;
import com.example.service.grpc.GreetingResponse;
import com.example.service.grpc.GreetingsGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class GrpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);
    }

}

// this _almost_ works: grpcurl --plaintext -d {"name":"josh"} localhost:9090 Greetings.hello
@Service
class MyGreetingsService extends GreetingsGrpc.GreetingsImplBase {

    @Override
    public void hello(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        IO.println("Received request: " + request.getName());
        var build = GreetingResponse.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();

    }
}