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
public class WaterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer waterReadingId;

    @Column
    private Integer floorId;

    @Column
    private Float value;

    @Column
    private Timestamp readingTime;

}
