package com.yonyougov.bootchat.fw.ex;

public class InnerException extends RuntimeException {
    public InnerException(String error) {
        super(error);
    }

    public InnerException(String format, Object... argArray) {
        super(String.format(format.replace("{}", "%s"), argArray));
    }

    public InnerException(Throwable error) {
        super(error);
    }
}
