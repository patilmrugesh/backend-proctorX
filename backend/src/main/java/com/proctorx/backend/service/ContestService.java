package com.proctorx.backend.service;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.entity.User;
import com.proctorx.backend.repository.ContestRepository;
import com.proctorx.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContestService {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional // Ensures credit deduction and contest creation happen together
    public Contest createContest(Long adminId, Contest contest) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // 1. Check Credits
        if (admin.getWalletCredits() < 1) {
            throw new RuntimeException("Insufficient credits! Please top up.");
        }

        // 2. Deduct Credit
        admin.setWalletCredits(admin.getWalletCredits() - 1);
        userRepository.save(admin);

        // 3. Generate Unique Tokens (Taking first 6 chars of a UUID)
        String sToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String jToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        contest.setStudentToken(sToken);
        contest.setJudgeToken(jToken);
        contest.setAdmin(admin);
        contest.setStatus("DRAFT");

        // 4. Save Contest
        return contestRepository.save(contest);
    }

    public List<Contest> getContestsByAdmin(Long adminId) {
        return contestRepository.findByAdmin_UserId(adminId);
    }
}