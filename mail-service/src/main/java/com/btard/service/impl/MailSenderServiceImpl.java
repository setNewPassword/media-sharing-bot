package com.btard.service.impl;

import com.btard.dto.MailParams;
import com.btard.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${service.activation.uri}")
    private String activationServiceUri;

    @Override
    public void send(MailParams mailParams) {
        var subject = "Активация учетной записи";
        var messageBody = getActivationMailBody(mailParams.getId());
        var emailTo = mailParams.getEmailTo();

        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        var msg = String.format("Для завершения регистрации перейдите по ссылке:\n %s",
                activationServiceUri);
        return msg.replace("{id}", id);
    }
}