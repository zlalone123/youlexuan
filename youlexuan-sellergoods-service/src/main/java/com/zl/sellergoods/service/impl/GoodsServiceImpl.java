package com.zl.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zl.entity.PageResult;
import com.zl.group.GoodsVO;
import com.zl.mapper.*;
import com.zl.pojo.*;
import com.zl.pojo.TbGoodsExample.Criteria;
import com.zl.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(GoodsVO goodsVO) {

        try {
            //补全商品信息
            goodsVO.getGoods().setAuditStatus("0");
            //添加商品基本信息
            goodsMapper.insert(goodsVO.getGoods());
            Long id = goodsVO.getGoods().getId();


            //添加商品描述信息
            goodsVO.getGoodsDesc().setGoodsId(id);
            tbGoodsDescMapper.insert(goodsVO.getGoodsDesc());

            if ("1".equals(goodsVO.getGoods().getIsEnableSpec())) {

                //添加商品的SKU信息
                List<TbItem> items = goodsVO.getItemList();

                for (TbItem item : items) {
                    //title sup名称 +规格选项
                    String goodsName = goodsVO.getGoods().getGoodsName();
                    Map<String, Object> map = JSON.parseObject(item.getSpec(), Map.class);
                    for (String key : map.keySet()) {
                        goodsName += " " + map.get(key);
                    }

                    String title = goodsName;
                    item.setTitle(title);
                    setItemValus(goodsVO, item);

                    tbItemMapper.insert(item);
                }
            } else {

                TbItem tbItem = new TbItem();
                tbItem.setTitle(goodsVO.getGoods().getGoodsName());
                setItemValus(goodsVO, tbItem);
                tbItem.setPrice(goodsVO.getGoods().getPrice());
                tbItem.setNum(99);
                tbItem.setIsDefault("1");
                tbItem.setStatus("1");
                tbItemMapper.insert(tbItem);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setItemValus(GoodsVO goodsVO,TbItem item) {
        //商品id
        item.setGoodsId(goodsVO.getGoods().getId());

        //创建时间
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());

        item.setSellerId(goodsVO.getGoods().getSellerId());
        item.setCategoryid(goodsVO.getGoods().getCategory3Id());

        //品牌名称
        item.setBrand(tbBrandMapper.selectByPrimaryKey(goodsVO.getGoods().getBrandId()).getName());
        //分类名称
        item.setCategory(tbItemCatMapper.selectByPrimaryKey(goodsVO.getGoods().getCategory3Id()).getName());
        item.setSeller(tbSellerMapper.selectByPrimaryKey(goodsVO.getGoods().getSellerId()).getNickName());
        item.setStatus("1");
        //图片
        String imageItem = goodsVO.getGoodsDesc().getItemImages();
        List<Map> images = JSON.parseArray(imageItem, Map.class);
        if (images != null && images.size() > 0) {
            item.setImage((String) images.get(0).get("url"));
        }
    }


    /**
     * 修改
     * 商品的审核状态需要修改
     */
    @Override
    public void update(GoodsVO goodsVO) {
        try {
            goodsVO.getGoods().setAuditStatus("0");
            //修改商品
            goodsMapper.updateByPrimaryKey(goodsVO.getGoods());
            //修改商品描述表
            tbGoodsDescMapper.updateByPrimaryKey(goodsVO.getGoodsDesc());

            //删除sku,重新添加
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(goodsVO.getGoods().getId());
            tbItemMapper.deleteByExample(tbItemExample);

            //添加sku
            insertItem(goodsVO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 添加sku列表
     * @param goodsVO
     */
    private void insertItem( GoodsVO goodsVO){
        if("1".equals(goodsVO.getGoods().getIsEnableSpec())){
            for(TbItem item :goodsVO.getItemList()){
                //标题
                String title= goodsVO.getGoods().getGoodsName();
                Map<String,Object> specMap = JSON.parseObject(item.getSpec());
                for(String key:specMap.keySet()){
                    title+=" "+ specMap.get(key);
                }
                item.setTitle(title);
                setItemValus(goodsVO,item);
               tbItemMapper.insert(item);
            }
        }else{
            TbItem item=new TbItem();
            item.setTitle(goodsVO.getGoods().getGoodsName());//商品KPU作为SKU名称
            item.setPrice( goodsVO.getGoods().getPrice() );//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(99999);//库存数量
            item.setSpec("{}");
            setItemValus(goodsVO,item);
            tbItemMapper.insert(item);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVO findOne(Long id) {
        //商品信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

        //商品描述信息
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);

        //item信息
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria1 = tbItemExample.createCriteria();
        criteria1.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);

        GoodsVO goodsVO = new GoodsVO();
        goodsVO.setGoods(tbGoods);
        goodsVO.setGoodsDesc(tbGoodsDesc);
        goodsVO.setItemList(tbItems);

        return goodsVO;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }

        //把sku的status设置为3
        List<TbItem> list = findItemListByGoodsIdandStatus(ids, "1");
        for (TbItem tbItem : list) {
            tbItem.setStatus("3");
            tbItemMapper.updateByPrimaryKey(tbItem);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        //未删除的数据
        criteria.andIsDeleteIsNull();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateAuditStatus(Long[] ids, String auditStatus) {
        for (long id : ids) {
            //修改商品的审核状态
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(auditStatus);
            goodsMapper.updateByPrimaryKey(tbGoods);

            //修改item的status
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
            for (TbItem tbItem : tbItems) {
                tbItem.setStatus(auditStatus);
                tbItemMapper.updateByPrimaryKey(tbItem);
            }
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example=new TbItemExample();
        com.zl.pojo.TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return tbItemMapper.selectByExample(example);

    }

    @Override
    public void updateIsMarketable(Long[] ids, String status) {
        for (Long id : ids) {

            //只有通过审核的商品才能进行操作

            TbGoods record = new TbGoods() {{
                setIsMarketable(status);
                setId(id);
            }};

            int i = goodsMapper.updateByPrimaryKeySelective(record);


        }

    }
}
