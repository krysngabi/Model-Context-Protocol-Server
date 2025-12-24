package com.abovebytes.mcp.repositories;

import com.abovebytes.mcp.entities.Course;
import com.abovebytes.mcp.models.Level;
import com.abovebytes.mcp.models.Provider;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseNameIgnoreCase(String courseName);

    Optional<Course> findFirstByDescriptionContainingIgnoreCase(String text);

    Optional<Course> findByCourseNameIgnoreCaseAndProviderAndLevel(String courseName, Provider provider, Level level);
}