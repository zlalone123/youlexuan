package com.zl.search.service;

import com.zl.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * portal商城的端的搜索
     * @param searchMap  查询条件（例如：keywords）
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 导入数据
     * @param list
     */
    public void importList(List<TbItem> list);

    /**
     * 从solr中删除商品的sku列表
     * @param ids
     */
    public void deleteByGoodsIds(Long[] ids);

}
