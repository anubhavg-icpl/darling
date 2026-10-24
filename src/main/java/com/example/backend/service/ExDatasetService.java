package com.example.backend.service;

import com.example.backend.dto.ExDatasetAnalysisDto;
import com.example.backend.dto.ExDatasetUploadDto;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExDatasetService {

    private final UserService userService;
    private final UserRepository userRepository;

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "and", "or", "but", "of", "to", "in", "on", "at", "for", "with",
            "is", "are", "was", "were", "be", "been", "it", "its", "this", "that", "these",
            "those", "i", "you", "he", "she", "we", "they", "me", "my", "your", "not", "no",
            "so", "if", "as", "by", "from", "do", "does", "did", "have", "has", "had", "just"
    );

    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[\\x{1F600}-\\x{1F64F}|\\x{1F300}-\\x{1F5FF}|\\x{1F680}-\\x{1F6FF}|\\x{1F1E0}-\\x{1F1FF}|\\x{2600}-\\x{26FF}|\\x{2700}-\\x{27BF}]",
            Pattern.UNICODE_CHARACTER_CLASS
    );

    @Transactional
    public ExDatasetAnalysisDto processAndTrainDataset(String email, ExDatasetUploadDto dto) {
        User user = userService.findByEmail(email);

        String rawChat = dto.getChatContent();
        String exName = (dto.getExSenderName() != null && !dto.getExSenderName().isBlank())
                ? dto.getExSenderName().trim()
                : (user.getExName() != null ? user.getExName() : "Ex");

        String[] lines = rawChat.split("\\r?\\n");
        List<String> exMessages = new ArrayList<>();
        List<String> userMessages = new ArrayList<>();
        Map<String, Integer> wordCounts = new HashMap<>();
        Map<String, Integer> emojiCounts = new HashMap<>();

        for (String line : lines) {
            String clean = line.trim();
            if (clean.isBlank()) continue;

            // Extract emojis
            Matcher emojiMatcher = EMOJI_PATTERN.matcher(clean);
            while (emojiMatcher.find()) {
                String emoji = emojiMatcher.group();
                emojiCounts.put(emoji, emojiCounts.getOrDefault(emoji, 0) + 1);
            }

            // Tell the ex's messages apart from the user's
            if (clean.toLowerCase().contains(exName.toLowerCase() + ":")) {
                int index = clean.toLowerCase().indexOf(exName.toLowerCase() + ":");
                String text = clean.substring(index + exName.length() + 1).trim();
                exMessages.add(text);
                countWords(text, wordCounts);
            } else {
                userMessages.add(clean);
            }
        }

        // If the format had no "ExName:" prefix, assume the odd-indexed lines belong to the ex
        if (exMessages.isEmpty()) {
            for (int i = 0; i < lines.length; i++) {
                String clean = lines[i].trim();
                if (clean.isBlank()) continue;
                if (i % 2 == 1) {
                    exMessages.add(clean);
                    countWords(clean, wordCounts);
                } else {
                    userMessages.add(clean);
                }
            }
        }

        double averageLength = exMessages.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0.0);

        List<String> frequentPhrases = wordCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> frequentEmojis = emojiCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String detectedTone = inferTone(averageLength, frequentPhrases, rawChat.toLowerCase());

        // Persist the personality and the training extract on the User entity
        user.setExPersonality(detectedTone);
        String trainingSummary = String.format("Phrases: %s | Emojis: %s | AverageLength: %.1f",
                String.join(", ", frequentPhrases),
                String.join(" ", frequentEmojis),
                averageLength);
        user.setExTrainingDataset(trainingSummary);
        userRepository.save(user);

        log.info("Ex dataset processed for user {}. Inferred tone: {}", email, detectedTone);

        return ExDatasetAnalysisDto.builder()
                .totalLinesProcessed(lines.length)
                .totalExMessages(exMessages.size())
                .totalUserMessages(userMessages.size())
                .averageExMessageLength(Math.round(averageLength * 100.0) / 100.0)
                .frequentExPhrases(frequentPhrases)
                .frequentEmojis(frequentEmojis)
                .detectedTone(detectedTone)
                .message("Dataset analyzed and personality clone updated successfully (JustZando style).")
                .build();
    }

    private void countWords(String text, Map<String, Integer> map) {
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");
        for (String p : words) {
            if (p.length() > 3 && !STOP_WORDS.contains(p)) {
                map.put(p, map.getOrDefault(p, 0) + 1);
            }
        }
    }

    private String inferTone(double avgLength, List<String> words, String fullText) {
        if (avgLength < 15.0) {
            return "COLD_AND_CURT";
        }
        if (fullText.contains("your fault") || fullText.contains("always") || fullText.contains("never") || fullText.contains("blame")) {
            return "TOXIC_ACCUSATORY";
        }
        if (fullText.contains("love") || fullText.contains("miss you") || fullText.contains("baby") || fullText.contains("sweetheart")) {
            return "NOSTALGIC_AMBIVALENT";
        }
        return "DISTANT_DISINTERESTED";
    }
}
