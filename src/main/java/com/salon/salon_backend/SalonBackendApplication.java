package com.salon.salon_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalonBackendApplication.class, args);
	}

}
