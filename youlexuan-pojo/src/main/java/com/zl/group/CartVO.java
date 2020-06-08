package com.zl.group;

import com.zl.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class CartVO implements Serializable {

    //商家id
    private String sellerId ;

    //商家名称
    private String sellerName;

    //商品订单明细
    private List<TbOrderItem> orderItemList;


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
