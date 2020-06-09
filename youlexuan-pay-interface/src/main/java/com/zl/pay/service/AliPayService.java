package com.zl.pay.service;

import java.util.Map;

public interface AliPayService {
    /**
     * 生成支付宝支付二维码
     * @param out_trade_no 订单号（支付单号）
     * @param total_fee 金额(分)  金额单位是“分”
     * @return
     */
    public Map createNative(String out_trade_no, Double total_fee);

    /**
     * 查询支付状态
     * @param out_trade_no
     */
    public Map queryPayStatus(String out_trade_no);
}
