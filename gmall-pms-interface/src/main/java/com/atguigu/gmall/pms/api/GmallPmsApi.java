package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zqq
 * @create 2021-01-28 12:02
 */

public interface GmallPmsApi {
    // 远程调用:分页查询已经上架的spu信息
    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    @PostMapping("pms/spu/json")
     ResponseVo<List<SpuEntity>> querySpusByPage(@RequestBody PageParamVo pageParamVo);

    //根据spuid查询spu
    @GetMapping("pms/spu/{id}")
    public ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    // 根据spuid查询sku信息
    @GetMapping("pms/sku/spu/{spuId}")
    ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    // 根据品牌id 查询brand信息
    @GetMapping("pms/brand/{id}")
     ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    // 根据分类id 查询category信息
    @GetMapping("pms/category/{id}")
     ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    // 根据sku中的三级分类id查询一二三级分类
    @GetMapping("pms/category/level123/{level3Cid}")
    public ResponseVo<List<CategoryEntity>> queryLevel123CategoriesByLevel3Id(@PathVariable("level3Cid") Long level3Cid);

    // 根据parentId 查询商品分类数据  parentId: -1：查询所有，0：查询一级节点
    @GetMapping("pms/category/parent/{parentId}")
    public ResponseVo<List<CategoryEntity>> getCategorysByParentId(@PathVariable("parentId") Long pid );

    @GetMapping("pms/category/parent/withSubs/{pid}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesWithSubsByPid(@PathVariable Long pid);

    //根据skuId查询sku所有图片
    @GetMapping("pms/skuimages/images/{skuId}")
    public ResponseVo<List<SkuImagesEntity>> querySkuImagesBySkuId(@PathVariable("skuId") Long skuId);

    // 根据分类id和spuid查询spu普通属性
    @GetMapping("pms/spuattrvalue/search/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> querySpuAttrsBySpuIDAndCategoryId(
            @RequestParam("categoryId") Long cid,
            @PathVariable("spuId") Long spuId);

    // 根据分类id和skuid查询spu销售属性
    @GetMapping("pms/skuattrvalue/search/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuAttrsBySkuIdAndCategoryId(
            @RequestParam("categoryId") Long cid,
            @PathVariable("skuId") Long skuId);


    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySkuAttrValuesBySpuId(@PathVariable("spuId")Long spuId);

    //查找当前sku下的所有销售属性
    @GetMapping("pms/skuattrvalue/skuAttr/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuAttrSBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuattrvalue/spu/sku/{spuId}")
    public ResponseVo<String> querySkusJsonBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/spudesc/{spuId}")
    public ResponseVo<List<SpuDescEntity>> querySpuDescById(@PathVariable("spuId") Long spuId);

    // 查询组及组下参数和值
    @GetMapping("pms/attrgroup/withattrvalues")
    public ResponseVo<List<ItemGroupVo>> queryGroupsBySpuIdAndCid(
            @RequestParam("spuId")Long spuId,
            @RequestParam("skuId")Long skuId,
            @RequestParam("cid")Long cid
    );
}
