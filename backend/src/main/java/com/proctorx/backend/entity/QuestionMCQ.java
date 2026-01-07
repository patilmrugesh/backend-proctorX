package com.proctorx.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "questions_mcq")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionMCQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mcq_id")
    private Long mcqId;

    @ManyToOne
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // Optional: For image-based questions (Future Scope)
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c")
    private String optionC;

    @Column(name = "option_d")
    private String optionD;

    // Stores 'A', 'B', 'C', or 'D'
    @Column(name = "correct_option", nullable = false, length = 1)
    private String correctOption;

    private Integer marks = 1;
}