package com.ThreeZem.three_zem_back.data.dto.buildingConfig;

import com.ThreeZem.three_zem_back.data.entity.Building;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
/// 빌딩 설정 파일(.json)로부터 데이터를 저장할 때 사용
public class BuildingConfigDto {
    private Long id;
    private String buildingName;
    private String address;
    private float totalArea;
    private List<FloorConfigDto> floors;

    public BuildingConfigDto(Building building, List<FloorConfigDto> floors) {
        this.id = building.getId();
        this.buildingName = building.getBuildingName();
        this.address = building.getAddress();
        this.totalArea = building.getTotalArea();
        this.floors = floors;
    }
}
