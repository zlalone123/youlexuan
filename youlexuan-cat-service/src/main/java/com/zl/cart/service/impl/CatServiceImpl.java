package com.zl.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zl.cart.service.CatService;
import com.zl.group.CartVO;
import com.zl.mapper.TbItemMapper;
import com.zl.pojo.TbItem;
import com.zl.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CatServiceImpl implements CatService {
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<CartVO> addGoodsToCartList(List<CartVO> cartList, Long itemId, Integer num) {

        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);


        //2.获取商家ID
        String sellerId = item.getSellerId();

        //3.根据商家ID判断购物车列表中是否存在该商家的购物车

        CartVO cart = searchCartBySellerId(cartList, sellerId);

        //4.如果购物车列表中不存在该商家的购物车
        if (cart == null) {
            //4.1 新建购物车对象 ，
            cart = new CartVO();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item, num);
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2将购物车对象添加到购物车列表
            cartList.add(cart);
        } else {
            //5.如果购物车列表中存在该商家
            // 判断购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);

            if (orderItem == null) {
                //5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(
                        new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                //如果数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);//移除购物车明细
                }

                //如果移除后cart的明细数量为0，则将cart移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }


    /**
     * 根据商家ID查询此商家是否已经存在
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private CartVO searchCartBySellerId(List<CartVO> cartList, String sellerId) {

        if(cartList.size()>0){
            for (CartVO cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {

                    //商家已经存在
                    return cart;
                }
            }
        }

        //不存在
        return null;
    }

    /**
     * 根据商品明细ID查询商品是否已经存在
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建订单明细
     *
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }
}


