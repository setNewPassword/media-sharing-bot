package com.btard.dao;

import com.btard.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDao extends JpaRepository<AppUser, Long> {
    AppUser findAppUsersByTelegramUserId(Long id);
}
