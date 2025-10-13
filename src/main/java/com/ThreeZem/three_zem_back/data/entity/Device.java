package com.ThreeZem.three_zem_back.data.entity;

import com.ThreeZem.three_zem_back.data.dto.building.DeviceDto;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(name = "device_type", nullable = false)
    private byte deviceType;

    @Column(name = "installed_time", nullable = false)
    private LocalDateTime installedTime;

    @Column(name = "status", nullable = false)
    private byte status;

    public DeviceDto toDto() {
        return new DeviceDto(this.id, this.deviceName, this.floor.getFloorNum(), this.deviceType, this.installedTime, this.status);
    }

    public void setStatus(byte status) {
        if (0 <= status && status < DeviceStatus.values().length) {
            this.status = status;
        }
        else {
            throw new IllegalArgumentException("잘못된 타입 값");
        }
    }
}