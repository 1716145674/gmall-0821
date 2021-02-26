package com.atguigu.gmall.pms.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> getCategorysByParentId(Long pid) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if (pid == -1) {
            return baseMapper.selectList(wrapper);
        }
        wrapper.eq("parent_id", pid);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CategoryEntity> queryCategoriesWithSubsByPid(Long pid) {
        return baseMapper.queryCategoriesWithSubsByPid(pid);
    }

    @Override
    public List<CategoryEntity> queryLevel123CategoriesByLevel3Id(Long level3Cid) {
        CategoryEntity level3 = this.getOne(new QueryWrapper<CategoryEntity>().eq("id", level3Cid));
        if (level3 == null) {
            return null;
        }
        CategoryEntity level2 = this.getOne(new QueryWrapper<CategoryEntity>().eq("id", level3.getParentId()));
        CategoryEntity level1 = this.getOne(new QueryWrapper<CategoryEntity>().eq("id", level2.getParentId()));
        return Arrays.asList(level1,level2,level3);
    }

}