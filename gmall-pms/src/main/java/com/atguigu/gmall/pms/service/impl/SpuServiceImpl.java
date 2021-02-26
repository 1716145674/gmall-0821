package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpusByCategoryId(Long cid, PageParamVo pageParamVo) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        String queryKey = pageParamVo.getKey();
        // 判断是否根据categoryid查
        // where category_id=cid and(id=queryKey or name like '%queryKey%')
        if (cid!=0){
            wrapper.eq("category_id",cid);
        }
        if (StringUtils.isNotBlank(queryKey)){
            wrapper.and(wrapper2->wrapper2.eq("id",queryKey).or().like("name",queryKey));
        }
        IPage<SpuEntity> page = pageParamVo.getPage();
        return new PageResultVo(baseMapper.selectPage(page,wrapper));
    }

    @Autowired
    private SpuDescService spuDescService;
    @Autowired
    private SpuAttrValueService spuAttrValueService;
    @Autowired
    private SkuService skuService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GlobalTransactional
    @Override
    public void bigSave(SpuVo spu) {
        //  保存spu信息
        SpuEntity spuEntity = getSpuEntity(spu);
        Long spuId = spuEntity.getId();
        Long spuBrandId = spuEntity.getBrandId();
        Long spuCategoryId = spuEntity.getCategoryId();

        // 保存spu_desc的信息
        spuDescService.saveSpuDesc(spu,spuId);

        // 保存spu_attr_value
        spuAttrValueService.saveSpuAttrValue(spu,spuId);

        // 2.保存sku信息
        skuService.saveSkus(spu, spuId, spuBrandId, spuCategoryId);

        rabbitTemplate.convertAndSend("PMS_GOODS_INSERT_EXCHANGE", "item.insert",spuId);
    }



    private SpuEntity getSpuEntity(SpuVo spu) {
        SpuEntity spuEntity = new SpuEntity();
        BeanUtils.copyProperties(spu,spuEntity);
        // 设置商品添加和更新的时间
        spuEntity.setCreateTime(new Date());
        spuEntity.setUpdateTime(spuEntity.getCreateTime());
        this.save(spuEntity);
        return spuEntity;
    }


}