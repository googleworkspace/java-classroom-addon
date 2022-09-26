package com.example.step_03_query_parameters;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Provides CRUD operations for the User class by extending the CrudRepository interface. */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
}