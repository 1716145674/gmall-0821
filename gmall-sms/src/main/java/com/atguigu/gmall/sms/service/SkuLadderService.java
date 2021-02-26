package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SalesVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;

/**
 * 商品阶梯价格
 *
 * @author oono
 * @email andychao3210@gmail.com
 * @date 2021-01-18 20:44:07
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveladder(SalesVo salesVo);
}

