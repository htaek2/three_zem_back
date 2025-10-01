package com.example.three_three.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer buildingId;

    @Column
    private Integer memberId;

    @Column(length = 45)
    private String buildingName;

    @Column(length = 100)
    private String address;

    @Column
    private Float totalArea;

}
