package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(name = "alert_type", nullable = false)
    private Byte alertType;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    public Alert() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public void setAlertType(Byte alertType) {
        this.alertType = alertType;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
