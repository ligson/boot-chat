package com.yonyougov.bootchat.web;



import com.yonyougov.bootchat.qianfan.service.QianfanService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    private final QianfanService qianfanService;
    @Value("classpath:chat_templates/rag2.tpl")
    private Resource promptResource;

    public AIController(@Qualifier("ollamaChatClientBuilder") ChatClient.Builder builder, VectorStore vectorStore, QianfanService qianfanService) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.qianfanService = qianfanService;
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
        Resource resource = new UrlResource("https://zwfile.yonyougov.top/share/tmp/YonDiF替换基础镜像.pdf");
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = new ArrayList<>(tikaDocumentReader.get());
//        List<Document> allDoc = vectorStore.similaritySearch("");
//        List<File> fileList = qianfanService.getFileList();
//        if (fileList == null || fileList.isEmpty()) {
//            throw new RuntimeException("文件列表为空");
//        }
//        List<Document> documents = new ArrayList<>();
//        for (File file : fileList) {
//            if (allDoc.stream().noneMatch(doc -> doc.getMetadata().get("source").equals(file.getName()))) {
//                Resource resource = new FileSystemResource(file);
//                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//                documents.addAll(tikaDocumentReader.get());
//            } else {
//                System.out.println(file.getName() + " 文件已缓存");
//            }
//        }
//        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
//        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader("chat_templates/YonDiF替换基础镜像.pdf");
//        return vectorStore.getName();
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
        return "ok";
    }

    @GetMapping("/test2")
    public Integer test2() {
        List<Document> yonDif = vectorStore.similaritySearch("用友的一个产品");
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
