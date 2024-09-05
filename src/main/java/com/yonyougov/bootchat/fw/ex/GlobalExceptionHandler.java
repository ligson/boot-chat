package com.yonyougov.bootchat.fw.ex;

import com.yonyougov.bootchat.fw.ex.vo.FieldErrorMsg;
import com.yonyougov.bootchat.fw.web.vo.ErrorType;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = InnerException.class)
    public WebResult businessExceptionHandler(InnerException e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("内部错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }

    @ResponseBody
    @ExceptionHandler(value = BussinessException.class)
    public WebResult innerExceptionHandler(BussinessException e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Business);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("业务错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public WebResult methodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance("参数校验失败");
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(exception));
        webResult.setHttpCode(HttpStatus.BAD_REQUEST.value());
        List<FieldErrorMsg> fieldErrorMsgs = new ArrayList<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            fieldErrorMsgs.add(FieldErrorMsg.newInstance(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        webResult.putData("fieldError", fieldErrorMsgs);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        log.error("错误:{},stack:{}", exception.getMessage(), webResult.getStackTrace());
        return webResult;
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public WebResult exceptionHandler(Exception e, HttpServletResponse response) {
        WebResult webResult = WebResult.newErrorInstance(e.getMessage());
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(e));
        webResult.setHttpCode(500);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        log.error("错误:{},stack:{}", e.getMessage(), webResult.getStackTrace());
        return webResult;
    }
}
