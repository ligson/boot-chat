package com.yonyougov.bootchat.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WxChatMessage {
    String problem;
    String group;
    Boolean isReadHistory;
    Boolean isReadVector;
}
