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
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceId;

    @Column
    private Integer floorId;

    @Column(length = 20)
    private String deviceType;

    @Column
    private Timestamp installedTime;

    @Column(length = 20)
    private String status;

}
