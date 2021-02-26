package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zqq
 * @create 2021-02-19 19:14
 */
@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;
    @Test
    void queryLevel123CategoriesByLevel3Id() {
        List<CategoryEntity> categoryEntities = categoryService.queryLevel123CategoriesByLevel3Id(225l);
        System.out.println(categoryEntities);
    }
}