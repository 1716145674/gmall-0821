package com.atguigu.gmall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;

import java.util.Map;

/**
 * spu属性值
 *
 * @author zqq
 * @email zqq@atguigu.com
 * @date 2021-01-18 21:05:27
 */
public interface SpuAttrValueService extends IService<SpuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

