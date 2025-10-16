package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.AlertDto;
import com.ThreeZem.three_zem_back.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/alerts")
    public ResponseEntity<List<AlertDto>> getAlerts() {
        return notificationService.getAlerts();
    }

    @PatchMapping("/api/alert/read/{id}")
    public ResponseEntity<String> readAlert(@PathVariable Long id) {
        return notificationService.readAlert(id);
    }

}
