package com.btard.dao;

import com.btard.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDao extends JpaRepository<AppPhoto, Long> {
}
