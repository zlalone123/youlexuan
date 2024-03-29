package com.zl.content.service;
import java.util.List;
import com.zl.pojo.TbContent;

import com.zl.entity.PageResult;
/**
 * 内容服务层接口
 * @author Administrator
 *
 */
public interface ContentService {

	/**
	 * 根据广告分类来查询广告
	 * @param categoryId
	 * @return
	 */
	public List<TbContent> findByContentCategoryId(long categoryId);

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbContent> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbContent content);
	
	
	/**
	 * 修改
	 */
	public void update(TbContent content);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbContent findOne(Long id);
	
	
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
	public PageResult findPage(TbContent content, int pageNum, int pageSize);
	
}
