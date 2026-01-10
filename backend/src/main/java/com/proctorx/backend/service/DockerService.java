package com.proctorx.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    @Value("${proctorx.code.temp-dir}")
    private String tempDirBase;

    // CONSTANT: Time taken for Docker to boot up on Windows/WSL (adjust as needed)
    private static final int DOCKER_STARTUP_BUFFER = 10;

    // --- UPDATED SIGNATURE: Added 'timeLimitSec' ---
    public String runCode(String code, String language, String inputData, Double timeLimitSec) throws IOException, InterruptedException {
        String folderName = UUID.randomUUID().toString();
        Path folderPath = Paths.get(tempDirBase, folderName);
        Files.createDirectories(folderPath);

        File codeFile;
        if ("CPP".equalsIgnoreCase(language)) {
            codeFile = new File(folderPath.toFile(), "Solution.cpp");
        } else if ("JAVA".equalsIgnoreCase(language)) {
            codeFile = new File(folderPath.toFile(), "Main.java");
        } else {
            codeFile = new File(folderPath.toFile(), "Solution.py");
        }
        Files.writeString(codeFile.toPath(), code);

        String absolutePath = folderPath.toAbsolutePath().toString();
        ProcessBuilder builder = new ProcessBuilder();

        if ("PYTHON".equalsIgnoreCase(language)) {
            builder.command("docker", "run", "-i", "--rm", "-v", absolutePath + ":/app", "python:3.9-alpine", "python", "/app/Solution.py");
        } else if ("CPP".equalsIgnoreCase(language)) {
            builder.command("docker", "run", "-i", "--rm", "-v", absolutePath + ":/app", "gcc:latest", "sh", "-c", "g++ -o /app/out /app/Solution.cpp && /app/out");
        } else if ("JAVA".equalsIgnoreCase(language)) {
            builder.command("docker", "run", "-i", "--rm", "-v", absolutePath + ":/app", "openjdk:17-alpine", "sh", "-c", "javac /app/Main.java && java -cp /app Main");
        } else {
            return "Error: Unsupported Language";
        }

        builder.redirectErrorStream(true);
        Process process = builder.start();

        if (inputData != null) {
            try (OutputStream os = process.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                writer.write(inputData);
                writer.flush();
            }
        }

        // --- DYNAMIC TIMEOUT LOGIC ---
        // If question limit is 2.0s, we wait 12.0s total to account for Docker startup.
        // In a real Linux server deployment, DOCKER_STARTUP_BUFFER can be reduced to 0 or 1.
        long totalWaitTime = (long) Math.ceil(timeLimitSec + DOCKER_STARTUP_BUFFER);

        boolean finished = process.waitFor(totalWaitTime, TimeUnit.SECONDS);

        if (!finished) {
            process.destroy();
            return "TLE: Time Limit Exceeded";
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        try { deleteDirectory(folderPath.toFile()); } catch (Exception e) {}

        return output.toString().trim();
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}