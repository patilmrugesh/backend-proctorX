package com.proctorx.backend.controller;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin(origins = "*")
public class ContestController {

    @Autowired
    private ContestService contestService;

    // Create a new contest (Deducts 1 Credit)
    @PostMapping("/create")
    public ResponseEntity<?> createContest(@RequestParam Long adminId, @RequestBody Contest contest) {
        try {
            Contest newContest = contestService.createContest(adminId, contest);
            return ResponseEntity.ok(newContest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all contests for an Admin
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<Contest>> getAdminContests(@PathVariable Long adminId) {
        return ResponseEntity.ok(contestService.getContestsByAdmin(adminId));
    }
}