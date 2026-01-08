package com.proctorx.backend.service;

import com.proctorx.backend.dto.ExecutionRequest;
import com.proctorx.backend.dto.ExecutionResponse;
import com.proctorx.backend.entity.*;
import com.proctorx.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionService {

    @Autowired
    private DockerService dockerService;

    @Autowired
    private QuestionDSARepository questionRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContestRepository contestRepository;

    public ExecutionResponse runCode(ExecutionRequest request) {
        ExecutionResponse response = new ExecutionResponse();

        try {
            // 1. Fetch Data
            QuestionDSA question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            List<TestCase> testCases = testCaseRepository.findByQuestionDSA_QuestionId(request.getQuestionId());

            if (testCases.isEmpty()) {
                response.setVerdict("Error");
                response.setOutput("No test cases found for this problem.");
                return response;
            }

            int passed = 0;
            long totalTime = 0;

            // 2. Run against ALL Test Cases
            for (TestCase tc : testCases) {
                long start = System.currentTimeMillis();

                // Call Docker
                String actualOutput = dockerService.runCode(request.getCode(), request.getLanguage(), tc.getInputData());

                long end = System.currentTimeMillis();
                totalTime += (end - start);

                // Check for TLE or Errors
                if (actualOutput.startsWith("TLE:")) {
                    return saveAndReturn(request, "TLE", "Time Limit Exceeded", 0.0);
                }

                // Normalization: Trim whitespace to avoid " 5" != "5" errors
                String expected = tc.getExpectedOutput().trim();
                String actual = actualOutput.trim();

                if (!expected.equals(actual)) {
                    // Failed!
                    return saveAndReturn(request, "WA",
                            "Wrong Answer on Input:\n" + tc.getInputData() +
                                    "\n\nExpected:\n" + expected +
                                    "\n\nActual:\n" + actual,
                            (double)totalTime);
                }

                passed++;
            }

            // 3. If loop finishes, all passed!
            return saveAndReturn(request, "AC", "All Test Cases Passed!", (double)totalTime);

        } catch (Exception e) {
            e.printStackTrace();
            return saveAndReturn(request, "RE", "Runtime Error: " + e.getMessage(), 0.0);
        }
    }

    private ExecutionResponse saveAndReturn(ExecutionRequest req, String verdict, String output, Double time) {
        // Save to DB
        Submission sub = new Submission();
        sub.setCode(req.getCode());
        sub.setLanguage(req.getLanguage());
        sub.setVerdict(verdict);
        sub.setExecutionTime(time);
        sub.setSubmittedAt(LocalDateTime.now());

        // Link entities (optional for MVP speed, better validation in prod)
        if(req.getUserId() != null) sub.setUser(userRepository.findById(req.getUserId()).orElse(null));
        if(req.getContestId() != null) sub.setContest(contestRepository.findById(req.getContestId()).orElse(null));
        if(req.getQuestionId() != null) sub.setQuestion(questionRepository.findById(req.getQuestionId()).orElse(null));

        submissionRepository.save(sub);

        // Return Response
        ExecutionResponse res = new ExecutionResponse();
        res.setVerdict(verdict);
        res.setOutput(output);
        res.setExecutionTime(time);
        return res;
    }
}