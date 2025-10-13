package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.building.BuildingDto;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonDataService {

    private final BuildingRepository buildingRepository;

    public ResponseEntity<List<BuildingDto>> getBuildings() {

        List<Building> result = buildingRepository.findAll();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        List<BuildingDto> buildings = new ArrayList<>();

        for (Building building : result) {
            buildings.add(new BuildingDto(building.getId(), building.getBuildingName(), building.getAddress(), building.getTotalArea()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(buildings);
    }
}
