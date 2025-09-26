package com.example.three_three.type;

public enum DeviceType {
    COM(1),
    COND(2),
    LIGHT(3),
    ;

    private int value;

    private DeviceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
