package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.User;
import com.abovebytes.mcp.models.RoleValue;
import com.abovebytes.mcp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* ==========================
       TOOL METHODS (WRITE / ACTION)
       ========================== */

    @McpTool(name = "users_create", description = "Create a new application user")
    public User createUser(
            @McpToolParam(description = "Full name of the user") String fullName,
            @McpToolParam(description = "Email address (unique)") String email,
            @McpToolParam(description = "User role (STUDENT, INSTRUCTOR, ADMIN)") RoleValue role
    ) {
        log.info("McpTool called: users_create | email={}", email);
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @McpTool(name = "users_deactivate", description = "Deactivate a user by email")
    public String deactivateUser(
            @McpToolParam(description = "User email") String email
    ) {
        log.info("McpTool called: users_deactivate | email={}", email);
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                    return "User " + email + " deactivated";
                })
                .orElse("User not found with email " + email);
    }

    /* ==========================
       RESOURCE METHODS (READ-ONLY)
       ========================== */

    @McpResource(uri = "users://list", name = "List all users")
    public List<User> listUsersResource() {
        log.info("McpResource called: users://list");
        return userRepository.findAll();
    }

    @McpResource(uri = "users://count-active", name = "Count active users")
    public long countActiveUsersResource() {
        log.info("McpResource called: users://count-active");
        return userRepository.countByActiveTrue();
    }

    @McpResource(uri = "users://by-email/{email}", name = "Get user by email")
    public User getUserByEmailResource(@McpToolParam(description = "User email") String email) {
        log.info("McpResource called: users://by-email/{}", email);
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}
