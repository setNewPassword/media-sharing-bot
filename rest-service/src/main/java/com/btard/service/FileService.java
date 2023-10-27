package com.btard.service;

import com.btard.entity.AppDocument;
import com.btard.entity.AppPhoto;


public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}
