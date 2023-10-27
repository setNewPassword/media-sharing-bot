package com.btard.service.impl;

import com.btard.dao.AppDocumentDao;
import com.btard.dao.AppPhotoDao;
import com.btard.entity.AppDocument;
import com.btard.entity.AppPhoto;
import com.btard.service.FileService;
import com.btard.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDao appDocumentDao,
                           AppPhotoDao appPhotoDao,
                           CryptoTool cryptoTool) {
        this.appDocumentDao = appDocumentDao;
        this.appPhotoDao = appPhotoDao;
        this.cryptoTool = cryptoTool;
    }


    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appDocumentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDao.findById(id).orElse(null);
    }

}
