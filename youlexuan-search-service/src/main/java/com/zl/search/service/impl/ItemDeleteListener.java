package com.zl.search.service.impl;

import com.zl.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService  itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] ids  = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听接收到消息..."+ids);
           // List<Long> list = Arrays.stream(ids).map(id -> Long.valueOf(id)).collect(Collectors.toList());

            //删除solr中的对应的数据
            itemSearchService.deleteByGoodsIds(ids);
            System.out.println("成功删除索引库中的记录");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
