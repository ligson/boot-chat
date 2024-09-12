package com.yonyougov.bootchat.gpt.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface VectorStoreService {
    List<Document> searchDocument(String searchText);

    void indexDocuments();
}
