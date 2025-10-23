package com.ThreeZem.three_zem_back.data.dto.buildingConfig;

import com.ThreeZem.three_zem_back.data.entity.Device;
import com.ThreeZem.three_zem_back.data.enums.DeviceStatus;
import com.ThreeZem.three_zem_back.data.enums.DeviceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
/// 빌딩 설정 파일(.json)로부터 데이터를 저장할 때 사용
public class DeviceConfigDto {
    private Long id;
    private String deviceName;
    private DeviceType deviceType;
    private String installedTime;
    private DeviceStatus status;
    private Double x;
    private Double y;
    private Double z;

    public DeviceConfigDto(Device device) {
        this.id = device.getId();
        this.deviceName = device.getDeviceName();
        this.deviceType = DeviceType.fromByte(device.getDeviceType());
        this.status = DeviceStatus.fromByte(device.getStatus());
    }
}
