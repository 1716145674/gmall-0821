package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.vo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zqq
 * @create 2021-01-28 15:34
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
