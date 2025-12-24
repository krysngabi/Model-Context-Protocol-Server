package com.abovebytes.mcp.entities;

import com.abovebytes.mcp.models.Level;
import com.abovebytes.mcp.models.Provider;
import com.abovebytes.mcp.models.RoleValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(
        name = "course",
        indexes = {
                @Index(name = "idx_course_name", columnList = "course_name"),
                @Index(name = "idx_course_active", columnList = "active")
        }
)
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditingEntityListener.class)
public class Course implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_url", nullable = false)
    private String courseUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "language", length = 10)
    private String language; // e.g. en, fr

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "rating")
    private Double rating; // 0.0 → 5.0

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

