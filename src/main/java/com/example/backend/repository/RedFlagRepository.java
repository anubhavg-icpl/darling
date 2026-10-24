package com.example.backend.repository;

import com.example.backend.model.RedFlag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RedFlagRepository extends JpaRepository<RedFlag, Long> {
    List<RedFlag> findByUserIdOrderBySeverityDesc(Long userId);
}
