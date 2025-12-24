package com.abovebytes.mcp.services;

import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentPrompts {

    @McpPrompt(
        name = "enroll_student_prompt",
        description = """
            Given a student email, a teacher email, and a course title,
            enroll the student in the course with the specified teacher.
            If any of them don't exist, return a clear message.
        """
    )
    public String enrollStudentPrompt(String studentEmail, String teacherEmail, String courseTitle) {
        // MCP server will use this as a natural-language style tool
        return "Enroll student " + studentEmail + " in course " + courseTitle + " with teacher " + teacherEmail;
    }
}
