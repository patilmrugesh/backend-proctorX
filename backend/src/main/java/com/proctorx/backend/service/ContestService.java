package com.proctorx.backend.service;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.entity.User;
import com.proctorx.backend.repository.ContestRepository;
import com.proctorx.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContestService {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Contest createContest(Long adminId, Contest contest) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getWalletCredits() < 1) {
            throw new RuntimeException("Insufficient credits! Please top up.");
        }

        admin.setWalletCredits(admin.getWalletCredits() - 1);
        userRepository.save(admin);

        String sToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String jToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        contest.setStudentToken(sToken);
        contest.setJudgeToken(jToken);
        contest.setAdmin(admin);
        contest.setStatus("DRAFT");

        return contestRepository.save(contest);
    }

    public List<Contest> getContestsByAdmin(Long adminId) {
        return contestRepository.findByAdmin_UserId(adminId);
    }

    // --- Deletion Logic ---
    public void deleteContest(Long contestId) {
        contestRepository.deleteById(contestId);
    }

    // --- Status Toggle Logic ---
    public Contest updateStatus(Long contestId, String status) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        contest.setStatus(status);
        return contestRepository.save(contest);
    }

    // --- CSV Upload Logic ---
    public Contest uploadAllowedEmails(Long contestId, MultipartFile file) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> emails = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String email = line.trim().toLowerCase();
                // Simple validation to ensure it looks like an email
                if (!email.isEmpty() && email.contains("@")) {
                    emails.add(email);
                }
            }
            contest.setAllowedEmails(emails);
            return contestRepository.save(contest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    // --- Student Join Logic with Email Verification ---
    public Contest joinContest(String token, String studentEmail) {
        Contest contest = contestRepository.findByStudentToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        // 1. Check if Contest is LIVE
        if (!"LIVE".equalsIgnoreCase(contest.getStatus())) {
            throw new RuntimeException("Contest is not active yet.");
        }

        // 2. Check Whitelist (if it exists)
        if (!contest.getAllowedEmails().isEmpty()) {
            if (!contest.getAllowedEmails().contains(studentEmail.trim().toLowerCase())) {
                throw new RuntimeException("Access Denied: Your email is not in the allowed list.");
            }
        }

        return contest;
    }

    // --- Judge Join Logic ---
    public Contest joinContestAsJudge(String token) {
        Contest contest = contestRepository.findByJudgeToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Judge Token"));

        // FIX: Check if Contest is LIVE
        if (!"LIVE".equalsIgnoreCase(contest.getStatus())) {
            throw new RuntimeException("Contest is not active yet.");
        }

        return contest;
    }

    public Optional<Contest> getContestByStudentToken(String token) {
        return contestRepository.findByStudentToken(token);
    }
}