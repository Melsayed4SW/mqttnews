package com.beaconsolutions.LatestEgyptNews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LatestEgyptNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LatestEgyptNewsApplication.class, args);
	}

}
