package com.zl.page.service.impl;

import com.zl.mapper.TbGoodsDescMapper;
import com.zl.mapper.TbGoodsMapper;
import com.zl.mapper.TbItemCatMapper;
import com.zl.mapper.TbItemMapper;
import com.zl.page.service.ItemPageService;
import com.zl.pojo.TbGoods;
import com.zl.pojo.TbGoodsDesc;
import com.zl.pojo.TbItem;
import com.zl.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    /**
     * 生成商品详情页
     *
     * @param goodsId
     * @return
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();

            //默认加载的是webapp下的WEB-INF下的ftl文件
            Template template = configuration.getTemplate("item.ftl");
            Map dataMap = new HashMap();

            //tb_goods tb_item tb_goods_desc
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);

            //商品的sku类表
            List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);

            dataMap.put("tbGoods", tbGoods);
            dataMap.put("tbGoodsDesc", tbGoodsDesc);
            dataMap.put("tbItems", tbItems);

            //三级商品分类名称
            dataMap.put("itemCat1", tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
            dataMap.put("itemCat2", tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
            dataMap.put("itemCat3", tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());

            FileWriter fileWriter = new FileWriter(new File(pagedir + goodsId + ".html"));
            try {

                //生成HTML页面
                template.process(dataMap, fileWriter);
                fileWriter.close();
                return true;
            } catch (TemplateException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                new File(pagedir + goodsId + ".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
