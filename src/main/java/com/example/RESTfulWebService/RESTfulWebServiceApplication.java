package com.example.RESTfulWebService;

import com.example.RESTfulWebService.controllers.book.BookResponseBody;
import com.example.RESTfulWebService.persistence.BookEntity;
import com.example.RESTfulWebService.persistence.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Optional;
import java.util.UUID;


@SpringBootApplication
public class RESTfulWebServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(RESTfulWebServiceApplication.class);
	//port 5432

	public static void main(String[] args) {
		SpringApplication.run(RESTfulWebServiceApplication.class, args);
	}
}
