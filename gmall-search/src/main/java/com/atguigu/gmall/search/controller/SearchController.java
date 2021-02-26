package com.atguigu.gmall.search.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.pkcs11.Secmod;

/**
 * @author zqq
 * @create 2021-01-29 18:24
 */
@Controller
@RequestMapping("search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public String search(SearchParamVo searchParamVo, Model model) {
        SearchResponseVo searchResponseVo = searchService.search(searchParamVo);
        model.addAttribute("response",searchResponseVo);
        model.addAttribute("searchParam",searchParamVo);
        return "search";
    }
}
