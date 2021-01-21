package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-20 18:52
 */
@Data
public class SpuAttrValuesVo extends SpuAttrValueEntity {
    private List<String> valueSelected;

    public void setValueSelected(List<String> valueSelected) {
        if (!CollectionUtils.isEmpty(valueSelected)){
            this.setAttrValue( StringUtils.join(valueSelected,","));
        }
    }
}
