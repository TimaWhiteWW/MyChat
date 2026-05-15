package com.TAstanov.MyChat_Notification.web.controller;

import com.TAstanov.MyChat_Notification.domain.ChatMessage;
import com.TAstanov.MyChat_Notification.server.Impl.ChatService;
import com.TAstanov.MyChat_Notification.web.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ChatMessage send(@RequestBody ChatMessageDto dto) {
        return chatService.send(dto);
    }

    @GetMapping("/dialog")
    public List<ChatMessage> dialog(@RequestParam String userTag, @RequestParam String partnerTag) {
        return chatService.getDialog(userTag, partnerTag);
    }
}
