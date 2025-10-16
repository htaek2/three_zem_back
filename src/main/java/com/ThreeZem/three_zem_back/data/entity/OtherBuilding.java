package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtherBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String buildingName;
    @Column
    private Integer locationCode;
    @Column
    private Integer usagePeople;
    @Column
    private Integer numOfLowPowerDevices;
    @Column
    private Integer numOfMidPowerDevices;
    @Column
    private Integer numOfHighPowerDevices;
    @Column
    private Integer numOfWaterUseSpot;
    @Column
    private Integer numOfGasUseSpot;

}
