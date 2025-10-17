package com.ThreeZem.three_zem_back.data.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillStatDto {

    private Integer numOfBuildings;
    private Long rowPriceTop5Per;
    private Long avgPrice;

}
