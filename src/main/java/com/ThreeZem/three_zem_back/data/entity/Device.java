package com.ThreeZem.three_zem_back.data.entity;

import com.ThreeZem.three_zem_back.data.dto.building.DeviceDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.util.TimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column
    private Double x;
    @Column
    private Double y;
    @Column
    private Double z;

    public Device(Floor floor, DeviceConfigDto deviceConfigDto) {
        this.floor = floor;
        this.deviceName = deviceConfigDto.getDeviceName();
        this.deviceType = deviceConfigDto.getDeviceType().getValue();
        this.installedTime = LocalDateTime.parse(deviceConfigDto.getInstalledTime(), TimeUtil.getDateTimeFormatter());
        this.status = DeviceStatus.DEVICE_OFF.getValue();
        this.x = deviceConfigDto.getX();
        this.y = deviceConfigDto.getY();
        this.z = deviceConfigDto.getZ();
    }

    public void setStatus(byte status) {
        if (0 <= status && status < DeviceStatus.values().length) {
            this.status = status;
        }
        else {
            throw new IllegalArgumentException("잘못된 타입 값");
        }
    }

    public DeviceDto toDto() {
        return new DeviceDto(this.id, this.deviceName, this.floor.getFloorNum(), this.deviceType, this.installedTime, this.status, this.x, this.y, this.z);
    }
}