package com.abovebytes.mcp.services;

import com.abovebytes.mcp.entities.Course;
import com.abovebytes.mcp.models.Level;
import com.abovebytes.mcp.models.Provider;
import com.abovebytes.mcp.repositories.CourseRepository;
import com.abovebytes.mcp.utils.CourseUtils;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
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
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /* =============================
       RESOURCE METHODS
       ============================= */

    /* =============================
   COURSE TOOLS (READ)
   ============================= */

    @McpTool(name = "courses_list", description = "Return all courses")
    public List<Course> listCoursesTool() {
        log.info("McpTool called: courses_list");
        return courseRepository.findAll();
    }

    @McpTool(name = "courses_count", description = "Return the total number of courses")
    public long countCoursesTool() {
        log.info("McpTool called: courses_count");
        return courseRepository.count();
    }

    @McpTool(name = "courses_get_by_id", description = "Get a course by its ID")
    public Course getCourseByIdTool(@McpToolParam(description = "Course ID") Long id) {
        log.info("McpTool called: courses_get_by_id | id={}", id);
        return courseRepository.findById(id).orElse(null);
    }

    @McpTool(name = "courses_get_by_title", description = "Get a course by its title")
    public Course getCourseByTitleTool(@McpToolParam(description = "Course title") String title) {
        log.info("McpTool called: courses_get_by_title | title={}", title);
        return courseRepository.findByCourseNameIgnoreCase(title).orElse(null);
    }

    @McpTool(name = "courses_search_by_description", description = "Search courses by description")
    public List<Course> searchByDescriptionTool(@McpToolParam(description = "Text to search") String text) {
        log.info("McpTool called: courses_search_by_description | text={}", text);
        return courseRepository.findFirstByDescriptionContainingIgnoreCase(text).stream().toList();
    }

    /* =============================
       TOOL METHODS
       ============================= */

    @McpTool(name = "courses_add", description = "Add a new course to the catalog")
    public Course addCourse(@McpToolParam(description = "Course title") String title,
                            @McpToolParam(description = "Course's description") String description,
                            @McpToolParam(description = "Course's provider") Provider provider,
                            @McpToolParam(description = "Course's level") Level level) {
        String url = CourseUtils.randomCourseUrl(provider.name());
        log.info("McpTool called: courses_add | title={}, url={}", title, url);

        Course found = courseRepository.findByCourseNameIgnoreCaseAndProviderAndLevel(title, provider, level).orElse(null);
        if (found != null) {
            throw new IllegalStateException(String.format(
                    "Cannot add course: A course with title '%s', provider '%s', and level '%s' already exists (ID: %d)",
                    found.getCourseName(),
                    found.getProvider(),
                    found.getLevel(),
                    found.getCourseId()
            ));
        }

        Course course = Course.builder()
                .courseName(title)
                .courseUrl(url)
                .provider(provider)
                .description(description)
                .durationMinutes(CourseUtils.randomDurationMinutes())
                .level(level)
                .rating(CourseUtils.randomRating())
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

/* Get course by title */
//@McpResource(uri = "courses://get-by-title/{title}", name = "Get course by title")
//public McpSchema.TextResourceContents getCourseByTitleResource(String title) {
//    return courseRepository.findByCourseNameIgnoreCase(title)
//            .map(course -> new McpSchema.TextResourceContents(
//                    "courses://get-by-title/" + course.getCourseId(),
//                    "application/json",
//                    String.format("{\"id\":%d,\"name\":\"%s\",\"url\":\"%s\",\"active\":%b}",
//                            course.getCourseId(),
//                            course.getCourseName(),
//                            course.getCourseUrl(),
//                            course.getActive())
//            ))
//            .orElse(new McpSchema.TextResourceContents(
//                    "courses://get-by-title/none",
//                    "application/json",
//                    "{}"
//            ));
//}
