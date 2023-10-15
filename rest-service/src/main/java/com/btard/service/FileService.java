package com.btard.service;

import com.btard.entity.AppDocument;
import com.btard.entity.AppPhoto;
import com.btard.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
