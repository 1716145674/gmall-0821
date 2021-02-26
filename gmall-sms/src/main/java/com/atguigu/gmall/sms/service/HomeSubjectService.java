package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.entity.HomeSubjectEntity;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author oono
 * @email andychao3210@gmail.com
 * @date 2021-01-18 20:44:07
 */
public interface HomeSubjectService extends IService<HomeSubjectEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

