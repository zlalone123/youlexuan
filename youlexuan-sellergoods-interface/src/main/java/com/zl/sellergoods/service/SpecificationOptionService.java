package com.zl.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.zl.pojo.TbSpecificationOption;

import com.zl.entity.PageResult;
/**
 * 规格选项服务层接口
 * @author Administrator
 *
 */
public interface SpecificationOptionService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecificationOption> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSpecificationOption specification_option);
	
	
	/**
	 * 修改
	 */
	public void update(TbSpecificationOption specification_option);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSpecificationOption findOne(Long id);
	
	
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
	public PageResult findPage(TbSpecificationOption specification_option, int pageNum, int pageSize);


	
}
