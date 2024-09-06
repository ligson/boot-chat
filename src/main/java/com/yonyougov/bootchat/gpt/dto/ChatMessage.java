package com.yonyougov.bootchat.gpt.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String role;
    private String content;
}
