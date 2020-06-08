package com.zl.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.zl.pojo.TbItem;
import com.zl.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 30000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        //空格处理
        String keywords = (String) searchMap.get("keywords");
        if(keywords !=null && keywords.length()>0){
            searchMap.put("keywords", keywords.replace(" ", ""));

            Map<String, Object> map = new HashMap<>();

            //1.按关键字查询（高亮显示）
            Map highlightList = searchList(searchMap);
            map.put("highlightList", highlightList);

            //2.根据关键字查询商品分类
            List categoryList = searchCategoryList(searchMap);

            //3.查询品牌和规格列表
            //读取分类名称
            String categoryName = (String) searchMap.get("category");
            if (!"".equals(categoryName)) {
                //按照分类名称重新读取对应品牌、规格
                map.putAll(searchBrandAndSpecList(categoryName));
            } else {
                if (categoryList.size() > 0) {
                    Map mapBrandAndSpec = searchBrandAndSpecList((String) categoryList.get(0));
                    map.putAll(mapBrandAndSpec);
                }
            }
            return map;
        }

        return null;
    }

    /**
     * 条件查询，高亮处理
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();

        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //3、设定需要高亮处理字段
        highlightOptions.addField("item_title");
        //4、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //5、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //6、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);

        //7、设定查询条件 根据关键字查询
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象

        //条件1 查询关键字
        query.addCriteria(criteria);

        //条件2 ：分类
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //条件3 品牌
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //条件4：规格
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map) searchMap.get("spec");//"spec":{"网络":"移动3G","机身内存":"16G"}
            for (String key : spec.keySet()) {
                String s = "item_spec_" + key;// item_spec_网络

                Criteria criteria4 = new Criteria(s).is(spec.get(key));
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria4);
                query.addFilterQuery(filterQuery);
            }
        }

        //条件5：价格区间
        String price = searchMap.get("price") + "";
        if(price!=null&&price.length()>0){
            String[] priceArray = price.split("-");
            String begin = priceArray[0];
            String end= priceArray[1];
            Criteria item_price = new Criteria("item_price").greaterThanEqual(begin);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(item_price);
            query.addFilterQuery(simpleFilterQuery);

            if(!end.equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(end);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //条件6 ：排序
        String sortValue =  searchMap.get("sort")+"";//排序的方向（ASC DESC）
        String sortField = searchMap.get("sortField")+"";///排序的字段
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }


        //8、发出带高亮数据查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //9、获取查询结果记录集合
        List<TbItem> list = highlightPage.getContent();
        //10、循环集合对象
        for (TbItem item : list) {
            //获取到针对对象TbItem高亮集合
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if (highlights != null && highlights.size() > 0) {
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();

                //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));

            }
        }
        map.put("rows", highlightPage.getContent());

        return map;
    }

    /**
     * 查询分类列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for(TbItem item:list){
            Map specMap = JSON.parseObject(item.getSpec(),Map.class);
            item.setSpecMap(specMap);	//给带动态域注解的字段赋值
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除solr库中的数据
     * @param ids
     */
    @Override
    public void deleteByGoodsIds(Long[] ids) {
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        simpleQuery.addCriteria(criteria);

        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();
    }
}
