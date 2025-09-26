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
public class TotalPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int totalPriceId;

    @Column
    private int buildingId;

    @Column
    private int gasPrice;

    @Column
    private int waterPrice;

    @Column
    private int elecPrice;

    @Column
    private Timestamp pricingTime;

}
