package com.yonyougov.bootchat.ex;

public class BussinessException extends RuntimeException {
    public BussinessException(String error) {
        super(error);
    }

    public BussinessException(String format, Object... argArray) {
        super(String.format(format.replace("{}", "%s"), argArray));
    }

    public BussinessException(Throwable error) {
        super(error);
    }
}
