package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.User;
import com.abovebytes.mcp.models.RoleValue;
import com.abovebytes.mcp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    @McpTool(name = "users_count_active", description = "Count active users")
    public long countActiveUsers() {
        log.info("McpTool called: users_count_active");
        return userRepository.countByActiveTrue();
    }

    /* ==========================
       RESOURCE (READ-ONLY)
       ========================== */

    @McpTool(
        name = "users_list",
        description = "Return a list of all users (read-only resource)"
    )
    public List<User> listUsers() {
        log.info("Resource called: users_list");
        return userRepository.findAll();
    }
}
