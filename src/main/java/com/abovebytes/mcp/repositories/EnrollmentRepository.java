package com.abovebytes.mcp.repositories;

import com.abovebytes.mcp.entities.Enrollment;
import com.abovebytes.mcp.entities.User;
import com.abovebytes.mcp.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByTeacher(User teacher);
}
