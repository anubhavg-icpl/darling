package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.event.UserRegisteredEvent;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.AppMode;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final DtoMapper dtoMapper;

    private static final String GOOGLE_CLIENT_ID = "962526435206-8gnivbn68h8ntc10e9k6epaoe68h0o42.apps.googleusercontent.com";

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("The email is already registered: " + request.getEmail());
        }

        User newUser = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .currentMode(AppMode.REMEMBER)
                .exName(request.getExName() != null ? request.getExName() : "My Ex")
                .exProfilePhoto(request.getExProfilePhoto())
                .noContactStartDate(LocalDateTime.now())
                .registeredAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(newUser);

        // Fire an async event for the welcome / confirmation email
        eventPublisher.publishEvent(new UserRegisteredEvent(this, saved));

        String accessToken = jwtService.generateAccessToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(dtoMapper.toUserResponseDto(saved))
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid credentials: wrong email or password");
        }

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(dtoMapper.toUserResponseDto(user))
                .build();
    }

    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String email = jwtService.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("The user associated with the token does not exist"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(dtoMapper.toUserResponseDto(user))
                .build();
    }

    @Transactional
    public AuthResponseDto loginWithGoogle(String tokenGoogle) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenGoogle);

            if (idToken == null) {
                throw new UnauthorizedException("Invalid or expired Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail().toLowerCase().trim();
            String name = (String) payload.get("name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .name(name != null ? name : "Google User")
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role(Role.ROLE_USER)
                        .currentMode(AppMode.REMEMBER)
                        .noContactStartDate(LocalDateTime.now())
                        .registeredAt(LocalDateTime.now())
                        .build();
                User registered = userRepository.save(newUser);
                eventPublisher.publishEvent(new UserRegisteredEvent(this, registered));
                return registered;
            });

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(dtoMapper.toUserResponseDto(user))
                .build();

        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error authenticating with Google: {}", e.getMessage());
            throw new UnauthorizedException("Verification with Google servers failed: " + e.getMessage());
        }
    }
}
