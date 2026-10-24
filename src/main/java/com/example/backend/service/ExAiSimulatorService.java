package com.example.backend.service;

import com.example.backend.model.AppMode;
import com.example.backend.model.User;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ExAiSimulatorService {

    // Whole-word match so "hi" does not fire on "this" or "which".
    private static final Pattern GREETING = Pattern.compile("\\b(hi|hello|hey|good morning|good evening)\\b");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(800))
            .build();

    // URL where the PyTorch / Transformer microservice (inspired by JustZando) runs
    private final String PYTHON_AI_URL = "http://localhost:8000/predict";

    public String generateResponse(User user, String userMessage, AppMode mode) {
        // 1. Try the PyTorch/FastAPI model first if it is running
        try {
            String jsonPayload = String.format(
                    "{\"message\":\"%s\", \"mode\":\"%s\", \"personality\":\"%s\", \"datasetContext\":\"%s\"}",
                    escapeJson(userMessage),
                    mode.name(),
                    user.getExPersonality(),
                    escapeJson(user.getExTrainingDataset() != null ? user.getExTrainingDataset() : "")
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PYTHON_AI_URL))
                    .timeout(Duration.ofSeconds(2))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
                return response.body();
            }
        } catch (Exception ignored) {
            // The Python microservice is not running; fall back to the simulated heuristic
        }

        // 2. Simulation heuristic based on the MODE and the learned PERSONALITY
        if (mode == AppMode.MOVE_ON) {
            return generateMoveOnModeResponse(userMessage);
        } else {
            return generateExModeResponse(user, userMessage);
        }
    }

    private String generateExModeResponse(User user, String message) {
        String msgLower = message.toLowerCase();
        String personality = user.getExPersonality() != null ? user.getExPersonality() : "DISTANT";

        if (GREETING.matcher(msgLower).find()) {
            if (personality.contains("COLD")) {
                return randomChoice(List.of("Hi. What do you want?", "What happened? Tell me quickly.", "Hi. I'm busy."));
            }
            if (personality.contains("TOXIC")) {
                return randomChoice(List.of("Oh, so now you remember I exist?", "Why are you texting me at this hour? Always the same with you."));
            }
            List<String> replies = List.of(
                    "Hi... what's up? I thought we agreed to give each other space.",
                    "Hi. Is everything okay? I wasn't expecting a message from you.",
                    "Hi. I was just busy.",
                    "What happened? Tell me quickly, please."
            );
            return randomChoice(replies);
        }

        if (msgLower.contains("miss you") || msgLower.contains("come back")) {
            if (personality.contains("COLD")) {
                return randomChoice(List.of("Just get over me, please.", "I don't feel the same.", "That's your problem, not mine."));
            }
            List<String> replies = List.of(
                    "Please don't make this harder. We already talked about this.",
                    "I think about how things used to be too... but you know we were hurting each other.",
                    "Please don't write me things like this right now. We need to move forward.",
                    "It's too late for that. You had your chance."
            );
            return randomChoice(replies);
        }

        if (msgLower.contains("where are you") || msgLower.contains("who are you with")) {
            List<String> replies = List.of(
                    "That's really none of your business anymore.",
                    "Out with some friends. Please take care of yourself.",
                    "Why are you asking me that? We're nothing now."
            );
            return randomChoice(replies);
        }

        if (msgLower.contains("forgive") || msgLower.contains("apologize") || msgLower.contains("i'm sorry") || msgLower.contains("sorry")) {
            List<String> replies = List.of(
                    "I appreciate you saying that, but words don't change what happened.",
                    "It's okay, I forgive you. But that doesn't mean we can go back to how it was.",
                    "A lot of time has passed. I hope we both find peace."
            );
            return randomChoice(replies);
        }

        // Generic personality-conditioned replies
        if (personality.contains("COLD")) {
            return randomChoice(List.of("Ok.", "👍", "Read.", "I have nothing to say.", "Seriously, don't text me."));
        }

        List<String> generic = List.of(
                "Read. I don't know what to say to that.",
                "I hope things go well for you, really.",
                "Please take care of yourself. Don't look for me anymore.",
                "I have to go, we'll talk later (or maybe not).",
                "Ok.",
                "👍"
        );
        return randomChoice(generic);
    }

    private String generateMoveOnModeResponse(String message) {
        List<String> advice = List.of(
                "🛡️ [Move-On Assistant]: Breathe deeply. Remember why you decided to start no contact.",
                "💪 [Mental Strength]: The urge to text them only lasts about 15 minutes at its peak. Drink a glass of water and step away from the phone.",
                "🧠 [Reality]: You don't miss that person, you miss the dopamine and the illusion of what you hoped it would be. Review your Red Flags list.",
                "✨ [Progress]: Every hour you go without checking their profile is a day won for your future peace. You are in control!"
        );
        return randomChoice(advice);
    }

    private String randomChoice(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private String escapeJson(String raw) {
        return raw.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
