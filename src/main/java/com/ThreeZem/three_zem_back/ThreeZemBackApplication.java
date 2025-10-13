package com.ThreeZem.three_zem_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ThreeZemBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreeZemBackApplication.class, args);
	}

}
