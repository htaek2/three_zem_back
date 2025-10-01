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
    private Integer elecConsumReadingId;

    @Column
    private Integer deviceId;

    @Column
    private Float value;

    @Column
    private Timestamp readingTime;

}
