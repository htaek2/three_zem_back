package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.data.constant.CarbonEmissionConst;
import com.ThreeZem.three_zem_back.data.constant.EnergyPriceConst;
import com.ThreeZem.three_zem_back.data.dto.energy.EnergyReadingDto;
import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import com.ThreeZem.three_zem_back.data.entity.ElectricityReading;
import com.ThreeZem.three_zem_back.data.entity.GasReading;
import com.ThreeZem.three_zem_back.data.entity.WaterReading;
import com.ThreeZem.three_zem_back.data.enums.DateTimeType;
import com.ThreeZem.three_zem_back.data.enums.EnergyType;
import com.ThreeZem.three_zem_back.repository.ElectricityReadingRepository;
import com.ThreeZem.three_zem_back.repository.FloorRepository;
import com.ThreeZem.three_zem_back.repository.GasReadingRepository;
import com.ThreeZem.three_zem_back.repository.WaterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private final FloorRepository floorRepository;

    /// 전력 데이터를 년/월/일/시별로 조회한다
    public EnergyReadingDto getElecRangeData(String start, String end, byte datetimeType) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endTime = LocalDateTime.parse(end, formatter);
            DateTimeType dateTimeType = DateTimeType.fromByte(datetimeType);

            List<ElectricityReading> result = electricityReadingRepository.findByReadingTimeBetween(startTime, endTime);

            if (result.isEmpty()) {
                log.info("No data found");
                return null;
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
                return null;
            }

            EnergyReadingDto energyReadingDto = new EnergyReadingDto();
            energyReadingDto.setEnergyType(EnergyType.ELECTRICITY);
            energyReadingDto.setDatas(aggregatedData);

            log.info("[INFO] 전력 데이터 호출 완료");
            return energyReadingDto;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// 가스 데이터를 년/월/일/시별로 조회한다
    public EnergyReadingDto getGasRangeData(String start, String end, byte datetimeType) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        DateTimeType dateTimeType = DateTimeType.fromByte(datetimeType);

        List<GasReading> result = gasReadingRepository.findByReadingTimeBetween(startTime, endTime);

        if (result.isEmpty()) {
            return null;
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
            return null;
        }

        EnergyReadingDto energyReadingDto = new EnergyReadingDto();
        energyReadingDto.setEnergyType(EnergyType.GAS);
        energyReadingDto.setDatas(aggregatedData);

        return energyReadingDto;
    }

    /// 수도 데이터를 년/월/일/시별로 조회한다
    public EnergyReadingDto getWaterRangeData(String start, String end, byte datetimeType) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        DateTimeType dateTimeType = DateTimeType.fromByte(datetimeType);

        List<WaterReading> result = waterReadingRepository.findByReadingTimeBetween(startTime, endTime);

        if (result.isEmpty()) {
            return null;
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
            return null;
        }

        EnergyReadingDto energyReadingDto = new EnergyReadingDto();
        energyReadingDto.setEnergyType(EnergyType.WATER);
        energyReadingDto.setDatas(aggregatedData);

        return energyReadingDto;
    }

    public List<EnergyReadingDto> getCarbonRangeData(String start, String end, byte datetimeType) {

        EnergyReadingDto elecDatas = getElecRangeData(start, end, datetimeType);
        EnergyReadingDto gasDatas = getGasRangeData(start, end, datetimeType);
        EnergyReadingDto waterDatas = getWaterRangeData(start, end, datetimeType);

        List<EnergyReadingDto> result = new ArrayList<>();

        result.add(new EnergyReadingDto(EnergyType.ELECTRICITY, aggregateByCoefficient(CarbonEmissionConst.AMOUNT_CARBON_ELECTRICITY, elecDatas)));
        result.add(new EnergyReadingDto(EnergyType.GAS, aggregateByCoefficient(CarbonEmissionConst.AMOUNT_CARBON_GAS, gasDatas)));
        result.add(new EnergyReadingDto(EnergyType.WATER, aggregateByCoefficient(CarbonEmissionConst.AMOUNT_CARBON_WATER, waterDatas)));

        return result;
    }

    public List<EnergyReadingDto> getBillRangeData(String start, String end, byte datetimeType) {

        EnergyReadingDto elecDatas = getElecRangeData(start, end, datetimeType);
        EnergyReadingDto gasDatas = getGasRangeData(start, end, datetimeType);
        EnergyReadingDto waterDatas = getWaterRangeData(start, end, datetimeType);

        List<EnergyReadingDto> result = new ArrayList<>();

        result.add(new EnergyReadingDto(EnergyType.ELECTRICITY, aggregateByCoefficient(EnergyPriceConst.UNIT_PRICE_ELECTRICITY, elecDatas)));
        result.add(new EnergyReadingDto(EnergyType.GAS, aggregateByCoefficient(EnergyPriceConst.UNIT_PRICE_GAS, gasDatas)));
        result.add(new EnergyReadingDto(EnergyType.WATER, aggregateByCoefficient(EnergyPriceConst.UNIT_PRICE_WATER, waterDatas)));

        return result;
    }

    private List<ReadingDto> aggregateByCoefficient(float coefficient, EnergyReadingDto energyReadings) {

        if (energyReadings == null || energyReadings.getDatas() == null) {
            return new ArrayList<>();
        }

        // 각 데이터에 배출량 곱하고
        LocalDateTime date = LocalDateTime.now().minusYears(1000);  // 데이터가 없을 시간대로 초기화
        List<ReadingDto> resultList = new ArrayList<>();
        ReadingDto aggregatedData = null;

        for (ReadingDto readingDto : energyReadings.getDatas()) {

            // 시간이 다르면 리스트에 넣고 컨테이너 초기화
            if (!date.equals(readingDto.getTimestamp()) || aggregatedData == null) {
                if (aggregatedData != null) {
                    resultList.add(aggregatedData);
                }
                date = readingDto.getTimestamp();

                aggregatedData = new ReadingDto();
                aggregatedData.setTimestamp(date);
                aggregatedData.setUsage(0f); // 사용량 초기화
            }

            aggregatedData.setUsage(aggregatedData.getUsage() + (readingDto.getUsage() * coefficient));
        }

        // 마지막 데이터 추가
        if (aggregatedData != null) {
            resultList.add(aggregatedData);
        }

        resultList.sort(Comparator.comparing(ReadingDto::getTimestamp));

        return resultList;
    }

    /// 층별 전력 사용량 조회
    public EnergyReadingDto getFloorElecRangeData(String floor, String start, String end, byte datetimeType) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endTime = LocalDateTime.parse(end, formatter);
            DateTimeType dateTimeType = DateTimeType.fromByte(datetimeType);
            int floorNumber = Integer.parseInt(floor);

            List<ElectricityReading> result = electricityReadingRepository.findByDevice_Floor_FloorNumAndReadingTimeBetween(floorNumber, startTime, endTime);

            if (result.isEmpty()) {
                log.info("[ERROR] 해당 층에 대한 데이터가 없습니다. {}", floor);
                return null;
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
                return null;
            }

            EnergyReadingDto energyReadingDto = new EnergyReadingDto();
            energyReadingDto.setEnergyType(EnergyType.ELECTRICITY);
            energyReadingDto.setDatas(aggregatedData);

            log.info("[INFO] 층별 전력 데이터 호출 완료. floor: {}", floor);
            return energyReadingDto;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// 층별 수도 사용량 조회
    public EnergyReadingDto getFloorWaterRangeData(String floor, String start, String end, byte datetimeType) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endTime = LocalDateTime.parse(end, formatter);
            DateTimeType dateTimeType = DateTimeType.fromByte(datetimeType);
            int floorNumber = Integer.parseInt(floor);

            List<WaterReading> result = waterReadingRepository.findByFloor_FloorNumAndReadingTimeBetween(floorNumber, startTime, endTime);

            if (result.isEmpty()) {
                log.info("No data found for floor {}", floor);
                return null;
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
                return null;
            }

            EnergyReadingDto energyReadingDto = new EnergyReadingDto();
            energyReadingDto.setEnergyType(EnergyType.WATER);
            energyReadingDto.setDatas(aggregatedData);

            log.info("[INFO] 층별 수도 데이터 호출 완료. floor: {}", floor);
            return energyReadingDto;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// 층별 전력 사용금액 조회
    public EnergyReadingDto getFloorElecBillData(String floor, String start, String end, byte datetimeType) {
        EnergyReadingDto elecDatas = getFloorElecRangeData(floor, start, end, datetimeType);
        return new EnergyReadingDto(EnergyType.ELECTRICITY, aggregateByCoefficient(EnergyPriceConst.UNIT_PRICE_ELECTRICITY, elecDatas));
    }

    /// 층별 수도 사용금액 조회
    public EnergyReadingDto getFloorWaterBillData(String floor, String start, String end, byte datetimeType) {
        EnergyReadingDto waterDatas = getFloorWaterRangeData(floor, start, end, datetimeType);
        return new EnergyReadingDto(EnergyType.WATER, aggregateByCoefficient(EnergyPriceConst.UNIT_PRICE_WATER, waterDatas));
    }
}
