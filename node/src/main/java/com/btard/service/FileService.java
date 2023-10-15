package com.btard.service;

import com.btard.entity.AppDocument;
import com.btard.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
