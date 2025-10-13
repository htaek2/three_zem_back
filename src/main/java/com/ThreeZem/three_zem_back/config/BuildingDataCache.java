package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.data.dto.buildingConfig.BuildingConfigDto;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.service.DataGenerationService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class BuildingDataCache {

    private final BuildingRepository buildingRepository;
    private final DataGenerationService dataGenerationService;
//    private final BuildingConfigDto buildingConfigDto;

    public void init() {

    }
}
