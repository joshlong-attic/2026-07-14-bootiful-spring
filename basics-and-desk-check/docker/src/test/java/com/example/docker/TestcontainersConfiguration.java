package com.example.docker;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	// @Bean
	// @ServiceConnection
	// PostgreSQLContainer postgresContainer() {
	// return new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));
	// }

}
