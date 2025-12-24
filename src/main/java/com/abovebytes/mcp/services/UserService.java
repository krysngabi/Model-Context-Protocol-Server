package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.User;
import com.abovebytes.mcp.models.RoleValue;
import com.abovebytes.mcp.repositories.UserRepository;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
/* =============================
   USER TOOLS (READ)
   ============================= */

    @McpTool(name = "users_list", description = "Return all users")
    public List<User> listUsersTool() {
        log.info("McpTool called: users_list");
        return userRepository.findAll();
    }

    @McpTool(name = "users_count_active", description = "Return the total number of active users")
    public long countActiveUsersTool() {
        log.info("McpTool called: users_count_active");
        return userRepository.countByActiveTrue();
    }

    @McpTool(name = "users_get_by_email", description = "Get a user by email")
    public User getUserByEmailTool(@McpToolParam(description = "User email") String email) {
        log.info("McpTool called: users_get_by_email | email={}", email);
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @McpTool(name = "users_get_by_id", description = "Get a user by ID")
    public User getUserByIdTool(@McpToolParam(description = "User ID") Long id) {
        log.info("McpTool called: users_get_by_id | id={}", id);
        return userRepository.findById(id).orElse(null);
    }

}