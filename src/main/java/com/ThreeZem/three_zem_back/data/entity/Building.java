package com.ThreeZem.three_zem_back.data.entity;

import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.DeviceConfigDto;
import com.ThreeZem.three_zem_back.data.dto.buildingConfig.FloorConfigDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
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

    public Building(Member member, BuildingConfigDto buildingConfigDto) {
        this.member = member;
        this.buildingName = buildingConfigDto.getBuildingName();
        this.address = buildingConfigDto.getAddress();
        this.totalArea = buildingConfigDto.getTotalArea();
    }

}
