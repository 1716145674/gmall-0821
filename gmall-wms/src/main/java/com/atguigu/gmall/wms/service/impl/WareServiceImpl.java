package com.atguigu.gmall.wms.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.wms.mapper.WareMapper;
import com.atguigu.gmall.wms.entity.WareEntity;
import com.atguigu.gmall.wms.service.WareService;


@Service("wareService")
public class WareServiceImpl extends ServiceImpl<WareMapper, WareEntity> implements WareService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        QueryWrapper<WareEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(paramVo.getKey())){
            wrapper.like("name",paramVo.getKey()).or().like("address",paramVo.getKey());
        }
        IPage<WareEntity> page = this.page(
                paramVo.getPage(),
                wrapper
        );

        return new PageResultVo(page);
    }

}