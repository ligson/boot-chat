package com.yonyougov.bootchat.util.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVo {
    private String id;
    private String name;
    private String url;
    private List<PageVo> pageVoList = new ArrayList<>();
}
