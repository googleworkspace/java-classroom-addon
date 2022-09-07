package com.example.basic_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the application server on the port specified in the application.properties file. */
@SpringBootApplication
public class BasicAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(BasicAppApplication.class, args);
  }

}
