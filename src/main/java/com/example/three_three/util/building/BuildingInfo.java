package com.example.three_three.util.building;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BuildingInfo {
    private String name;
    private int totalFloors;
    private Map<Integer, FloorInfo> floors;
}
