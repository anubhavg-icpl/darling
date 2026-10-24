package com.example.backend.repository;

import com.example.backend.model.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findByUserIdOrderByCreatedAtDesc(Long userId);
}
