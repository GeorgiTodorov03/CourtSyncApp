package com.courtsync.service;

import com.courtsync.dto.*;
import com.courtsync.entity.*;
import com.courtsync.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIConversationRepository conversationRepository;
    private final AIMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SportHallRepository hallRepository;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.model}")
    private String openAiModel;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_PROMPT =
        "You are CourtSync AI Coach, an expert assistant for finding and booking sports halls. " +
        "Help users find courts, recommend venues, suggest time slots, and answer booking questions. " +
        "Be friendly, concise, and sports-enthusiastic. When recommending halls, mention specific names " +
        "from the available inventory if relevant. Keep responses under 200 words.";

    @Transactional
    public AIMessageDto sendMessage(AIMessageRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AIConversation conversation;
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findByIdAndUserId(request.getConversationId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        } else {
            conversation = AIConversation.builder()
                    .user(user)
                    .title(truncate(request.getMessage(), 50))
                    .messages(new ArrayList<>())
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        // Save user message
        AIMessage userMsg = AIMessage.builder()
                .conversation(conversation)
                .role(AIMessage.Role.USER)
                .content(request.getMessage())
                .build();
        messageRepository.save(userMsg);

        // Build history for OpenAI
        List<AIMessage> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());

        String aiResponse = callOpenAI(history, user.getFullName());

        // Save assistant message
        AIMessage assistantMsg = AIMessage.builder()
                .conversation(conversation)
                .role(AIMessage.Role.ASSISTANT)
                .content(aiResponse)
                .build();
        messageRepository.save(assistantMsg);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // Extract hall suggestions if mentioned
        List<SportHallDto> suggestions = extractSuggestedHalls(aiResponse);

        return AIMessageDto.builder()
                .conversationId(conversation.getId())
                .role("ASSISTANT")
                .content(aiResponse)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")))
                .suggestedHalls(suggestions)
                .build();
    }

    public List<AIMessage> getConversationMessages(Long conversationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        conversationRepository.findByIdAndUserId(conversationId, user.getId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @SuppressWarnings("unchecked")
    private String callOpenAI(List<AIMessage> history, String userName) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content",
                    SYSTEM_PROMPT + "\nUser's name: " + userName));

            for (AIMessage msg : history) {
                messages.add(Map.of(
                        "role", msg.getRole() == AIMessage.Role.USER ? "user" : "assistant",
                        "content", msg.getContent()
                ));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("model", openAiModel);
            body.put("messages", messages);
            body.put("max_tokens", 300);
            body.put("temperature", 0.8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions", entity, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, String> message = (Map<String, String>) choices.get(0).get("message");
                    return message.get("content");
                }
            }
        } catch (Exception e) {
            // Fallback response if OpenAI unavailable
        }
        return "I'm here to help you find the perfect court! Could you tell me what sport you'd like to play, how many players, and your preferred location or time?";
    }

    private List<SportHallDto> extractSuggestedHalls(String response) {
        // Simple keyword matching to suggest halls based on AI response
        List<SportHall> allHalls = hallRepository.findRecommended(
                org.springframework.data.domain.PageRequest.of(0, 20));
        return allHalls.stream()
                .filter(h -> response.toLowerCase().contains(h.getName().toLowerCase()))
                .limit(3)
                .map(h -> SportHallDto.builder()
                        .id(h.getId())
                        .name(h.getName())
                        .imageUrl(h.getImageUrl())
                        .pricePerHour(h.getPricePerHour())
                        .rating(h.getRating())
                        .build())
                .collect(Collectors.toList());
    }

    private String truncate(String text, int max) {
        return text.length() > max ? text.substring(0, max) : text;
    }
}
