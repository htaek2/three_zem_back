package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@ToString
@Getter
@Setter
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "building_name", nullable = false, length = 45)
    private String buildingName;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "total_area", nullable = false)
    private Float totalArea;
}
