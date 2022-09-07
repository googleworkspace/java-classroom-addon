package com.example.sign_in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the application server on the port specified in the application.properties file. */
@SpringBootApplication
public class SignInApplication {

  public static void main(String[] args) {
    SpringApplication.run(SignInApplication.class, args);
  }

}
