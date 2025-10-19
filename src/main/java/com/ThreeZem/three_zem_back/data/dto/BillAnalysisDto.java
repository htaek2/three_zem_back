package com.ThreeZem.three_zem_back.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillAnalysisDto {

    private Integer total;
    private Long rowYearyPriceTop;
    private Long avgYearyPrice;
    private Long ourYearyPrice;
    private Float ourPercentage;

}
