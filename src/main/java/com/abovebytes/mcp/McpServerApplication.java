package com.abovebytes.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.abovebytes.mcp")
@EntityScan(basePackages = "com.abovebytes.mcp.entities")
@EnableJpaRepositories
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

//	@Bean
//	public List<ToolCallback> mcpTools(      // ← single bean name, can be anything
//											 CourseService courseService,
//											 UserService userService
//	) {
//		ToolCallback courseCallback = ToolCallbacks.from(courseService);
//		return List.of(
//				ToolCallbacks.from(courseService),
//				ToolCallbacks.from(userService)
//				// Add more here later: ToolCallbacks.from(paymentService), etc.
//		);
//	}
}
