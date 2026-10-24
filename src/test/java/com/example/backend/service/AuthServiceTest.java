package com.example.backend.service;

import com.example.backend.dto.AuthResponseDto;
import com.example.backend.dto.LoginRequestDto;
import com.example.backend.dto.RegisterRequestDto;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@nocontact.app")
                .name("Rick Sanchez")
                .password("encoded_pass")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Successful registration generates tokens and publishes the welcome event")
    void registrationGeneratesTokensAndPublishesWelcomeEvent() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setName("Rick Sanchez");
        request.setEmail("rick@c137.com");
        request.setPassword("Password123!");
        request.setExName("Diane");

        when(userRepository.existsByEmail("rick@c137.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_secret");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("fake_access_token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("fake_refresh_token");

        AuthResponseDto response = authService.register(request);

        assertNotNull(response);
        assertEquals("fake_access_token", response.getAccessToken());
        assertEquals("fake_refresh_token", response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("Registration with a duplicate email throws DuplicateResourceException")
    void registrationWithDuplicateEmailThrowsDuplicateResourceException() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("test@nocontact.app");

        when(userRepository.existsByEmail("test@nocontact.app")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login with invalid credentials throws UnauthorizedException")
    void loginWithInvalidCredentialsThrowsUnauthorizedException() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail("test@nocontact.app");
        loginRequest.setPassword("wrong_password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest));
    }
}
