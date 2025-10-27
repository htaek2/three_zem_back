package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.repository.ElectricityReadingRepository;
import com.ThreeZem.three_zem_back.repository.GasReadingRepository;
import com.ThreeZem.three_zem_back.repository.WaterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {

    private final ElectricityReadingRepository electricityReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final WaterReadingRepository waterReadingRepository;

    public String predictBill() {
        try {


            return null;
        }
        catch (Exception e) {
            log.error("[ERORR] ");
            return null;
        }
    }

    public String predictCarbon() {

        try {

            return null;
        }
        catch (Exception e) {
            log.error("[ERORR] ");
            return null;
        }
    }
}
