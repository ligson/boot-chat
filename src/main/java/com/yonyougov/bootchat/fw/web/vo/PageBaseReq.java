package com.yonyougov.bootchat.fw.web.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class PageBaseReq extends BaseReq{
    private int page;
    private int size;

    private String sort;
    private Sort.Direction order;
}
