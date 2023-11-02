package com.btard.service;

import com.btard.dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail(MailParams mailParams);

}
