package com.ThreeZem.three_zem_back.data.dto.building;

import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeviceDto {

    private Long deviceId;
    private String deviceName;
    private int floorNum;
    private DeviceType deviceType;
    private LocalDateTime installedTime;
    private DeviceStatus status;

}
