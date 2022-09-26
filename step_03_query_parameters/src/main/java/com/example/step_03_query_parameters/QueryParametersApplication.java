package com.example.step_03_query_parameters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QueryParametersApplication {

  public static void main(String[] args) {

    /** Note that if you stop and re-start the server, any attributes stored in the session will be
     * cleared.*/
    SpringApplication.run(QueryParametersApplication.class, args);
  }
}