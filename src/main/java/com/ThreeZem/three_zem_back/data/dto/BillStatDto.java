package com.ThreeZem.three_zem_back.data.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillStatDto {

    private Integer total;
    private Integer local;
    private List<Long> avgAll;
    private List<Long> avgLocal;

}
