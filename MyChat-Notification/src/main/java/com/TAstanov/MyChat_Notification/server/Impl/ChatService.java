package com.TAstanov.MyChat_Notification.server.Impl;

import com.TAstanov.MyChat_Notification.domain.ChatMessage;
import com.TAstanov.MyChat_Notification.web.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final NotificationService notificationService;
    private final Map<String, List<ChatMessage>> dialogs = new ConcurrentHashMap<>();

    public ChatMessage send(ChatMessageDto dto) {
        validate(dto);
        ChatMessage message = new ChatMessage(
                dto.getFromTag().trim(),
                dto.getToTag().trim(),
                dto.getText().trim(),
                LocalDateTime.now()
        );

        dialogs.computeIfAbsent(dialogKey(message.getFromTag(), message.getToTag()), key -> new ArrayList<>())
                .add(message);
        notificationService.sendNewNotification(message.getToTag(), "New message from @" + message.getFromTag());
        return message;
    }

    public List<ChatMessage> getDialog(String userTag, String partnerTag) {
        return dialogs.getOrDefault(dialogKey(userTag, partnerTag), List.of())
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .toList();
    }

    private String dialogKey(String firstTag, String secondTag) {
        String first = firstTag.trim();
        String second = secondTag.trim();
        return first.compareTo(second) <= 0 ? first + ":" + second : second + ":" + first;
    }

    private void validate(ChatMessageDto dto) {
        if (dto == null || isBlank(dto.getFromTag()) || isBlank(dto.getToTag()) || isBlank(dto.getText())) {
            throw new IllegalArgumentException("fromTag, toTag and text are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
