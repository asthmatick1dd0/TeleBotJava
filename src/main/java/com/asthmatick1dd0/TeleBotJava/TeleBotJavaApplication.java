package com.asthmatick1dd0.TeleBotJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TeleBotJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeleBotJavaApplication.class, args);
	}

}
