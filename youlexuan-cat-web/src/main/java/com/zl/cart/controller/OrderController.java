package com.zl.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zl.entity.PageResult;
import com.zl.entity.Result;
import com.zl.order.service.OrderService;
import com.zl.pojo.TbOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference(timeout = 10000)
	private OrderService orderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){			
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){

		//获取当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setUserId(username);
		order.setSourceType("2");//订单来源  PC
		try {
			orderService.add(order);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long orderId){
		return orderService.findOne(orderId);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] orderIds){
		try {
			orderService.delete(orderIds);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}
	
}
