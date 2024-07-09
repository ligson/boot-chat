package com.yonyougov.bootchat.vo;

import java.util.List;

public class PageWebResult extends WebResult {
    public static <E> PageWebResult newInstance(long totalCount, List<E> datas) {
        PageWebResult pageWebResult = new PageWebResult();
        pageWebResult.setSuccess(true);
        pageWebResult.putData("datas", datas);
        pageWebResult.putData("totalCount", totalCount);
        return pageWebResult;
    }
}
