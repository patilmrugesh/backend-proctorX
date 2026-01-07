package com.proctorx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProctorEvent {
    private String type; // "TAB_SWITCH", "ONLINE", "DISCONNECT"
    private Long userId;
    private String username;
    private Long contestId;
    private String message; // e.g., "User switched tabs 5 times"
    private String timestamp;
}