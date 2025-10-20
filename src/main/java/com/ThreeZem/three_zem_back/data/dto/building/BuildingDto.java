package com.ThreeZem.three_zem_back.data.dto.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BuildingDto {

    private Long buildingId;
    private String buildingName;
    private String address;
    private float totalArea;

}
