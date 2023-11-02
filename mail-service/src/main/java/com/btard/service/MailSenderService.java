package com.btard.service;

import com.btard.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);

}