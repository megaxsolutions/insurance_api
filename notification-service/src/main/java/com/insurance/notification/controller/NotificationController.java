package com.insurance.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody Map<String, String> notificationRequest) {
        String to = notificationRequest.get("to");
        String subject = notificationRequest.get("subject");
        String message = notificationRequest.get("message");

        Map<String, String> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Notification sent to " + to);
        response.put("notificationId", System.currentTimeMillis() + "");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{notificationId}")
    public ResponseEntity<Map<String, String>> getNotificationStatus(@PathVariable String notificationId) {
        Map<String, String> response = new HashMap<>();
        response.put("notificationId", notificationId);
        response.put("status", "DELIVERED");
        response.put("timestamp", System.currentTimeMillis() + "");

        return ResponseEntity.ok(response);
    }
}
