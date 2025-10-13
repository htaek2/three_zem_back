package com.ThreeZem.three_zem_back.data.dto.buildingConfig;

import com.ThreeZem.three_zem_back.data.entity.Floor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
/// 빌딩 설정 파일(.json)로부터 데이터를 저장할 때 사용
public class FloorConfigDto {
    private Long id;
    private int floorNum;
    private List<DeviceConfigDto> devices;

    public FloorConfigDto(Floor floor, List<DeviceConfigDto> devices) {
        this.id = floor.getId();
        this.floorNum = floor.getFloorNum();
        this.devices = devices;
    }
}
