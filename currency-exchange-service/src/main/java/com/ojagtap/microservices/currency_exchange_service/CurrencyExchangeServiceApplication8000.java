package com.ojagtap.microservices.currency_exchange_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyExchangeServiceApplication8000 {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeServiceApplication8000.class, args);
	}

}
