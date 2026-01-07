package com.proctorx.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "questions_dsa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDSA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    // Link this question to a specific Contest
    @ManyToOne
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(nullable = false)
    private String title;

    @Column(name = "problem_statement", nullable = false, columnDefinition = "TEXT")
    private String problemStatement;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    private String difficulty; // 'EASY', 'MEDIUM', 'HARD'

    @Column(name = "time_limit_sec")
    private Double timeLimitSec = 1.0;

    @Column(name = "memory_limit_mb")
    private Integer memoryLimitMb = 256;
}