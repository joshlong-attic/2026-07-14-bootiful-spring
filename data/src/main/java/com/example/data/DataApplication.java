package com.example.data;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootApplication
public class DataApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(JdbcClient jdbcClient) {
        return _ -> jdbcClient
                .sql("select * from customer")
                .query((rs, rowNum) -> new Customer(
                        rs.getInt("id"),
                        rs.getString("name"))
                )
                .list()
                .forEach(IO::println);
    }
}

record Customer(int id, String name) {
}