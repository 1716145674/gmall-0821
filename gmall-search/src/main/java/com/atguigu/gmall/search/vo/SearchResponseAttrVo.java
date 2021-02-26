package com.atguigu.gmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-29 18:39
 */
@Data
public class SearchResponseAttrVo {

    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
