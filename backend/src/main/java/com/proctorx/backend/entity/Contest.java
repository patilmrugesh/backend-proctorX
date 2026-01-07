package com.proctorx.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private User admin;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String type; // 'DSA', 'MCQ', 'HYBRID'

    private String status = "DRAFT"; // 'DRAFT', 'LIVE', 'ENDED'

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

    // --- NEW: Whitelist for Emails ---
    @ElementCollection
    @CollectionTable(name = "contest_allowed_emails", joinColumns = @JoinColumn(name = "contest_id"))
    @Column(name = "email")
    private List<String> allowedEmails = new ArrayList<>();
}