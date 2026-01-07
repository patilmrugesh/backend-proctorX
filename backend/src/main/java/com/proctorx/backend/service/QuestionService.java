package com.proctorx.backend.service;

import com.proctorx.backend.entity.Contest;
import com.proctorx.backend.entity.QuestionDSA;
import com.proctorx.backend.entity.QuestionMCQ;
import com.proctorx.backend.entity.TestCase;
import com.proctorx.backend.repository.ContestRepository;
import com.proctorx.backend.repository.QuestionDSARepository;
import com.proctorx.backend.repository.QuestionMCQRepository;
import com.proctorx.backend.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDSARepository questionDSARepository;

    @Autowired
    private QuestionMCQRepository questionMCQRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    // --- DSA Logic ---
    public QuestionDSA addDSAQuestion(Long contestId, QuestionDSA question) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));
        question.setContest(contest);
        return questionDSARepository.save(question);
    }

    public List<QuestionDSA> getQuestionsByContest(Long contestId) {
        return questionDSARepository.findByContest_ContestId(contestId);
    }

    public TestCase addTestCase(Long questionId, TestCase testCase) {
        QuestionDSA question = questionDSARepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        testCase.setQuestionDSA(question);
        return testCaseRepository.save(testCase);
    }

    // --- MCQ Logic (NEW) ---
    public QuestionMCQ addMCQQuestion(Long contestId, QuestionMCQ question) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new RuntimeException("Contest not found"));

        // Basic validation
        if (!"A".equals(question.getCorrectOption()) &&
                !"B".equals(question.getCorrectOption()) &&
                !"C".equals(question.getCorrectOption()) &&
                !"D".equals(question.getCorrectOption())) {
            throw new RuntimeException("Correct option must be A, B, C, or D");
        }

        question.setContest(contest);
        return questionMCQRepository.save(question);
    }

    public List<QuestionMCQ> getMCQQuestionsByContest(Long contestId) {
        return questionMCQRepository.findByContest_ContestId(contestId);
    }
}