package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.ResponseMessage;
import com.ThreeZem.three_zem_back.data.dto.AlertDto;
import com.ThreeZem.three_zem_back.data.entity.Alert;
import com.ThreeZem.three_zem_back.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AlertRepository alertRepository;

    public ResponseEntity<List<AlertDto>> getAlerts() {
        List<Alert> alerts = alertRepository.findAll();

        if (alerts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<AlertDto> alertDtos = new ArrayList<>();
        for (Alert alert : alerts) {
            alertDtos.add(new AlertDto(alert));
        }

        return ResponseEntity.status(HttpStatus.OK).body(alertDtos);
    }

    public ResponseEntity<String> readAlert(Long id) {
        Optional<Alert> alert = alertRepository.findById(id);
        if (alert.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        alert.get().setIsRead(true);
        alertRepository.save(alert.get());

        return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
    }
}
