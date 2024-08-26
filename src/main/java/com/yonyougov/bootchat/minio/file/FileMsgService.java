package com.yonyougov.bootchat.minio.file;

import java.io.File;
import java.util.Optional;

public interface FileMsgService {

    FileMsg save(String fileName);
    FileMsg save(String fileName,Integer size,String url);

    FileMsg findById(String id);
    FileMsg findByUploadId(String uploadId);
    FileMsg findByFileName(String fileName);

    void delete(String id);

}
