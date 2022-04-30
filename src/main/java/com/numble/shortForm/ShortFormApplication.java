package com.numble.shortForm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class ShortFormApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ShortFormApplication.class, args);
	}

	@Bean
	JPAQueryFactory queryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

	@PostConstruct
	public void timezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins("*");
		registry.addMapping("/*").allowedOrigins("*");
	}
}
