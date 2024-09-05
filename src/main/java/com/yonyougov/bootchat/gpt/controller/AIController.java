package com.yonyougov.bootchat.gpt.controller;


import com.yonyougov.bootchat.gpt.service.GptChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final GptChatService gptChatService;
    @Value("classpath:chat_templates/rag2.tpl")
    private Resource promptResource;
//    private final TokenTextSplitter tokenTextSplitter;

    public AIController(ChatClient chatClient, VectorStore vectorStore, GptChatService gptChatService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.gptChatService = gptChatService;
//        this.tokenTextSplitter = tokenTextSplitter;
    }

    @GetMapping("/chat")
    public String chat(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/test")
    public String test() throws MalformedURLException {
//        Resource resource = new UrlResource("https://zwfile.yonyougov.top/share/tmp/YonDiF替换基础镜像.pdf");
        File file = new File("D:\\data\\YonDiF替换基础镜像.pdf");
        Resource resource = new FileSystemResource(file);
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter
                                .Builder()
                                .withNumberOfBottomTextLinesToDelete(3)
                                .withNumberOfTopPagesToSkipBeforeDelete(1)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, config);
        List<Document> documents = new ArrayList<>(pagePdfDocumentReader.get());
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        return "ok";
    }

    @GetMapping("/test2")
    public Integer test2() {
        List<Document> yonDif = vectorStore.similaritySearch("这么制盘");
//        return "ok";
        return yonDif.size();
    }

    @GetMapping("/chat2")
    public String chatByVStore(String prompt) {
        List<Document> docs = vectorStore.similaritySearch(prompt);
        //String docStrings = docs.stream().map(Document::getContent).collect(Collectors.joining());
        // 获取documents里的content
        List<String> context = docs.stream().map(Document::getContent).toList();
        // 创建系统提示词
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(promptResource);
        // 填充数据
        Prompt p = promptTemplate.create(Map.of("context", context, "question", prompt));

        return chatClient.prompt(p).call().content();
    }
}
