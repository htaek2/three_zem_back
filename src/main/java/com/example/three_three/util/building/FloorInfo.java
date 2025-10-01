package com.example.three_three.util.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FloorInfo {
    // 층 수
    private int floorNum;
    
    // 방 개수
    private int numOfRoom;

    // 최대 사용 인원
    private int maxOccupants;

    // 컴퓨터 + 모니터 세트 개수
    private int numOfComputerSets;

    // 냉난방 시스템 에어컨 개수
    private int numOfHvacs;

    // 조명 개수
    private int numOfLights;
}
