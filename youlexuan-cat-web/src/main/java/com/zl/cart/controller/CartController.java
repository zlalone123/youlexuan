package com.zl.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zl.cart.service.CatService;
import com.zl.entity.Result;
import com.zl.group.CartVO;
import com.zl.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    @SuppressWarnings("all")
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

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if("anonymousUser".equals(username)){
            //如果未登录
            return cookieCartList;
        }else{
            //已经登录，整合cookie和Redis中的购物车
            List<CartVO> cartList_redis =cartService.findCartListFromRedis(username);//从redis中提取
            if(cookieCartList.size()>0){//如果本地存在购物车
                //合并购物车
                cartList_redis=cartService.mergeCartList(cartList_redis, cookieCartList);
                //清除本地cookie的数据
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);
                System.out.println("从Redis中获得数据");
            }
            return cartList_redis;
        }


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
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addGoodsToCartList(Long itemId, Integer num) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            List<CartVO> cartList = findCartList();//获取购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)) {
                //如果是未登录，保存到cookie
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");

            } else {
                //如果是已登录，保存到redis
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "添加成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    @RequestMapping("/getLoginName")
    public Result getLoginName(){
        //判断用户是否已经登录
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return new Result(true,username);
    }
}



