package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "The name is required")
    @Size(min = 2, max = 50, message = "The name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "The email is required")
    @Email(message = "You must provide a valid email address format")
    private String email;

    @NotBlank(message = "The password is required")
    @Size(min = 6, message = "The password must be at least 6 characters")
    private String password;

    private String exName;
    private String exProfilePhoto;
}
