package com.iexceed.seatmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SeatmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatmanagementApplication.class, args);
	}

}
