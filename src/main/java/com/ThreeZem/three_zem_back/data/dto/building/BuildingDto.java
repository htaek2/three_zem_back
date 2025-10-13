package com.ThreeZem.three_zem_back.data.dto.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BuildingDto {
    private UUID buildingId;
    private String buildingName;
    private String address;
    private float totalArea;

    public void setBuildingId(String buildingId){
        try {
            this.buildingId = UUID.fromString(buildingId);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setBuildingId(UUID buildingId){
        this.buildingId = buildingId;
    }
}
