package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zqq
 * @create 2021-02-02 20:46
 */
@Controller
public class IndexController {


    @Autowired
    private IndexService indexService;
    @GetMapping
    public String toIndex(Model model) {

        // 获取一级分类
        List<CategoryEntity> categories = indexService.queryLv1Categories();
        model.addAttribute("categories",categories);
        return "index";
    }
    @GetMapping("index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLv2CategoriesByParentId(@PathVariable Long pid, Model model) {

        // 获取二级分类
        List<CategoryEntity> categoryEntities=indexService.queryLv2CategoriesByParentId(pid);
        return ResponseVo.ok(categoryEntities);
    }

}
