package com.zl.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zl.cart.service.CatService;
import com.zl.entity.Result;
import com.zl.group.CartVO;
import com.zl.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 10000)
    private CatService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    /**
     * 购物车列表
     * @param
     * @return
     */
    @RequestMapping("/findCartList")
    public List<CartVO> findCartList(){
        String cartListString = CookieUtil.getCookieValue(request,"cartList","UTF-8");
        if(cartListString==null || cartListString.equals("")){
            cartListString="[]";
        }
        List<CartVO> cookieCartList = JSON.parseArray(cartListString, CartVO.class);
        return cookieCartList;
    }

    /**
     * 添加商品到购物车
     * @param
     * @param
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        try {
            //获取购物车列表
            List<CartVO> cartList =findCartList();

            //添加商品到集合
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            //将集合保存到cookie
            CookieUtil.setCookie(request, response,
                    "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}



