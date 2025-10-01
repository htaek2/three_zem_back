package com.example.three_three.repository;

import com.example.three_three.entity.TotalPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TotalPriceRepository extends JpaRepository<TotalPrice, Integer> {
}
