package com.proctorx.backend.controller;

import com.proctorx.backend.entity.QuestionDSA;
import com.proctorx.backend.entity.QuestionMCQ;
import com.proctorx.backend.entity.TestCase;
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

    // --- DSA Endpoints ---
    @PostMapping("/dsa/add")
    public ResponseEntity<?> addDSAQuestion(@RequestParam Long contestId, @RequestBody QuestionDSA question) {
        try {
            QuestionDSA savedQuestion = questionService.addDSAQuestion(contestId, question);
            return ResponseEntity.ok(savedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/dsa/contest/{contestId}")
    public ResponseEntity<List<QuestionDSA>> getContestDSAQuestions(@PathVariable Long contestId) {
        return ResponseEntity.ok(questionService.getQuestionsByContest(contestId));
    }

    @PostMapping("/cases/add")
    public ResponseEntity<?> addTestCase(@RequestParam Long questionId, @RequestBody TestCase testCase) {
        try {
            TestCase savedCase = questionService.addTestCase(questionId, testCase);
            return ResponseEntity.ok(savedCase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- MCQ Endpoints (NEW) ---
    @PostMapping("/mcq/add")
    public ResponseEntity<?> addMCQQuestion(@RequestParam Long contestId, @RequestBody QuestionMCQ question) {
        try {
            QuestionMCQ savedQuestion = questionService.addMCQQuestion(contestId, question);
            return ResponseEntity.ok(savedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mcq/contest/{contestId}")
    public ResponseEntity<List<QuestionMCQ>> getContestMCQQuestions(@PathVariable Long contestId) {
        return ResponseEntity.ok(questionService.getMCQQuestionsByContest(contestId));
    }
}