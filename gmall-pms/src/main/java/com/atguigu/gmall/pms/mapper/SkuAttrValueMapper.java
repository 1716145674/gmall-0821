package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * 
 * @author zqq
 * @email zqq@atguigu.com
 * @date 2021-01-18 21:05:27
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    List<AttrValueVo> querySkuAttrValuesBySpuId(Long spuId);

    List<Map<String, Object>> querySkusJsonBySpuId(Long spuId);
}
