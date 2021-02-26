package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.exception.CartException;
import com.atguigu.gmall.cart.pojo.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zqq
 * @create 2021-02-24 10:25
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    /**
     * 购物车添加商品
     */
    @GetMapping("/")
    public String addCart(Cart cart){
        //判断商品是否存在
        if (cart==null||cart.getSkuId()==null){
            throw new CartException("添加的商品不存在");
        }
        this.cartService.addCart(cart);
        return "redirect:http://cart.gmall.com/addCart.html?skuId="+cart.getSkuId();

    }

    //跳转到添加成功页面

    @GetMapping("addCart.html")
    public String addSuccess(@RequestParam("skuId") Long skuId, Model model){
       Cart cart= this.cartService.queryCartBySkuId(skuId);
       model.addAttribute("cart",cart);
       return "addCart";
    }
    /**
     * 查询购物车商品
     */
    @GetMapping("cart.html")
    public String queryCarts(Model model){
        List<Cart> carts=this.cartService.queryCarts();
        model.addAttribute("carts",carts);
        return "cart";
    }

    /**
     * 购物车更新商品
     */
    @PostMapping("updateNum")
    @ResponseBody
    public ResponseVo updateNum(@RequestBody(required = true) Cart cart){

        this.cartService.updateNum(cart);


        return ResponseVo.ok();
    }
    /**
     * 购物车根据skuId删除商品
     */

    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo deleteCart(@RequestParam(value = "skuId",required = true) Long skuId){

        this.cartService.deleteCart(skuId);


        return ResponseVo.ok();
    }
    /**
     * 购物车根据选中状态删除商品
     */
}
