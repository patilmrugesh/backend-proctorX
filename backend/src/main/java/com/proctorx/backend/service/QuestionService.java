package com.proctorx.backend.service;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.entity.QuestionDSA;
import com.proctorx.backend.repository.ContestRepository;
import com.proctorx.backend.repository.QuestionDSARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDSARepository questionDSARepository;

    @Autowired
    private ContestRepository contestRepository;

    public QuestionDSA addDSAQuestion(Long contestId, QuestionDSA question) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        question.setContest(contest);
        return questionDSARepository.save(question);
    }

    public List<QuestionDSA> getQuestionsByContest(Long contestId) {
        return questionDSARepository.findByContest_ContestId(contestId);
    }
}