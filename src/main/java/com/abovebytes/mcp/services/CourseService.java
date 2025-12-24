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


    @McpTool(name = "courses_list", description = "Return the complete list of available courses with title and URL")
    public List<Course> listCourses() {
        log.info("McpTool called: courses_list");
        return courseRepository.findAll();
    }

    @McpTool(name = "courses_get_by_title", description = "Find a course by its exact title")
    public Course getCourseByTitle(@McpToolParam(description = "Exact course title") String title) {
        log.info("McpTool called: courses_get_by_title | title={}", title);
        return courseRepository
                .findByCourseNameIgnoreCase(title)
                .orElse(null);
    }

    @McpResource(uri = "config://{key}", name = "Configuration for key")
    public String getConfig(String key) {
        return "I got the config for " + key + " which is ISO";
    }

    @McpTool(
            name = "courses_search_by_description",
            description = "Find a course whose description contains the given text"
    )
    public Course searchByDescription(
            @McpToolParam(description = "Text contained in the course description") String text
    ) {
        log.info("McpTool called: courses_search_by_description | text={}", text);
        return courseRepository
                .findFirstByDescriptionContainingIgnoreCase(text)
                .orElse(null);
    }


    @McpTool(name = "courses_count", description = "Return the total number of available courses")
    public long countCourses() {
        log.info("McpTool called: courses_count");
        return courseRepository.count();
    }

    /* ======================================================
       WRITE OPERATIONS
       ====================================================== */

    @McpTool(name = "courses_add", description = "Add a new course to the catalog")
    public Course addCourse(
            @McpToolParam(description = "Course title") String title,
            @McpToolParam(description = "Public URL of the course") String url
    ) {
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
    public String deleteCourseByTitle(@McpToolParam(description = "Exact title of the course to delete") String title) {
        log.info("McpTool called: courses_delete_by_title | title={}", title);
        List<Course> toDelete = courseRepository.findAll()
                .stream()
                .filter(c -> c.getCourseName().equalsIgnoreCase(title))
                .toList();

        if (toDelete.isEmpty()) return "No course found with title '" + title + "'";
        courseRepository.deleteAll(toDelete);
        return "Course(s) with title '" + title + "' successfully deleted";
    }

    @McpTool(name = "courses_update_url", description = "Update the URL of an existing course identified by title")
    public Course updateCourseUrl(
            @McpToolParam(description = "Exact title of the course") String title,
            @McpToolParam(description = "New URL for the course") String newUrl
    ) {
        log.info("McpTool called: courses_update_url | title={}, newUrl={}", title, newUrl);

        Course course = getCourseByTitle(title);
        if (course == null) return null;

        course.setCourseUrl(newUrl);
        return courseRepository.save(course);
    }

    @McpTool(name = "courses_update_title", description = "Update the title of an existing course identified by its current title")
    public Course updateCourseTitle(
            @McpToolParam(description = "Current exact title of the course") String title,
            @McpToolParam(description = "New title to assign to the course") String newTitle
    ) {
        log.info("McpTool called: courses_update_title | title={}, newTitle={}", title, newTitle);

        Course course = getCourseByTitle(title);
        if (course == null) return null;

        course.setCourseName(newTitle);
        return courseRepository.save(course);
    }

    @McpTool(name = "courses_health", description = "Health check for the course MCP service")
    public String health() {
        log.info("McpTool called: courses_health");
        return "OK - Course MCP service is running. Total courses: " + courseRepository.count();
    }

    /* ======================================================
       INITIALIZATION
       ====================================================== */

    @PostConstruct
    public void init() {
        if (courseRepository.count() == 0) {
            log.info("Initializing database with default courses");
        }
    }
}
