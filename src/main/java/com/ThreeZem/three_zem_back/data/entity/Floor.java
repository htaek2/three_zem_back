package com.ThreeZem.three_zem_back.data.entity;

import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
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

    public Floor(Building building, FloorConfigDto floorConfigDto) {
        this.building = building;
        this.floorNum = floorConfigDto.getFloorNum();
    }

}