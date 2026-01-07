package com.proctorx.backend.controller;

import com.proctorx.backend.entity.User;
import com.proctorx.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow React frontend to connect
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userService.loginUser(email, password);

        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("role", user.getRole());
            response.put("userId", user.getUserId());
            response.put("credits", user.getWalletCredits());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }
}