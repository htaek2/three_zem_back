package com.example.three_three.type;

public enum DeviceStatus {
    DEVICE_OFFLINE(0),
    DEVICE_OFF(1),
    DEVICE_ON(2),
    ;

    private int value;

    private DeviceStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
