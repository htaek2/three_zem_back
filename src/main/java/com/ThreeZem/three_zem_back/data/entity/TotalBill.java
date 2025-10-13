package com.ThreeZem.three_zem_back.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
public class TotalBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(name = "gas_fee", nullable = false)
    private Integer gasFee;

    @Column(name = "water_fee", nullable = false)
    private Integer waterFee;

    @Column(name = "elec_fee", nullable = false)
    private Integer elecFee;

    @Column(name = "calc_time", nullable = false)
    private LocalDateTime calcTime;

    public TotalBill() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setGasFee(Integer gasFee) {
        this.gasFee = gasFee;
    }

    public void setWaterFee(Integer waterFee) {
        this.waterFee = waterFee;
    }

    public void setElecFee(Integer elecFee) {
        this.elecFee = elecFee;
    }

    public void setCalcTime(LocalDateTime calcTime) {
        this.calcTime = calcTime;
    }
}
