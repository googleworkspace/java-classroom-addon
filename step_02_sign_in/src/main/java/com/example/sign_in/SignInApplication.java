package com.example.sign_in;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the application server on the port specified in the application.properties file. */
@SpringBootApplication
public class SignInApplication {

  /** Note that if you stop and re-start the server, any attributes stored in the session will be
   * cleared. */
  public static void main(String[] args) {
    SpringApplication.run(SignInApplication.class, args);
  }

}
