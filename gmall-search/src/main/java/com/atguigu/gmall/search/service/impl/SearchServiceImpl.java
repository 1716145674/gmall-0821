package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.Goods;
import com.atguigu.gmall.search.vo.SearchParamVo;
import com.atguigu.gmall.search.vo.SearchResponseAttrVo;
import com.atguigu.gmall.search.vo.SearchResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zqq
 * @create 2021-01-29 18:27
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        SearchResponseVo responseVo = null;
        try {
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, paresSearchParamToDsl(searchParamVo));
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            responseVo = paresSearchResponseToSearchResponseVo(response);
            responseVo.setPageNum(searchParamVo.getPageNum());
            responseVo.setPageSize(searchParamVo.getPageSize());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return responseVo;
    }

    private SearchSourceBuilder paresSearchParamToDsl(SearchParamVo searchParamVo) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 1. 封装query
        //  bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // match配置查询
        String keyword = searchParamVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            // TODO
            return null;
        }
        boolQuery.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));

        // 过滤brand
        List<Long> brandIds = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandIds)) {
            boolQuery.filter(new TermsQueryBuilder("brandId", brandIds));
        }
        // 过滤category
        List<Long> categoryIds = searchParamVo.getCategoryId();
        if (!CollectionUtils.isEmpty(categoryIds)) {
            boolQuery.filter(new TermsQueryBuilder("categoryId", categoryIds));
        }
        // 过滤是否有货
        Boolean store = searchParamVo.getStore();
        if (store != null && store) {
            boolQuery.filter(new TermQueryBuilder("store", store));
        }
        // 过滤价格
        Double priceFrom = searchParamVo.getPriceFrom();
        Double priceTo = searchParamVo.getPriceTo();
        if (priceFrom != null || priceTo != null) {
            RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("price");
            if (priceFrom != null) {
                boolQuery.filter(rangeQueryBuilder.gte(priceFrom));
            }
            if (priceTo != null) {
                boolQuery.filter(rangeQueryBuilder.lte(priceTo));
            }

        }
        // 过滤属性
        List<String> props = searchParamVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                String[] splitStr = StringUtils.split(prop, ":");
                if (splitStr != null && splitStr.length == 2) {

                    String SearchAttrId = splitStr[0];
                    String[] SearchAttrVals = StringUtils.split(splitStr[1], "-");

                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                    boolQueryBuilder.must(new TermQueryBuilder("searchAttrs.attrId", SearchAttrId));
                    boolQueryBuilder.must(new TermsQueryBuilder("searchAttrs.attrValue", SearchAttrVals));

                    boolQuery.filter(new NestedQueryBuilder("searchAttrs", boolQueryBuilder, ScoreMode.None));
                }
            });
        }
        sourceBuilder.query(boolQuery);
        // 2. 封装排序
        switch (searchParamVo.getSort()) {
            case 1:
                sourceBuilder.sort("price", SortOrder.ASC);
                break;
            case 2:
                sourceBuilder.sort("price", SortOrder.DESC);
                break;
            case 3:
                sourceBuilder.sort("sales", SortOrder.DESC);
                break;
            case 4:
                sourceBuilder.sort("createTime", SortOrder.DESC);
                break;
        }
        // 3. 封装分页
        Integer pageNum = searchParamVo.getPageNum();
        Integer pageSize = searchParamVo.getPageSize();

        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);

        // 4. 封装高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").preTags("<em>").postTags("</em>");
        sourceBuilder.highlighter(highlightBuilder);
        // 5. 封装聚合
        //品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("brandLogoAgg").field("logo")));

        // 分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));

        // 属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue"))));

        sourceBuilder.fetchSource(new String[]{"skuId", "defaultImage", "price", "title", "subTitle"}, null);
        System.out.println(sourceBuilder);
        return sourceBuilder;
    }

    private SearchResponseVo paresSearchResponseToSearchResponseVo(SearchResponse response) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析hits
        SearchHits hits = response.getHits();
        searchResponseVo.setTotal(hits.getTotalHits());
        SearchHit[] hitsHits = hits.getHits();
        List<Goods> goodsList = Stream.of(hitsHits).map(hitsHit -> {
            String json = hitsHit.getSourceAsString();
            Goods goods = JSON.parseObject(json, Goods.class);
            Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
            String title = highlightFields.get("title").getFragments()[0].string();
            goods.setTitle(title);
            return goods;
        }).collect(Collectors.toList());
        searchResponseVo.setGoodsList(goodsList);
        //解析
        Aggregations aggregations = response.getAggregations();
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        ParsedLongTerms brandIdAgg = (ParsedLongTerms) asMap.get("brandIdAgg");
        List<? extends Terms.Bucket> brandIdBuckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(brandIdBuckets)) {

            List<BrandEntity> brandEntityList = brandIdBuckets.stream().map(bucket -> {
                        BrandEntity brandEntity = new BrandEntity();
                        long id = bucket.getKeyAsNumber().longValue();
                        brandEntity.setId(id);
                        Map<String, Aggregation> brandIdSubAggs = bucket.getAggregations().getAsMap();
                        ParsedStringTerms brandNameAgg = (ParsedStringTerms) brandIdSubAggs.get("brandNameAgg");
                        brandEntity.setName(brandNameAgg.getBuckets().get(0).getKeyAsString());
                        ParsedStringTerms brandLogoAgg = (ParsedStringTerms) brandIdSubAggs.get("brandLogoAgg");
                        brandEntity.setLogo(brandLogoAgg.getBuckets().get(0).getKeyAsString());
                        return brandEntity;
                    }
            ).collect(Collectors.toList());


            searchResponseVo.setBrands(brandEntityList);
        }
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms) asMap.get("categoryIdAgg");
        List<? extends Terms.Bucket> categoryIdAggBuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(categoryIdAggBuckets)) {

            List<CategoryEntity> categoryEntityList = categoryIdAggBuckets.stream().map(bucket -> {
                        CategoryEntity categoryEntity = new CategoryEntity();

                        long id = bucket.getKeyAsNumber().longValue();
                        categoryEntity.setId(id);
                        Map<String, Aggregation> categoryIdSubAggs = bucket.getAggregations().getAsMap();
                        ParsedStringTerms brandNameAgg = (ParsedStringTerms) categoryIdSubAggs.get("categoryNameAgg");
                        categoryEntity.setName(brandNameAgg.getBuckets().get(0).getKeyAsString());
                        return categoryEntity;
                    }
            ).collect(Collectors.toList());

            searchResponseVo.setCategories(categoryEntityList);
        }

        ParsedNested attrAgg = (ParsedNested) asMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();

        if (!CollectionUtils.isEmpty(attrIdAggBuckets)) {

            List<SearchResponseAttrVo> searchResponseAttrVoList = (List<SearchResponseAttrVo>) attrIdAggBuckets.stream().map(bucket -> {

                        SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                        searchResponseAttrVo.setAttrId(bucket.getKeyAsNumber().longValue());

                        Map<String, Aggregation> subAggMap = bucket.getAggregations().getAsMap();
                        ParsedStringTerms attrNameAgg = (ParsedStringTerms) subAggMap.get("attrNameAgg");
                        searchResponseAttrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());

                        ParsedStringTerms attrValueAgg = (ParsedStringTerms) subAggMap.get("attrValueAgg");
                        List<? extends Terms.Bucket> valuesBuckets = attrValueAgg.getBuckets();
                        if (!CollectionUtils.isEmpty(valuesBuckets)) {
                            searchResponseAttrVo.setAttrValues(valuesBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList()));
                        }
                        return searchResponseAttrVo;
                    }
            ).collect(Collectors.toList());
            searchResponseVo.setFilters(searchResponseAttrVoList);
        }

        return searchResponseVo;
    }
}
