package com.zl.solrutil;

import com.alibaba.fastjson.JSON;
import com.zl.mapper.TbItemMapper;
import com.zl.pojo.TbItem;
import com.zl.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 获取所有的审核通过的商品
     */
    public void importItemData() {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//已审核
        List<TbItem> itemList = tbItemMapper.selectByExample(example);

        for (TbItem item : itemList) {

            //将spec字段中的json字符串转换为map
            Map specMap = JSON.parseObject(item.getSpec());
            item.setSpecMap(specMap);//给带动态域注解的字段赋值
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }


    public void cleanItemData(){
        SimpleQuery simpleQuery = new SimpleQuery("*:*");
        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();
    }
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        //solrUtil.importItemData();
        solrUtil.cleanItemData();
    }
}
