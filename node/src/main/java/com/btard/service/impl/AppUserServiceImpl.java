package com.btard.service.impl;

import com.btard.dao.AppUserDao;
import com.btard.dto.MailParams;
import com.btard.entity.AppUser;
import com.btard.service.AppUserService;
import com.btard.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import java.util.Optional;

import static com.btard.entity.enums.UserState.BASIC_STATE;
import static com.btard.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDao appUserDao, CryptoTool cryptoTool) {
        this.appUserDao = appUserDao;
        this.cryptoTool = cryptoTool;
    }

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
            InternetAddress emailAddr = new InternetAddress(email);
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
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var msg = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDao.save(appUser);
                return msg;
            }
            return "Письмо для подтверждения регистрации было отправлено вам на почту. \n"
                    + "Перейдите по ссылке в письме, чтоб начать пользоваться ботом.";
        } else {
            return "Этот email уже используется другим пользователем.\n" +
                    "Введите корректный email. \n"
                    + " Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
