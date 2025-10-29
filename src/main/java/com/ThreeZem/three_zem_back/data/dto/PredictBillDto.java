package com.ThreeZem.three_zem_back.data.dto;

import com.ThreeZem.three_zem_back.data.dto.energy.ReadingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictBillDto {

    private List<ReadingDto> month;
    private List<ReadingDto> year;

}
