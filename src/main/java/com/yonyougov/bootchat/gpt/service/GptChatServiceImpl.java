package com.yonyougov.bootchat.gpt.service;

import com.yonyougov.bootchat.chatmsg.ChatMsg;
import com.yonyougov.bootchat.chatmsg.ChatMsgService;
import com.yonyougov.bootchat.config.gpt.model.MultiChatModel;
import com.yonyougov.bootchat.gpt.dto.ChatMessage2;
import com.yonyougov.bootchat.gpt.dto.WxChatMessage;
import com.yonyougov.bootchat.util.SaveWikeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.qianfan.QianFanChatModel;
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
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GptChatServiceImpl implements GptChatService {
    private final ChatClient chatClient;
    private final TokenTextSplitter tokenTextSplitter;
    private final MultiChatModel multiChatModel;
    private final QianFanChatModel qianFanChatModel;
    private final VectorStore vectorStore;
    private final ChatMsgService chatMsgService;
    @Value("${yondif.file.path}")
    String filePath;
    @Value("classpath:chat_templates/rag2.tpl")
    private Resource promptResource;
    @Value("${yondif.chat.model:qianfan}")
    private String chatModel;

    public GptChatServiceImpl(ChatClient chatClient, TokenTextSplitter tokenTextSplitter, MultiChatModel multiChatModel, QianFanChatModel qianFanChatModel, VectorStore vectorStore, ChatMsgService chatMsgService) {
        this.chatClient = chatClient;
        this.tokenTextSplitter = tokenTextSplitter;
        this.multiChatModel = multiChatModel;
        this.qianFanChatModel = qianFanChatModel;
        this.vectorStore = vectorStore;
        this.chatMsgService = chatMsgService;
    }


    private Prompt buildPrompt(String userId, ChatMessage2 chatMessage, Boolean isReadVector, Boolean isReadHistory) {
        List<String> context = new ArrayList<>();
        if ((isReadVector == null || isReadVector)) {
            List<Document> docs = vectorStore.similaritySearch(chatMessage.getProblem());
            context = docs.stream().map(Document::getContent).toList();
        }

        if (isReadHistory == null || isReadHistory) {
            List<ChatMsg> byUserId = chatMsgService.findByUserId(userId);
            Prompt prompt = new Prompt(byUserId.stream().map(m -> {
                if (MessageType.ASSISTANT.getValue().equals(m.getRole())) {
                    return new AssistantMessage(m.getMsg());
                } else {
                    return new UserMessage(m.getMsg());
                }
            }).collect(Collectors.toList()));
            if (context.isEmpty()) {
                prompt.getInstructions().add(new UserMessage(chatMessage.getProblem()));
                return prompt;
            } else {
                SystemPromptTemplate promptTemplate = new SystemPromptTemplate(promptResource);
                // 填充数据
                Prompt p = promptTemplate.create(Map.of("context", context, "question", chatMessage.getProblem()));
                prompt.getInstructions().add(new UserMessage(p.toString()));
                return prompt;
            }
        }
        return new Prompt(context.stream().map(UserMessage::new).collect(Collectors.toList()));
    }

    @Override
    public Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage) {
        Prompt prompt = buildPrompt(userId, chatMessage, true, true);
        if (!StringUtils.isEmpty(chatMessage.getProblem())) {
            chatMsgService.saveMsg(userId, false, chatMessage.getProblem());
        }


        Flux<ChatResponse> result = multiChatModel.stream(prompt);

        return result.collectList().flatMapMany(list -> {
            // 处理list中的数据，例如将它们连接成一个字符串
            String fullAnswer = list.stream().map(ChatResponse::getResult).map(Generation::getOutput).map(AssistantMessage::getContent).reduce((a, b) -> a + b).orElse("");

            chatMsgService.saveMsg(userId, true, fullAnswer);

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
            if (file.isFile() && (file.getName().endsWith(".pdf") || file.getName().endsWith(".txt"))) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Override
//    @Async
    public void saveFile(String cookie) throws Exception {
        SaveWikeUtil saveWikeUtil = new SaveWikeUtil();
        saveWikeUtil.saveWiki(cookie, filePath);
    }

    @Override
    public ChatResponse generate(String userId, String message) {
        Prompt prompt = buildPrompt(userId, new ChatMessage2(message, ""), false, false);
//        chatMsgService.saveMsg(userId, false, message);
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
//        chatMsgService.saveMsg(userId, true, response.getResult().getOutput().getContent());
        return response;
    }

    @Override
    public String call(WxChatMessage wxChatMessage) {
        if (!StringUtils.isEmpty(wxChatMessage.getProblem()) && (wxChatMessage.getIsReadHistory() == null || wxChatMessage.getIsReadHistory())) {
            chatMsgService.saveMsg(wxChatMessage.getGroup(), false, wxChatMessage.getProblem());
        }
        ChatMessage2 chatMessage2 = new ChatMessage2(wxChatMessage.getProblem(), "");
        Prompt prompt = buildPrompt(wxChatMessage.getGroup(), chatMessage2, wxChatMessage.getIsReadVector(), wxChatMessage.getIsReadHistory());
        String result = multiChatModel.call(prompt).getResult().getOutput().getContent();
        if (wxChatMessage.getIsReadHistory() == null || wxChatMessage.getIsReadHistory()) {
            chatMsgService.saveMsg(wxChatMessage.getGroup(), true, result);
        }
        return result;
//        return "";
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
}
