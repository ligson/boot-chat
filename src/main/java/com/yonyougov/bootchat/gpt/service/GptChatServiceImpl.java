package com.yonyougov.bootchat.gpt.service;

import com.yonyougov.bootchat.chatmsg.ChatMsg;
import com.yonyougov.bootchat.chatmsg.ChatMsgService;
import com.yonyougov.bootchat.config.gpt.model.MultiChatModel;
import com.yonyougov.bootchat.gpt.dto.ChatMessage2;
import com.yonyougov.bootchat.gpt.dto.WxChatMessage;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GptChatServiceImpl implements GptChatService {
    private final ChatClient chatClient;
    private final MultiChatModel multiChatModel;
    private final ChatMsgService chatMsgService;
    @Value("${yondif.file.path}")
    String filePath;
    @Value("classpath:chat_templates/rag2.tpl")
    private Resource promptResource;
    private final VectorStoreService vectorStoreService;

    public GptChatServiceImpl(ChatClient chatClient, MultiChatModel multiChatModel, ChatMsgService chatMsgService, VectorStoreService vectorStoreService) {
        this.chatClient = chatClient;
        this.multiChatModel = multiChatModel;
        this.vectorStoreService = vectorStoreService;
        this.chatMsgService = chatMsgService;
    }


    private Prompt buildPrompt(String userId, ChatMessage2 chatMessage, Boolean isReadVector, Boolean isReadHistory) {
        List<String> context = new ArrayList<>();
        if ((isReadVector == null || isReadVector)) {
            List<Document> docs = vectorStoreService.searchDocument(chatMessage.getProblem());
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
            } else {
                SystemPromptTemplate promptTemplate = new SystemPromptTemplate(promptResource);
                // 填充数据
                Prompt p = promptTemplate.create(Map.of("context", context, "question", chatMessage.getProblem()));
                prompt.getInstructions().add(new UserMessage(p.toString()));
//                return prompt;
            }
            return prompt;
        }
        if (!context.isEmpty()) {
            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(promptResource);
            // 填充数据
            Prompt p = promptTemplate.create(Map.of("context", context, "question", chatMessage.getProblem()));
            return new Prompt(new UserMessage(p.toString()));
        }
        return new Prompt(new UserMessage(chatMessage.getProblem()));
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


}
