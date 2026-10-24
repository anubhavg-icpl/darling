package com.example.backend.event;

import com.example.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppEventListener {

    private final EmailService emailService;

    @Async("taskExecutor")
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("[EVENT] New user registered: {} ({})", event.getUser().getName(), event.getUser().getEmail());
        emailService.sendWelcomeEmail(event.getUser().getEmail(), event.getUser().getName());
    }

    @Async("taskExecutor")
    @EventListener
    public void handleRelapseRecorded(RelapseRecordedEvent event) {
        log.info("[EVENT] Relapse recorded for user {}: {}", event.getUser().getEmail(), event.getRelapse().getReason());
        emailService.sendRelapseAlert(
                event.getUser().getEmail(),
                event.getUser().getName(),
                event.getRelapse().getReason()
        );
    }
}
