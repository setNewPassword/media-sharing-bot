package com.btard.service.impl;

import com.btard.dao.AppUserDao;
import com.btard.dto.MailParams;
import com.btard.entity.AppUser;
import com.btard.service.AppUserService;
import com.btard.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

import static com.btard.entity.enums.UserState.BASIC_STATE;
import static com.btard.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDao appUserDao;

    private final CryptoTool cryptoTool;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы.\n" +
                    "Можете пользоваться ботом без ограничений.";
        } else if (appUser.getEmail() != null) {
            //TODO добавить возможность повторной отправки письма с задержкой по времени
            return "Письмо для подтверждения регистрации было отправлено вам на почту. \n"
                    + "Перейдите по ссылке в письме, чтоб начать пользоваться ботом.";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Введите, пожалуйста, ваш email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            var emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Пожалуйста, введите корректный email.\n" +
                    "Для отмены команды введите /cancel";
        }
        Optional<AppUser> appUserOptional = appUserDao.findByEmail(email);
        if (appUserOptional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser = appUserDao.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email);
            return "Письмо для подтверждения регистрации было отправлено вам на почту. \n"
                    + "Перейдите по ссылке в письме, чтоб начать пользоваться ботом.";
        } else {
            return "Этот email уже используется другим пользователем.\n" +
                    "Введите корректный email. \n"
                    + " Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams
                .builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}