package com.ThreeZem.three_zem_back.data.dto.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceUpdateResponseDto {
    private Long deviceId;
    private byte status;
}
