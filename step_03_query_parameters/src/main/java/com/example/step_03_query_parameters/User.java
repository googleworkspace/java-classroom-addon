package com.example.step_03_query_parameters;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** An entity class that provides a model to store user information. */
@Entity
@Table(name = "users")
public class User {

  /** The user's unique Google ID. The @Id annotation specifies that this is the primary key. */
  @Id
  @Column
  private String id;

  /** The user's email address. */
  @Column
  private String email;

  /** Required User class no args constructor. */
  public User() {
  }

  /** The User class constructor that creates a User object with the specified parameters.
   * @param id the user's unique Google ID
   * @param email the user's email address
   */
  public User(String id, String email) {
    this.id = id;
    this.email = email;
  }

  /** Getter and setter for the id column. */
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  /** Getter and setter for the email column. */
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

 /** Creates and returns the User object as a String.
  * @return String representation of the User object. */
  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", email='" + email + '\'' +
        '}';
  }
}
