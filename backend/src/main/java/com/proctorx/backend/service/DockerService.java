package com.proctorx.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    @Value("${proctorx.code.temp-dir}")
    private String tempDirBase;

    public String runCode(String code, String language, String input) throws IOException, InterruptedException {
        String folderName = UUID.randomUUID().toString();
        Path folderPath = Paths.get(tempDirBase, folderName);
        Files.createDirectories(folderPath);

        File codeFile;
        File inputFile = new File(folderPath.toFile(), "input.txt");

        if ("CPP".equalsIgnoreCase(language)) {
            codeFile = new File(folderPath.toFile(), "Solution.cpp");
        } else {
            codeFile = new File(folderPath.toFile(), "Solution.py");
        }

        Files.writeString(codeFile.toPath(), code);
        Files.writeString(inputFile.toPath(), input);

        String absolutePath = folderPath.toAbsolutePath().toString();
        ProcessBuilder builder = new ProcessBuilder();

        if ("PYTHON".equalsIgnoreCase(language)) {
            builder.command(
                    "docker", "run", "--rm",
                    "--network", "none",
                    "-v", absolutePath + ":/app",
                    "python:3.9-alpine",
                    "sh", "-c", "python /app/Solution.py < /app/input.txt"
            );
        } else if ("CPP".equalsIgnoreCase(language)) {
            builder.command(
                    "docker", "run", "--rm",
                    "--network", "none",
                    "-v", absolutePath + ":/app",
                    "gcc:latest",
                    "sh", "-c", "g++ -o /app/out /app/Solution.cpp && /app/out < /app/input.txt"
            );
        } else {
            return "Error: Unsupported Language";
        }

        builder.redirectErrorStream(true);
        Process process = builder.start();

        // FIX: Increased timeout to 15 seconds to allow Docker startup time on Windows
        boolean finished = process.waitFor(15, TimeUnit.SECONDS);

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

        deleteDirectory(folderPath.toFile());

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