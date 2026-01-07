package com.proctorx.backend.controller;

import com.proctorx.backend.dto.ProctorEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Controller
@CrossOrigin(origins = "*")
public class ProctoringController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Student sends update here: /app/contest/{contestId}/alert
    @MessageMapping("/contest/{contestId}/alert")
    public void handleAlert(@DestinationVariable Long contestId, @Payload ProctorEvent event) {

        // Add server-side timestamp
        event.setTimestamp(LocalDateTime.now().toString());
        event.setContestId(contestId);

        System.out.println("ALERT RECEIVED: " + event.getType() + " from " + event.getUsername());

        // Broadcast to Judges watching this contest
        // Judges subscribe to: /topic/contest/{contestId}/judge
        messagingTemplate.convertAndSend("/topic/contest/" + contestId + "/judge", event);
    }
}