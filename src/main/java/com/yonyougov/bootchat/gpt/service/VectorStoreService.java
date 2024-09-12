package com.yonyougov.bootchat.gpt.service;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

public interface VectorStoreService {
    List<Document> searchDocument(String searchText);

    void indexDocuments();

    void addVector(Map<String, String> text);
}
