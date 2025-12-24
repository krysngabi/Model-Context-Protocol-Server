package com.abovebytes.mcp.repositories;

import com.abovebytes.mcp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    long countByActiveTrue();
}
