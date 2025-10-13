package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.data.dto.energy.RangeDataRequestDto;
import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import com.ThreeZem.three_zem_back.data.entity.ElectricityReading;
import com.ThreeZem.three_zem_back.data.entity.GasReading;
import com.ThreeZem.three_zem_back.data.entity.WaterReading;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.ElectricityReadingRepository;
import com.ThreeZem.three_zem_back.repository.GasReadingRepository;
import com.ThreeZem.three_zem_back.repository.WaterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnergyDataService {

    private final ElectricityReadingRepository electricityReadingRepository;
    private final GasReadingRepository gasReadingRepository;
    private final WaterReadingRepository waterReadingRepository;

    /// 전력 데이터를 년/월/일/시별로 조회한다
    public ResponseEntity<EnergyReadingDto> getElecRangeData(RangeDataRequestDto rangeDataRequestDto) {

        LocalDateTime start = rangeDataRequestDto.getStart();
        LocalDateTime end = rangeDataRequestDto.getEnd();
        DateTimeType dateTimeType = DateTimeType.fromByte(rangeDataRequestDto.getReadingTarget());

        List<ElectricityReading> result = electricityReadingRepository.findByReadingTimeBetween(start, end);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ReadingDto> aggregatedData;

        if (dateTimeType == DateTimeType.HOUR) {

            Map<LocalDateTime, Double> hourlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().truncatedTo(ChronoUnit.HOURS),
                            Collectors.summingDouble(ElectricityReading::getValue)
                    ));

            aggregatedData = hourlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.DAY) {

            Map<LocalDate, Double> dailySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().toLocalDate(),
                            Collectors.summingDouble(ElectricityReading::getValue)
                    ));

            aggregatedData = dailySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.MONTH) {

            Map<YearMonth, Double> monthlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> YearMonth.from(er.getReadingTime()),
                            Collectors.summingDouble(ElectricityReading::getValue)
                    ));

            aggregatedData = monthlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.YEAR) {

            Map<Integer, Double> yearlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().getYear(),
                            Collectors.summingDouble(ElectricityReading::getValue)
                    ));

            aggregatedData = yearlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(Year.of(entry.getKey()).atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        }
        else {
            log.info("[Error] 잘못된 데이터 타입");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EnergyReadingDto energyReadingDto = new EnergyReadingDto();
        energyReadingDto.setEnergyType(EnergyType.ELECTRICITY);
        energyReadingDto.setDatas(aggregatedData);

        return ResponseEntity.status(HttpStatus.OK).body(energyReadingDto);
    }

    /// 가스 데이터를 년/월/일/시별로 조회한다
    public ResponseEntity<EnergyReadingDto> getGasRangeData(RangeDataRequestDto rangeDataRequestDto) {

        LocalDateTime start = rangeDataRequestDto.getStart();
        LocalDateTime end = rangeDataRequestDto.getEnd();
        DateTimeType dateTimeType = DateTimeType.fromByte(rangeDataRequestDto.getReadingTarget());

        List<GasReading> result = gasReadingRepository.findByReadingTimeBetween(start, end);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ReadingDto> aggregatedData;

        if (dateTimeType == DateTimeType.HOUR) {

            Map<LocalDateTime, Double> hourlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().truncatedTo(ChronoUnit.HOURS),
                            Collectors.summingDouble(GasReading::getValue)
                    ));

            aggregatedData = hourlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.DAY) {

            Map<LocalDate, Double> dailySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().toLocalDate(),
                            Collectors.summingDouble(GasReading::getValue)
                    ));

            aggregatedData = dailySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.MONTH) {

            Map<YearMonth, Double> monthlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> YearMonth.from(er.getReadingTime()),
                            Collectors.summingDouble(GasReading::getValue)
                    ));

            aggregatedData = monthlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.YEAR) {

            Map<Integer, Double> yearlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().getYear(),
                            Collectors.summingDouble(GasReading::getValue)
                    ));

            aggregatedData = yearlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(Year.of(entry.getKey()).atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        }
        else {
            log.info("[Error] 잘못된 데이터 타입");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EnergyReadingDto energyReadingDto = new EnergyReadingDto();
        energyReadingDto.setEnergyType(EnergyType.GAS);
        energyReadingDto.setDatas(aggregatedData);

        return ResponseEntity.status(HttpStatus.OK).body(energyReadingDto);
    }

    /// 수도 데이터를 년/월/일/시별로 조회한다
    public ResponseEntity<EnergyReadingDto> getWaterRangeData(RangeDataRequestDto rangeDataRequestDto) {

        LocalDateTime start = rangeDataRequestDto.getStart();
        LocalDateTime end = rangeDataRequestDto.getEnd();
        DateTimeType dateTimeType = DateTimeType.fromByte(rangeDataRequestDto.getReadingTarget());

        List<WaterReading> result = waterReadingRepository.findByReadingTimeBetween(start, end);

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ReadingDto> aggregatedData;

        if (dateTimeType == DateTimeType.HOUR) {

            Map<LocalDateTime, Double> hourlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().truncatedTo(ChronoUnit.HOURS),
                            Collectors.summingDouble(WaterReading::getValue)
                    ));

            aggregatedData = hourlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.DAY) {

            Map<LocalDate, Double> dailySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().toLocalDate(),
                            Collectors.summingDouble(WaterReading::getValue)
                    ));

            aggregatedData = dailySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.MONTH) {

            Map<YearMonth, Double> monthlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> YearMonth.from(er.getReadingTime()),
                            Collectors.summingDouble(WaterReading::getValue)
                    ));

            aggregatedData = monthlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(entry.getKey().atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        } else if (dateTimeType == DateTimeType.YEAR) {

            Map<Integer, Double> yearlySums = result.stream()
                    .collect(Collectors.groupingBy(
                            er -> er.getReadingTime().getYear(),
                            Collectors.summingDouble(WaterReading::getValue)
                    ));

            aggregatedData = yearlySums.entrySet().stream()
                    .map(entry -> new ReadingDto(Year.of(entry.getKey()).atDay(1).atStartOfDay(), (float) entry.getValue().doubleValue()))
                    .sorted(Comparator.comparing(ReadingDto::getTimestamp))
                    .collect(Collectors.toList());

        }
        else {
            log.info("[Error] 잘못된 데이터 타입");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        EnergyReadingDto energyReadingDto = new EnergyReadingDto();
        energyReadingDto.setEnergyType(EnergyType.WATER);
        energyReadingDto.setDatas(aggregatedData);

        return ResponseEntity.status(HttpStatus.OK).body(energyReadingDto);
    }
}
