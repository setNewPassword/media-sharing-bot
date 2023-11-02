package com.btard.service.impl;

import com.btard.dao.AppUserDao;
import com.btard.service.UserActivationService;
import com.btard.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDao appUserDao;

    private final CryptoTool cryptoTool;

    @Override
    public boolean activateUser(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var appUserOptional = appUserDao.findById(userId);
        if (appUserOptional.isPresent()) {
            var user = appUserOptional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }

}