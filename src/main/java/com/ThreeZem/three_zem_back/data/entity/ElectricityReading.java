package com.ThreeZem.three_zem_back.data.entity;

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
public class ElectricityReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "value", nullable = false)
    private Float value;

    @Column(name = "reading_time", nullable = false)
    private LocalDateTime readingTime;

    public ElectricityReading(Device device, LocalDateTime readingTime, float value) {
        this.device = device;
        this.readingTime = readingTime;
        this.value = value;
    }
}