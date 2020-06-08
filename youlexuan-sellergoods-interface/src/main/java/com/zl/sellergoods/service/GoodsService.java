package com.zl.sellergoods.service;
import com.zl.entity.PageResult;
import com.zl.group.GoodsVO;
import com.zl.pojo.TbGoods;
import com.zl.pojo.TbItem;

import java.util.List;

/**
 * 商品服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(GoodsVO goodsVO);
	
	
	/**
	 * 修改
	 */
	public void update(GoodsVO goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsVO findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	/**
	 * 改变商品的审核状态
	 * @param ids
	 * @param auditStatus
	 */
	void updateAuditStatus(Long[] ids, String auditStatus);

	/**
	 * 根据商品ID和状态查询Item表信息
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status );

	/**
	 * 商品的上下架
	 * @param ids
	 * @param status
	 */
	public void updateIsMarketable(Long[] ids, String status);
}
