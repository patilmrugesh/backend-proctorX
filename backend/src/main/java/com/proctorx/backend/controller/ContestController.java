package com.proctorx.backend.controller;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin(origins = "*")
public class ContestController {

    @Autowired
    private ContestService contestService;

    @PostMapping("/create")
    public ResponseEntity<?> createContest(@RequestParam Long adminId, @RequestBody Contest contest) {
        try {
            Contest newContest = contestService.createContest(adminId, contest);
            return ResponseEntity.ok(newContest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Contest>> getAdminContests(@PathVariable Long adminId) {
        return ResponseEntity.ok(contestService.getContestsByAdmin(adminId));
    }

    @DeleteMapping("/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable Long contestId) {
        contestService.deleteContest(contestId);
        return ResponseEntity.ok("Contest deleted successfully");
    }

    @PutMapping("/{contestId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long contestId, @RequestParam String status) {
        return ResponseEntity.ok(contestService.updateStatus(contestId, status));
    }

    @PostMapping("/{contestId}/upload-users")
    public ResponseEntity<?> uploadUsers(@PathVariable Long contestId, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(contestService.uploadAllowedEmails(contestId, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/join/{token}")
    public ResponseEntity<?> joinContest(@PathVariable String token, @RequestParam String email) {
        try {
            Contest contest = contestService.joinContest(token, email);
            return ResponseEntity.ok(contest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- THIS WAS LIKELY MISSING ---
    @GetMapping("/judge/join/{token}")
    public ResponseEntity<?> joinJudge(@PathVariable String token) {
        try {
            Contest contest = contestService.joinContestAsJudge(token);
            return ResponseEntity.ok(contest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}