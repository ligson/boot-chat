package com.yonyougov.bootchat.gpt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class VectorStoreServiceImpl implements VectorStoreService {
    private final VectorStore vectorStore;
    @Value("${yondif.file.path}")
    private String docDir;
    private final TokenTextSplitter tokenTextSplitter;

    public VectorStoreServiceImpl(VectorStore vectorStore,
                                  TokenTextSplitter tokenTextSplitter) {
        this.vectorStore = vectorStore;
        this.tokenTextSplitter = tokenTextSplitter;
    }


    @Override
    public List<Document> searchDocument(String searchText) {
        return vectorStore.similaritySearch(searchText);
    }


    public List<File> getFileList() {
        File folder = new File(docDir);

        return Arrays.stream(Objects.requireNonNull(folder.listFiles(new FilenameFilter() {
            private static final String[] fileNameSuffixes = {"pdf", "txt"};

            @Override
            public boolean accept(File dir, String name) {
                return Arrays.stream(fileNameSuffixes).anyMatch(suffix -> name.endsWith("." + suffix));
            }
        }))).toList();

    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void indexDocuments() {
        List<Document> allDoc = vectorStore.similaritySearch("");
        List<File> fileList = getFileList();
        if (fileList == null || fileList.isEmpty()) {
            throw new RuntimeException("文件列表为空");
        }
        for (File file : fileList) {
            if (allDoc.stream().noneMatch(doc -> file.getName().equals(doc.getMetadata().get("source")))) {
//                Resource resource = new FileSystemResource(file);
//                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//                documents.addAll(tikaDocumentReader.get());
                Resource resource = new FileSystemResource(file);
                PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder().withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(3).withNumberOfTopPagesToSkipBeforeDelete(1).build()).withPagesPerDocument(1).build();
                PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, config);
//                documents.addAll(pagePdfDocumentReader.get());
                try {
                    vectorStore.add(tokenTextSplitter.apply(pagePdfDocumentReader.get()));
                } catch (Exception e) {
                    log.error("添加向量失败", e);
                }
            } else {
                System.out.println(file.getName() + " 文件已缓存");
            }
        }
    }
    public void addVector(Map<String, String> text) {
        vectorStore.add( List.of(new Document(text.get("massage"))));
    }
}
