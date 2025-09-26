package com.example.three_three.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElecConsumReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int elecConsumReadingId;

    @Column
    private int deviceId;

    @Column
    private float value;

    @Column
    private Timestamp readingTime;

}
