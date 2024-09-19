package com.una.paradigmas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ParadigmasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParadigmasApplication.class, args);
	}
}
