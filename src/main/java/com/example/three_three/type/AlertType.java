package com.example.three_three.type;

public enum AlertType {
    OUT_PATTERN(1),
    ;

    private int value;

    AlertType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
