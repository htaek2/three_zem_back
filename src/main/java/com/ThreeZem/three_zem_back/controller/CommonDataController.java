package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.service.CommonDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommonDataController {

    private final CommonDataService commonDataService;

    /// 빌딩 목록 조회
    @GetMapping("/api/data/buildings")
    public ResponseEntity<List<BuildingDto>> getBuildings() {
        return commonDataService.getBuildings();
    }

}
