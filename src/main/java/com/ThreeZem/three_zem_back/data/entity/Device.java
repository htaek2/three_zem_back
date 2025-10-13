package com.ThreeZem.three_zem_back.data.entity;

import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
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

    public Device(Floor floor, DeviceConfigDto deviceConfigDto) {
        this.floor = floor;
        this.deviceName = deviceConfigDto.getDeviceName();
        this.deviceType = deviceConfigDto.getDeviceType().getValue();
        this.installedTime = LocalDateTime.parse(deviceConfigDto.getInstalledTime(), TimeUtil.getDateTimeFormatter());
        this.status = DeviceStatus.DEVICE_OFF.getValue();
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