package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.Enrollment;
import com.abovebytes.mcp.entities.User;
import com.abovebytes.mcp.entities.Course;
import com.abovebytes.mcp.repositories.EnrollmentRepository;
import com.abovebytes.mcp.repositories.UserRepository;
import com.abovebytes.mcp.repositories.CourseRepository;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /* ==========================
   ENROLLMENT TOOLS (READ)
   ========================== */

    @McpTool(name = "enrollments_list", description = "Return all enrollments")
    public List<Enrollment> listEnrollmentsTool() {
        log.info("McpTool called: enrollments_list");
        return enrollmentRepository.findAll();
    }

    @McpTool(name = "enrollments_by_student", description = "Return enrollments for a given student email")
    public List<Enrollment> getByStudentTool(@McpToolParam(description = "Student email") String studentEmail) {
        log.info("McpTool called: enrollments_by_student | email={}", studentEmail);
        User student = userRepository.findByEmailIgnoreCase(studentEmail).orElse(null);
        if (student == null) return List.of();
        return enrollmentRepository.findByStudent(student);
    }

    @McpTool(name = "enrollments_by_teacher", description = "Return enrollments for a given teacher email")
    public List<Enrollment> getByTeacherTool(@McpToolParam(description = "Teacher email") String teacherEmail) {
        log.info("McpTool called: enrollments_by_teacher | email={}", teacherEmail);
        User teacher = userRepository.findByEmailIgnoreCase(teacherEmail).orElse(null);
        if (teacher == null) return List.of();
        return enrollmentRepository.findByTeacher(teacher);
    }

    @McpTool(name = "enrollments_by_course", description = "Return enrollments for a given course title")
    public List<Enrollment> getByCourseTool(@McpToolParam(description = "Course title") String courseTitle) {
        log.info("McpTool called: enrollments_by_course | title={}", courseTitle);
        Course course = courseRepository.findByCourseNameIgnoreCase(courseTitle).orElse(null);
        if (course == null) return List.of();
        return enrollmentRepository.findByCourse(course);
    }
}
