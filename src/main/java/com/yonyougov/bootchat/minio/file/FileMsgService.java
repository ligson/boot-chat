package com.yonyougov.bootchat.minio.file;


import com.yonyougov.bootchat.gpt.dto.ChatMessage2;

import java.io.File;
import java.util.Optional;

public interface FileMsgService {

    FileMsg save(String fileName);
    FileMsg save(String fileName, Integer size, String url, ChatMessage2 messages);

    FileMsg findById(String id);
    FileMsg findByUploadId(String uploadId);
    FileMsg findByFileName(String fileName);

    void delete(String id);

}
