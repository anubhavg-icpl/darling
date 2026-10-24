package com.example.backend.dto;

import com.example.backend.model.AppMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeModeDto {
    @NotNull(message = "The new application mode is required (REMEMBER or MOVE_ON)")
    private AppMode mode;
}
