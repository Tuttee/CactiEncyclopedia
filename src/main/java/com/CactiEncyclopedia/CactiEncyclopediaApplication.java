package com.CactiEncyclopedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CactiEncyclopediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CactiEncyclopediaApplication.class, args);
	}

}
