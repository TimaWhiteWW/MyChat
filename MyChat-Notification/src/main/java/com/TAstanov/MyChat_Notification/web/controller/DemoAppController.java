package com.TAstanov.MyChat_Notification.web.controller;

import com.TAstanov.MyChat_Notification.domain.ChatMessage;
import com.TAstanov.MyChat_Notification.domain.DemoUser;
import com.TAstanov.MyChat_Notification.server.Impl.DemoAppService;
import com.TAstanov.MyChat_Notification.web.dto.ChatMessageDto;
import com.TAstanov.MyChat_Notification.web.dto.DemoLoginRequest;
import com.TAstanov.MyChat_Notification.web.dto.DemoRegisterRequest;
import com.TAstanov.MyChat_Notification.web.dto.VerifyEmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/demo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DemoAppController {

    private final DemoAppService demoAppService;

    @PostMapping("/register")
    public Map<String, Boolean> register(@RequestBody DemoRegisterRequest request) {
        demoAppService.register(request);
        return Map.of("verificationRequired", true);
    }

    @PostMapping("/verify")
    public DemoUser verify(@RequestBody VerifyEmailRequest request) {
        return demoAppService.verify(request);
    }

    @PostMapping("/login")
    public DemoUser login(@RequestBody DemoLoginRequest request) {
        return demoAppService.login(request);
    }

    @PutMapping("/users/{userId}")
    public DemoUser updateProfile(@PathVariable String userId, @RequestBody DemoUser profile) {
        return demoAppService.updateProfile(userId, profile);
    }

    @PostMapping(value = "/users/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DemoUser uploadPhoto(@PathVariable String userId, @RequestPart("photo") MultipartFile photo) {
        return demoAppService.uploadPhoto(userId, photo);
    }

    @GetMapping("/users/{userId}/discover")
    public List<DemoUser> discover(@PathVariable String userId) {
        return demoAppService.discover(userId);
    }

    @PostMapping("/users/{userId}/like/{targetId}")
    public Map<String, Boolean> like(@PathVariable String userId, @PathVariable String targetId) {
        return Map.of("match", demoAppService.like(userId, targetId));
    }

    @PostMapping("/users/{userId}/pass/{targetId}")
    public void pass(@PathVariable String userId, @PathVariable String targetId) {
        demoAppService.pass(userId, targetId);
    }

    @GetMapping("/users/{userId}/matches")
    public List<DemoUser> matches(@PathVariable String userId) {
        return demoAppService.matches(userId);
    }

    @PostMapping("/chat/send")
    public ChatMessage send(@RequestBody ChatMessageDto dto) {
        return demoAppService.send(dto);
    }

    @GetMapping("/chat/{userId}/{partnerId}")
    public List<ChatMessage> dialog(@PathVariable String userId, @PathVariable String partnerId) {
        return demoAppService.dialog(userId, partnerId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException exception) {
        return Map.of("error", exception.getMessage());
    }
}
