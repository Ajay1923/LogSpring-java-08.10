package com.crud.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crud.demo.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndEnabled(String username, boolean enabled);
    Optional<User> findById(Long id); 
}
