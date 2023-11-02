package com.btard.service;

import com.btard.entity.AppUser;

public interface AppUserService {

    String registerUser(AppUser appUser);

    String setEmail(AppUser appUser, String email);

}