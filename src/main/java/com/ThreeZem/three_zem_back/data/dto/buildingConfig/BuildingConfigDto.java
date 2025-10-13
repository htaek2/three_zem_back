package com.ThreeZem.three_zem_back.data.dto.buildingConfig;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
/// 빌딩 설정 파일(.json)로부터 데이터를 저장할 때 사용
public class BuildingConfigDto {
    private UUID id;
    private String buildingName;
    private String address;
    private double totalArea;
    private List<FloorConfigDto> floors;
}
