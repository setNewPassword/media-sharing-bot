package com.btard.service.impl;

import com.btard.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(answerMessageQueue, sendMessage);
    }
}
