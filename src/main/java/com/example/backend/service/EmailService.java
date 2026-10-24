package com.example.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:contact@nocontact.app}")
    private String senderEmail;

    @Async("taskExecutor")
    public void sendWelcomeEmail(String recipient, String userName) {
        String subject = "Welcome to No Contact & Moving On!";
        try {
            Context context = new Context();
            context.setVariable("userName", userName != null ? userName : "User");

            String htmlContent = templateEngine.process("email-welcome", context);
            sendHtml(recipient, subject, htmlContent);
        } catch (Exception e) {
            log.error("Error processing Thymeleaf template for welcome email: {}", e.getMessage());
            // Resilient fallback so the user still gets a message if the template fails
            String fallbackHtml = String.format("<h2>Hello %s</h2><p>Welcome to No Contact.</p>", userName);
            sendHtml(recipient, subject, fallbackHtml);
        }
    }

    @Async("taskExecutor")
    public void sendRelapseAlert(String recipient, String userName, String reason) {
        String subject = "Relapse Alert: Don't give up, a stumble is not the end";
        try {
            Context context = new Context();
            context.setVariable("userName", userName != null ? userName : "User");
            context.setVariable("reason", reason != null ? reason : "Emotional trigger not specified");

            String htmlContent = templateEngine.process("email-relapse", context);
            sendHtml(recipient, subject, htmlContent);
        } catch (Exception e) {
            log.error("Error processing Thymeleaf template for relapse email: {}", e.getMessage());
            // Resilient fallback so the user still gets a message if the template fails
            String fallbackHtml = String.format("<h2>Relapse Alert</h2><p>Hello %s, reason: %s</p>", userName, reason);
            sendHtml(recipient, subject, fallbackHtml);
        }
    }

    private void sendHtml(String recipient, String subject, String htmlContent) {
        if (mailSender == null) {
            log.warn("[SIMULATED EMAIL] SMTP server not configured. Simulating send to: {}, Subject: {}", recipient, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", recipient);
        } catch (MessagingException e) {
            log.error("Error building or sending email to {}: {}", recipient, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected exception in the email service: {}", e.getMessage());
        }
    }
}
