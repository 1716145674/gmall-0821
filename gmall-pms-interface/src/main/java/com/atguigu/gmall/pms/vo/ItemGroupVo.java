package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zqq
 * @create 2021-02-19 18:52
 */
@Data
public class ItemGroupVo {
    private Long groupId;//组id
    private String groupName;//组名
    private List<AttrValueVo> attrValues;//这一组及其组中的属性

}
