package com.example.backend.repository;

import com.example.backend.model.NoContactRelapse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoContactRelapseRepository extends JpaRepository<NoContactRelapse, Long> {
    List<NoContactRelapse> findByUserIdOrderByRelapsedAtDesc(Long userId);
    long countByUserId(Long userId);
}
