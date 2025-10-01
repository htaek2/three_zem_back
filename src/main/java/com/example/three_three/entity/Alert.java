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
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;

    @Column
    private Integer floorId;

    @Column(length = 20)
    private String alertType;

    @Column
    private Timestamp createdAt;

    @Column(columnDefinition = "TEXT")
    private String resolve;

}
