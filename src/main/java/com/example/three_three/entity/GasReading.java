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
public class GasReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gasReadingId;

    @Column
    private int buildingId;

    @Column
    private float value;

    @Column
    private Timestamp readingTime;

}
