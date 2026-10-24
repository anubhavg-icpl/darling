package com.example.backend.repository;

import com.example.backend.model.AdConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdConfigRepository extends JpaRepository<AdConfig, Long> {
    Optional<AdConfig> findByKey(String key);
    List<AdConfig> findByActiveTrue();
}
