package com.zl.cart.service;

import com.zl.group.CartVO;

import java.util.List;

public interface CatService {

    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     */
    public List<CartVO> addGoodsToCartList(List<CartVO> cartList,Long itemId,Integer num );


    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<CartVO> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<CartVO> cartList);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<CartVO> mergeCartList(List<CartVO> cartList1,List<CartVO> cartList2);

}
