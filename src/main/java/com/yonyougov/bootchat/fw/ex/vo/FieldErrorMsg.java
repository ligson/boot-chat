package com.yonyougov.bootchat.fw.ex.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorMsg {
    private String code;
    private String message;

    public static FieldErrorMsg newInstance(String field, String defaultMessage) {
        FieldErrorMsg fieldErrorMsg = new FieldErrorMsg();
        fieldErrorMsg.setCode(field);
        fieldErrorMsg.setMessage(defaultMessage);
        return fieldErrorMsg;
    }
}
