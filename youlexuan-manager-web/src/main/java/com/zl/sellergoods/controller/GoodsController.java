package com.zl.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.zl.entity.PageResult;
import com.zl.entity.Result;
import com.zl.group.GoodsVO;
import com.zl.pojo.TbGoods;
import com.zl.pojo.TbItem;
import com.zl.sellergoods.service.GoodsService;
import com.zl.sellergoods.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * 商品controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemService itemService;

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDestination;
	@Autowired
	private Destination topicPageDestination;


	/**
	 * 生成静态页（测试）
	 * @param goodsId
	 *//*
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}*/

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
/*	@RequestMapping("/add")
	public Result add(@RequestBody TbGoods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}*/

	/**
	 * 修改
	 * @param goods
	 * @return
	 */
/*	@RequestMapping("/update")
	public Result update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}*/
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public GoodsVO findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


	@RequestMapping("/updateAuditStatus")
	public Result updateAuditStatus(Long[] ids,String auditStatus ){
		try {
			goodsService.updateAuditStatus(ids,auditStatus);
			if(auditStatus.equals("1")){//审核通过
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, auditStatus);
				//调用搜索接口实现数据批量导入到solr中
				if(itemList.size()>0){
					//itemSearchService.importList(itemList);

					//将itemList集合转为消息字符串
					final String text = JSON.toJSONString(itemList);

					//发送消息到消息队列中
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(text);
						}
					});

				}else{
					System.out.println("没有明细数据");
				}

				//静态页生成
				for(final Long goodsId:ids){
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							System.out.println("通知生成静态网页");
							return session.createTextMessage(goodsId+"");
						}
					});
				}
			}
			return new Result(true,"操作成功");
		} catch (Exception e) {
			System.out.println(e);
			return new Result(false,"操作失败");

		}
	}
	
}
