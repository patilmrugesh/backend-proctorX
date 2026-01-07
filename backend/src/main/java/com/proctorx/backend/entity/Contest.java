package com.proctorx.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "contests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contest_id")
    private Long contestId;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin; // Links to the User table

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String type; // 'DSA', 'MCQ', 'HYBRID'

    private String status = "DRAFT"; // 'DRAFT', 'LIVE', 'ENDED'

    // Tokens for access
    @Column(name = "student_token", unique = true)
    private String studentToken;

    @Column(name = "judge_token", unique = true)
    private String judgeToken;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}