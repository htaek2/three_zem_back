package com.ThreeZem.three_zem_back.data.dto;

import com.ThreeZem.three_zem_back.data.entity.Alert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertDto {

    private Long id;
    private Long relatedFloorId;
    private byte alertType;
    private LocalDateTime createdTime;
    private String message;
    private boolean isRead;

    public AlertDto(Alert alert) {
        this.id = alert.getId();
        this.relatedFloorId = alert.getFloor().getId();
        this.alertType = alert.getAlertType();
        this.createdTime = alert.getCreatedTime();
        this.message = alert.getMessage();
        this.isRead = alert.getIsRead();
    }
}
