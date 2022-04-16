package com.numble.shortForm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShortFormApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortFormApplication.class, args);
	}

}
