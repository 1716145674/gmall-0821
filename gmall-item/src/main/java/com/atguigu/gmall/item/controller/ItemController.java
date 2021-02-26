package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zqq
 * @create 2021-02-21 15:56
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
    public String load(Model model, @PathVariable("skuId") Long skuId){

        ItemVo itemVo=itemService.load(skuId);
        model.addAttribute("itemVo",itemVo );

//        itemService.asyncExecute(skuId);

        return "item";
    }
}
