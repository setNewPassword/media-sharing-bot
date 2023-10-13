package com.btard.dao;

import com.btard.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDao extends JpaRepository<AppDocument, Long> {
}
