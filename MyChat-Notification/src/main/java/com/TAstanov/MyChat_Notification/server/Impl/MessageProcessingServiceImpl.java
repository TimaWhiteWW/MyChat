package com.TAstanov.MyChat_Notification.server.Impl;

import com.TAstanov.MyChat_Notification.server.MessageProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProcessingServiceImpl implements MessageProcessingService {

    private final NotificationService notificationService;

    @Override
    public void process(KafkaListenerImpl.LikeKafkaDto likeKafkaDto) {
        notificationService.sendNewNotification(likeKafkaDto.getValue1(), likeKafkaDto.getValue2());
        notificationService.sendNewNotification(likeKafkaDto.getValue2(), likeKafkaDto.getValue1());
    }


}
