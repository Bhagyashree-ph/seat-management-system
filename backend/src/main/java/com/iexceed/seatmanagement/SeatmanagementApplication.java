package com.iexceed.seatmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.iexceed.seatmanagement")
public class SeatmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatmanagementApplication.class, args);
	}

}
