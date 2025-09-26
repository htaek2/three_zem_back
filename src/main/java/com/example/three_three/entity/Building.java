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
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int buildingId;

    @Column
    private int memberId;

    @Column(length = 45)
    private String buildingName;

    @Column(length = 100)
    private String address;

    @Column
    private float totalArea;

}
