package com.btard.dao;

import com.btard.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentDao extends JpaRepository<BinaryContent, Long> {
}
