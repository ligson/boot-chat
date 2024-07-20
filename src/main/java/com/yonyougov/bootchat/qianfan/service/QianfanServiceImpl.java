package com.yonyougov.bootchat.qianfan.service;

import com.yonyougov.bootchat.base.chatmsg.ChatMsg;
import com.yonyougov.bootchat.base.chatmsg.ChatMsgService;
import com.yonyougov.bootchat.base.user.User;
import com.yonyougov.bootchat.qianfan.dto.ChatMessage2;
import org.apache.tika.utils.StringUtils;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QianfanServiceImpl implements QianfanService {
    private final QianFanChatModel chatClient;

    private final ChatMsgService chatMsgService;

    public QianfanServiceImpl(QianFanChatModel chatClient, ChatMsgService chatMsgService) {
        this.chatClient = chatClient;
        this.chatMsgService = chatMsgService;
    }

    @Override
    public Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage) {
        if (!StringUtils.isEmpty(chatMessage.getProblem())) {
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setMsg(chatMessage.getProblem());
            chatMsg.setRole("user");
            chatMsg.setUserId(userId);
            //使上次回答的消息时间早于当前时间，以便于排序
            chatMsg.setCreateTime(new Date(System.currentTimeMillis()));
            chatMsgService.save(chatMsg);
        }
        List<ChatMsg> byUserId = chatMsgService.findByUserId(userId);
        Prompt prompt = new Prompt(
                byUserId.stream().map(m -> {
                    if (MessageType.ASSISTANT.getValue().equals(m.getRole())) {
                        return new AssistantMessage(m.getMsg());
                    } else {
                        return new UserMessage(m.getMsg());
                    }
                }).collect(Collectors.toList()));
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
}
