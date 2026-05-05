package com.TAstanov.MyChat_Notification.web.controller;

import com.TAstanov.MyChat_Notification.server.Impl.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/v1/notification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(path = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam String userTag){
        log.info("connect user");
        return  notificationService.subscribe(userTag);
    }

}
