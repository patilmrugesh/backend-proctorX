package com.proctorx.backend.controller;

import com.proctorx.backend.entity.QuestionDSA;
import com.proctorx.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // Add a DSA Question to a Contest
    @PostMapping("/dsa/add")
    public ResponseEntity<?> addDSAQuestion(@RequestParam Long contestId, @RequestBody QuestionDSA question) {
        try {
            QuestionDSA savedQuestion = questionService.addDSAQuestion(contestId, question);
            return ResponseEntity.ok(savedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all questions for a specific contest
    @GetMapping("/contest/{contestId}")
    public ResponseEntity<List<QuestionDSA>> getContestQuestions(@PathVariable Long contestId) {
        return ResponseEntity.ok(questionService.getQuestionsByContest(contestId));
    }
}