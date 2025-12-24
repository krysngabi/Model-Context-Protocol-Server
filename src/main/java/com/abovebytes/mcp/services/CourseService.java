package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.Course;
import com.abovebytes.mcp.repositories.CourseRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /* =============================
       RESOURCE METHODS
       ============================= */

    @McpResource(uri = "courses://list", name = "All Courses")
    public List<Course> listCoursesResource() {
        log.info("McpResource called: courses://list");
        return courseRepository.findAll();
    }

    @McpResource(uri = "courses://count", name = "Total number of courses")
    public long countCoursesResource() {
        log.info("McpResource called: courses://count");
        return courseRepository.count();
    }

    @McpResource(uri = "courses://by-title/{title}", name = "Course by exact title")
    public Course getCourseByTitleResource(@McpToolParam(description = "Exact title") String title) {
        log.info("McpResource called: courses://by-title/{}", title);
        return courseRepository.findByCourseNameIgnoreCase(title).orElse(null);
    }

    @McpResource(uri = "courses://search-description/{text}", name = "Search course by description")
    public Course searchByDescriptionResource(@McpToolParam(description = "Text to search") String text) {
        log.info("McpResource called: courses://search-description/{}", text);
        return courseRepository.findFirstByDescriptionContainingIgnoreCase(text).orElse(null);
    }

    /* =============================
       TOOL METHODS
       ============================= */

    @McpTool(name = "courses_add", description = "Add a new course to the catalog")
    public Course addCourse(@McpToolParam(description = "Course title") String title,
                            @McpToolParam(description = "Public URL of the course") String url) {
        log.info("McpTool called: courses_add | title={}, url={}", title, url);
        Course course = Course.builder()
                .courseName(title)
                .courseUrl(url)
                .active(true)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        return courseRepository.save(course);
    }

    @McpTool(name = "courses_delete_by_title", description = "Delete a course by its exact title")
    public String deleteCourseByTitle(@McpToolParam(description = "Exact title") String title) {
        log.info("McpTool called: courses_delete_by_title | title={}", title);
        List<Course> toDelete = courseRepository.findAll()
                .stream()
                .filter(c -> c.getCourseName().equalsIgnoreCase(title))
                .toList();
        if (toDelete.isEmpty()) return "No course found with title '" + title + "'";
        courseRepository.deleteAll(toDelete);
        return "Course(s) with title '" + title + "' successfully deleted";
    }

    @McpTool(name = "courses_update_url", description = "Update the URL of an existing course by title")
    public Course updateCourseUrl(@McpToolParam(description = "Exact title") String title,
                                  @McpToolParam(description = "New URL") String newUrl) {
        log.info("McpTool called: courses_update_url | title={}, newUrl={}", title, newUrl);
        Course course = courseRepository.findByCourseNameIgnoreCase(title).orElse(null);
        if (course == null) return null;
        course.setCourseUrl(newUrl);
        return courseRepository.save(course);
    }

    @McpTool(name = "courses_update_title", description = "Update the title of an existing course")
    public Course updateCourseTitle(@McpToolParam(description = "Current title") String title,
                                    @McpToolParam(description = "New title") String newTitle) {
        log.info("McpTool called: courses_update_title | title={}, newTitle={}", title, newTitle);
        Course course = courseRepository.findByCourseNameIgnoreCase(title).orElse(null);
        if (course == null) return null;
        course.setCourseName(newTitle);
        return courseRepository.save(course);
    }

    @McpTool(name = "courses_health", description = "Health check for course MCP service")
    public String health() {
        log.info("McpTool called: courses_health");
        return "OK - Course MCP service is running. Total courses: " + courseRepository.count();
    }

    /* =============================
       INITIALIZATION
       ============================= */

    @PostConstruct
    public void init() {
        if (courseRepository.count() == 0) {
            log.info("Initializing database with default courses");
        }
    }
}
