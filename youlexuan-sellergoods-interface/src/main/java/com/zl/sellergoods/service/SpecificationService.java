package com.zl.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.zl.group.SpecificationVO;
import com.zl.pojo.TbSpecification;

import com.zl.entity.PageResult;
/**
 * 规格服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(SpecificationVO specificationVO);
	
	
	/**
	 * 修改
	 */
	public void update(SpecificationVO specificationVO);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public SpecificationVO findOne(Long id);
	
	
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
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize);
	public List<Map> selectOptionList();
}
