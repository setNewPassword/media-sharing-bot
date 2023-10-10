package com.btard.service.impl;

import com.btard.dao.RawDataDao;
import com.btard.entity.RawData;
import com.btard.service.MainService;
import com.btard.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;

    public MainServiceImpl(RawDataDao rawDataDao, ProducerService producerService) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Hello from Node!");
        producerService.produceAnswer(sendMessage);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData
                .builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);
    }
}
