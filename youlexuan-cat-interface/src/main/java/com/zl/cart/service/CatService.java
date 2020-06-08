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




}
