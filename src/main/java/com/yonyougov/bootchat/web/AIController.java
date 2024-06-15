package com.yonyougov.bootchat.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    @Value("classpath:chat_templates/rag.tpl")
    private Resource promptResource;

    public AIController(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/chat")
    public String chat(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    public String test() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader("");
        vectorStore.add(tikaDocumentReader.get());
        return "ok";
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
