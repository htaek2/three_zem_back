package com.ThreeZem.three_zem_back.data.dto.building;

import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.data.entity.Floor;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {

    private Long deviceId;
    private String deviceName;
    private int floorNum;
    private byte deviceType;
    private LocalDateTime installedTime;
    private byte status;

    public Device toEntity(Floor floor) {
        return new Device(this.deviceId, this.deviceName, floor, this.deviceType, this.installedTime, this.status);
    }
}
