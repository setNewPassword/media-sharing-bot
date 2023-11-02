package com.btard.service.impl;

import com.btard.dto.MailParams;
import com.btard.service.ConsumerService;
import com.btard.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}