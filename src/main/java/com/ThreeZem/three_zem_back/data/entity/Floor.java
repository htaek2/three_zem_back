package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Entity
@ToString
@Getter
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "floor_num", nullable = false)
    private Integer floorNum;

    public Floor() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setFloorNum(Integer floorNum) {
        this.floorNum = floorNum;
    }
}