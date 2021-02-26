package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;

/**
 * @author zqq
 * @create 2021-01-29 18:26
 */

public interface SearchService {
    SearchResponseVo search(SearchParamVo searchParamVo);
}
