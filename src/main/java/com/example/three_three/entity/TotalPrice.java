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
    private Integer totalPriceId;

    @Column
    private Integer buildingId;

    @Column
    private Integer gasPrice;

    @Column
    private Integer waterPrice;

    @Column
    private Integer elecPrice;

    @Column
    private Timestamp pricingTime;

}
