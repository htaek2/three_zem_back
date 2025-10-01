package com.example.three_three.service;

import com.example.three_three.dto.RealTimeDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealTimeDataService {

    private final MockDataService mockDataService;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String BUILDING_ID = "1"; // Mock building ID

    @Scheduled(fixedRate = 10000) // 10 seconds
    public void sendRealTimeData() {
        RealTimeDataDto data = mockDataService.generateRealTimeData();
        messagingTemplate.convertAndSend("/topic/energy/" + BUILDING_ID, data);
    }
}
