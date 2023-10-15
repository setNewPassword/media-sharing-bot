package com.btard.service.impl;

import com.btard.dao.AppDocumentDao;
import com.btard.dao.AppPhotoDao;
import com.btard.entity.AppDocument;
import com.btard.entity.AppPhoto;
import com.btard.entity.BinaryContent;
import com.btard.service.FileService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDao appDocumentDao;
    private final AppPhotoDao appPhotoDao;

    public FileServiceImpl(AppDocumentDao appDocumentDao, AppPhotoDao appPhotoDao) {
        this.appDocumentDao = appDocumentDao;
        this.appPhotoDao = appPhotoDao;
    }


    @Override
    public AppDocument getDocument(String docId) {
        //TODO добавить дешифрование хеш-строки
        var id = Long.parseLong(docId);
        return appDocumentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String photoId) {
        //TODO добавить дешифрование хеш-строки
        var id = Long.parseLong(photoId);
        return appPhotoDao.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            //TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
