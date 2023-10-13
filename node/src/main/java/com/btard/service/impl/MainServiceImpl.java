package com.btard.service.impl;

import com.btard.dao.AppUserDao;
import com.btard.dao.RawDataDao;
import com.btard.entity.AppDocument;
import com.btard.entity.AppUser;
import com.btard.entity.RawData;
import com.btard.exception.FileUploadException;
import com.btard.service.FileService;
import com.btard.service.MainService;
import com.btard.service.ProducerService;
import com.btard.service.enums.ServiceCommand;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.btard.entity.enums.UserState.BASIC_STATE;
import static com.btard.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static com.btard.service.enums.ServiceCommand.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    public MainServiceImpl(RawDataDao rawDataDao,
                           ProducerService producerService,
                           AppUserDao appUserDao,
                           FileService fileService) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if(CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if(BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if(WAIT_FOR_EMAIL_STATE.equals(userState)) {
            //TODO Добавить обработку электронной почты
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и повторите попытку.";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            //TODO Добавить генерацию ссылки для скачивания документа
            var answer = "Документ успешно загружен! Ссылка для скачивания: https://tiny.cc/km8cvz";
            sendAnswer(answer, chatId);
        } catch (FileUploadException exception) {
            log.error(exception);
            var error = "К сожалению, не удалось загрузить документ. Попробуйте еще раз чуть позже.";
            sendAnswer(error, chatId);
        }

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if(isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        //TODO добавить сохранение фоток
        var answer = "Фото успешно загружено! Ссылка для скачивания: https://tiny.cc/km8cvz";
        sendAnswer(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if(!appUser.getIsActive()) {
            var error = "Зарегистрируйтесь или активируйте учетную запись для отправки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки контента.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommand.fromValue(cmd);
        String answer = "";
        if(REGISTRATION.equals(serviceCommand)) {
            //TODO добавить регистрацию
            answer = "Функция в разработке";
        } else if(HELP.equals(serviceCommand)) {
            answer = help();
        } else if (START.equals(serviceCommand)) {
            answer = "Привет! Чтобы просмотреть список доступных команд, введите /help";
        } else {
            answer = "Неизвестная команда. Чтобы просмотреть список доступных команд, введите /help";
        }
        return answer;
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cancel — отмена выполнения текущей операции;\n"
                + "/registration — регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена.";
    }

    private AppUser findOrSaveAppUser(Update update) {
        var telegramUser = update.getMessage().getFrom();
        AppUser persistentAppUser = appUserDao.findAppUsersByTelegramUserId(telegramUser.getId());
        if(persistentAppUser == null) {
            AppUser transientAppUser = AppUser
                    .builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData
                .builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }
}
