package com.zl.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zl.entity.PageResult;
import com.zl.group.CartVO;
import com.zl.mapper.TbOrderItemMapper;
import com.zl.mapper.TbOrderMapper;
import com.zl.mapper.TbPayLogMapper;
import com.zl.order.service.OrderService;
import com.zl.pojo.*;
import com.zl.pojo.TbOrderExample.Criteria;
import com.zl.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbPayLogMapper payLogMapper;


    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        // 得到购物车数据
        List<CartVO> cartList = (List<CartVO>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        double totalFee = 0;
        List<String> orderIdList = new ArrayList<>();
        for (CartVO cart : cartList) {

            //生成订单编号
            long orderId = idWorker.nextId();

            //记录本次所有的订单号
            orderIdList.add(orderId + "");

            TbOrder tborder = new TbOrder();// 新创建订单对象
            tborder.setOrderId(orderId+"");// 订单ID
            tborder.setUserId(order.getUserId());// 用户名
            tborder.setPaymentType(order.getPaymentType());// 支付类型
            tborder.setStatus("1");// 状态：未付款
            tborder.setCreateTime(new Date());// 订单创建日期
            tborder.setUpdateTime(new Date());// 订单更新日期
            tborder.setReceiverAreaName(order.getReceiverAreaName());// 地址
            tborder.setReceiverMobile(order.getReceiverMobile());// 手机号
            tborder.setReceiver(order.getReceiver());// 收货人
            tborder.setSourceType(order.getSourceType());// 订单来源
            tborder.setSellerId(cart.getSellerId());// 商家ID

            // 循环购物车明细
            double money = 0;

            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId+"");// 订单ID
                orderItem.setSellerId(cart.getSellerId());
                money += orderItem.getTotalFee().doubleValue();// 金额累加
                totalFee += money;
                orderItemMapper.insert(orderItem);
            }
            tborder.setPayment(new BigDecimal(money));
            orderMapper.insert(tborder);
        }

        if ("1".equals(order.getPaymentType())) {
            //生成支付log
            TbPayLog payLog = new TbPayLog();

            //补齐参数
            payLog.setCreateTime(new Date());
            //支付订单号
            payLog.setOutTradeNo(idWorker.nextId() + "");
            //payLog.setPayTime(支付时间);
            payLog.setPayType("1");
            payLog.setTotalFee(new BigDecimal(totalFee));//订单的总价
            payLog.setTradeState("0");
            payLog.setUserId(order.getUserId());
            //payLog.setTransactionId(交易的流水单号);

            String orderIds = orderIdList.toString().replace("[", "").replace("]", "").replaceAll(" ", "");
            payLog.setOrderList(orderIds);//一个商家对应一个订单


            payLogMapper.insert(payLog);//添加支付日志到数据库

            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//添加支付日志到Redis中

        }

        //删除Redis中的购物车数据
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param
     * @return
     */
    @Override
    public TbOrder findOne(Long orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] orderIds) {
        for (Long orderId : orderIds) {
            orderMapper.deleteByPrimaryKey(orderId);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }
        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {

        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(out_trade_no);
        //修改支付记录的付款时间
        payLog.setPayTime(new Date());
        //支付记录交易流水单号
        payLog.setTransactionId(transaction_id);
        //支付记录修改支付状态
        payLog.setTradeState("2");

        payLogMapper.updateByPrimaryKeySelective(payLog);

        //订单的状态
        TbPayLog payLog1 = payLogMapper.selectByPrimaryKey(out_trade_no);
        String orderList = payLog1.getOrderList();
        String[] orderIds = orderList.split(",");

        for (String orderId : orderIds) {
            TbOrder order = new TbOrder();
            order.setStatus("2");
            order.setPaymentTime(new Date());
            order.setOrderId(orderId);
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //删除Redis中的缓存
        redisTemplate.boundHashOps("payLog").delete(payLog1.getUserId());
    }
}
