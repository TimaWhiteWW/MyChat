package com.TAstanov.MyChat_Recomendation.domain.exception;

public class ExceptionWhileKafkaProcessing extends RuntimeException {
    public ExceptionWhileKafkaProcessing(String message) {
        super(message);
    }
}
