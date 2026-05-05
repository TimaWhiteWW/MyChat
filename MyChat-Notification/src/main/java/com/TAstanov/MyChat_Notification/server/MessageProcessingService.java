package com.TAstanov.MyChat_Notification.server;

import com.TAstanov.MyChat_Notification.server.Impl.KafkaListenerImpl;

public interface MessageProcessingService {

    void process(KafkaListenerImpl.LikeKafkaDto likeKafkaDto);

}
