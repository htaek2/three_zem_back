package com.ThreeZem.three_zem_back.repository;

import com.ThreeZem.three_zem_back.data.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
