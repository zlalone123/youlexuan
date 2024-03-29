package com.zl.sellergoods.service;
import java.util.List;
import com.zl.pojo.TbGoodsDesc;

import com.zl.entity.PageResult;
/**
 * 商品详情服务层接口
 * @author Administrator
 *
 */
public interface GoodsDescService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoodsDesc> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbGoodsDesc goods_desc);
	
	
	/**
	 * 修改
	 */
	public void update(TbGoodsDesc goods_desc);
	

	/**
	 * 根据ID获取实体
	 * @param
	 * @return
	 */
	public TbGoodsDesc findOne(Long goodsId);
	
	
	/**
	 * 批量删除
	 * @param
	 */
	public void delete(Long[] goodsIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoodsDesc goods_desc, int pageNum, int pageSize);
	
}
