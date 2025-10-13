package com.ThreeZem.three_zem_back.data.dto.buildingConfig;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
/// 빌딩 설정 파일(.json)로부터 데이터를 저장할 때 사용
public class FloorConfigDto {
    private Long id;
    private int floorNum;
    private List<DeviceConfigDto> devices;
}
