package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "memories")
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    private String photoUrl;

    private LocalDate memoryDate;

    @Builder.Default
    private int nostalgiaLevel = 5; // Scale 1 to 10

    @Builder.Default
    private String type = "PHOTO"; // PHOTO, LETTER, SONG, PLACE

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
