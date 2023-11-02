package com.btard.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {

    void consume(SendMessage sendMessage);

}
