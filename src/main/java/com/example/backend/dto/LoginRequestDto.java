package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "The email is required")
    @Email(message = "You must provide a valid email")
    private String email;

    @NotBlank(message = "The password is required")
    private String password;
}
