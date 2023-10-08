package com.btard.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
}
