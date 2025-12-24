package com.abovebytes.mcp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Teacher
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // Course
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDateTime enrolledAt;
    private boolean active;
}
