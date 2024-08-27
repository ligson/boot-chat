package com.yonyougov.bootchat.qianfan.service;

import com.yonyougov.bootchat.base.chatmsg.ChatMsg;
import com.yonyougov.bootchat.base.chatmsg.ChatMsgService;
import com.yonyougov.bootchat.util.SaveWikeUtil;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;

import com.yonyougov.bootchat.qianfan.dto.ChatMessage2;
import org.apache.tika.utils.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QianfanServiceImpl implements QianfanService {
    private final QianFanChatModel chatClient;
    private final VectorStore vectorStore;
    private final ChatMsgService chatMsgService;
    @Value("${yondif.file.path}")
    String filePath;
    @Value("classpath:chat_templates/rag2.tpl")
    private Resource promptResource;

    public QianfanServiceImpl(QianFanChatModel chatClient, VectorStore vectorStore, ChatMsgService chatMsgService) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.chatMsgService = chatMsgService;
    }

    @Override
    public Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage) {
        List<Document> docs = vectorStore.similaritySearch(chatMessage.getProblem());

        List<String> context = docs.stream().map(Document::getContent).toList();

        List<ChatMsg> byUserId = chatMsgService.findByUserId(userId);
        Prompt prompt = new Prompt(
                byUserId.stream().map(m -> {
                    if (MessageType.ASSISTANT.getValue().equals(m.getRole())) {
                        return new AssistantMessage(m.getMsg());
                    } else {
                        return new UserMessage(m.getMsg());
                    }
                }).collect(Collectors.toList()));
        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(promptResource);
        // 填充数据
        Prompt p = promptTemplate.create(Map.of("context", context, "question", chatMessage.getProblem()));
        if (!StringUtils.isEmpty(chatMessage.getProblem())) {
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setMsg(chatMessage.getProblem());
            chatMsg.setRole("user");
            chatMsg.setUserId(userId);
            //使上次回答的消息时间早于当前时间，以便于排序
            chatMsg.setCreateTime(new Date(System.currentTimeMillis()));
            chatMsgService.save(chatMsg);
        }

        prompt.getInstructions().add(new UserMessage(p.toString()));
        Flux<ChatResponse> result = chatClient.stream(prompt);

        return result.collectList()
                .flatMapMany(list -> {
                    // 处理list中的数据，例如将它们连接成一个字符串
                    String fullAnswer = list.stream()
                            .map(ChatResponse::getResult)
                            .map(Generation::getOutput)
                            .map(AssistantMessage::getContent)
                            .reduce((a, b) -> a + b)
                            .orElse("");

                    ChatMsg chatMsg = new ChatMsg();
                    chatMsg.setMsg(fullAnswer);
                    chatMsg.setRole("assistant");
                    chatMsg.setUserId(userId);
                    chatMsg.setCreateTime(new Date());
                    chatMsgService.save(chatMsg);
                    return Flux.fromIterable(list);
                });

    }

    public List<File> getFileList() {
        File folder = new File(filePath);
        List<File> fileList = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return fileList;
        }
        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(".pdf") || file.getName().endsWith(".doc") || file.getName().endsWith(".docx") || file.getName().endsWith(".txt"))) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Override
//    @Async
    public void saveFile(String tooken) throws Exception {
        SaveWikeUtil saveWikeUtil = new SaveWikeUtil();
        saveWikeUtil.saveWike(tooken,filePath);
    }


    @Override
    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void AddVectorStore() {
        List<Document> allDoc = vectorStore.similaritySearch("");
        List<File> fileList = getFileList();
        if (fileList == null || fileList.isEmpty()) {
            throw new RuntimeException("文件列表为空");
        }
        List<Document> documents = new ArrayList<>();
        for (File file : fileList) {
            if (allDoc.stream().noneMatch(doc -> doc.getMetadata().get("source").equals(file.getName()))) {
                Resource resource = new FileSystemResource(file);
                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                documents.addAll(tikaDocumentReader.get());
            } else {
                System.out.println(file.getName() + " 文件已缓存");
            }
        }
        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }
    }
}
