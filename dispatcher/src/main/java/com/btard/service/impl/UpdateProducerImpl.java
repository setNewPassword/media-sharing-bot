package com.btard.service.impl;

import com.btard.service.UpdateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;


    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
