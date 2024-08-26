package com.yonyougov.bootchat.minio.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMsgDao  extends JpaRepository<FileMsg, String>, QuerydslPredicateExecutor<FileMsg> {
    FileMsg findByUploadId(String uploadId);

    FileMsg findByFileName(String fileName);
}
