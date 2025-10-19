package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.CarbonEmissionConst;
import com.ThreeZem.three_zem_back.data.constant.EnergyPriceConst;
import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class EnergyDataServiceTest {

    @Spy
    @InjectMocks
    private EnergyDataService energyDataService;

    private String start;
    private String end;
    private byte datetimeType;
    private EnergyReadingDto mockElecDto;
    private EnergyReadingDto mockGasDto;
    private EnergyReadingDto mockWaterDto;

    @BeforeEach
    void setUp() {
        start = "2023-01-01 00:00:00";
        end = "2023-01-01 01:00:00";
        datetimeType = DateTimeType.HOUR.getValue();

        LocalDateTime timestamp = LocalDateTime.of(2023, 1, 1, 0, 0);

        mockElecDto = new EnergyReadingDto(EnergyType.ELECTRICITY, Arrays.asList(new ReadingDto(timestamp, 100f)));
        mockGasDto = new EnergyReadingDto(EnergyType.GAS, Arrays.asList(new ReadingDto(timestamp, 50f)));
        mockWaterDto = new EnergyReadingDto(EnergyType.WATER, Arrays.asList(new ReadingDto(timestamp, 10f)));
    }

    @Test
    @DisplayName("탄소 배출량 계산 테스트")
    void getCarbonRangeData_Success() {
        // given
        doReturn(mockElecDto).when(energyDataService).getElecRangeData(anyString(), anyString(), anyByte());
        doReturn(mockGasDto).when(energyDataService).getGasRangeData(anyString(), anyString(), anyByte());
        doReturn(mockWaterDto).when(energyDataService).getWaterRangeData(anyString(), anyString(), anyByte());

        // when
        List<EnergyReadingDto> result = energyDataService.getCarbonRangeData(start, end, datetimeType);

        // then
        assertEquals(3, result.size());

        // Elec
        EnergyReadingDto elecResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.ELECTRICITY).findFirst().get();
        float expectedElecCarbon = 100f * CarbonEmissionConst.AMOUNT_CARBON_ELECTRICITY;
        assertEquals(expectedElecCarbon, elecResult.getDatas().get(0).getUsage(), 0.01);

        // Gas
        EnergyReadingDto gasResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.GAS).findFirst().get();
        float expectedGasCarbon = 50f * CarbonEmissionConst.AMOUNT_CARBON_GAS;
        assertEquals(expectedGasCarbon, gasResult.getDatas().get(0).getUsage(), 0.01);

        // Water
        EnergyReadingDto waterResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.WATER).findFirst().get();
        float expectedWaterCarbon = 10f * CarbonEmissionConst.AMOUNT_CARBON_WATER;
        assertEquals(expectedWaterCarbon, waterResult.getDatas().get(0).getUsage(), 0.01);
    }

    @Test
    @DisplayName("에너지 요금 계산 테스트")
    void getBillRangeData_Success() {
        // given
        doReturn(mockElecDto).when(energyDataService).getElecRangeData(anyString(), anyString(), anyByte());
        doReturn(mockGasDto).when(energyDataService).getGasRangeData(anyString(), anyString(), anyByte());
        doReturn(mockWaterDto).when(energyDataService).getWaterRangeData(anyString(), anyString(), anyByte());

        // when
        List<EnergyReadingDto> result = energyDataService.getBillRangeData(start, end, datetimeType);

        // then
        assertEquals(3, result.size());

        // Elec
        EnergyReadingDto elecResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.ELECTRICITY).findFirst().get();
        float expectedElecBill = 100f * EnergyPriceConst.UNIT_PRICE_ELECTRICITY;
        assertEquals(expectedElecBill, elecResult.getDatas().get(0).getUsage(), 0.01);

        // Gas
        EnergyReadingDto gasResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.GAS).findFirst().get();
        float expectedGasBill = 50f * EnergyPriceConst.UNIT_PRICE_GAS;
        assertEquals(expectedGasBill, gasResult.getDatas().get(0).getUsage(), 0.01);

        // Water
        EnergyReadingDto waterResult = result.stream().filter(r -> r.getEnergyType() == EnergyType.WATER).findFirst().get();
        float expectedWaterBill = 10f * EnergyPriceConst.UNIT_PRICE_WATER;
        assertEquals(expectedWaterBill, waterResult.getDatas().get(0).getUsage(), 0.01);
    }

    @Test
    void testest() {
        System.out.println(Math.floorMod(123, 10));
    }
}
