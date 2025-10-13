package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
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

    public Device() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public void setInstalledTime(LocalDateTime installedTime) {
        this.installedTime = installedTime;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void setDeviceName(String deviceName){
        this.deviceName = deviceName;
    }
}