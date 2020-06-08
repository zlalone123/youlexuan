package com.zl.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zl.entity.PageResult;
import com.zl.mapper.TbSpecificationOptionMapper;
import com.zl.mapper.TbTypeTemplateMapper;
import com.zl.pojo.TbSpecificationOption;
import com.zl.pojo.TbSpecificationOptionExample;
import com.zl.pojo.TbTypeTemplate;
import com.zl.pojo.TbTypeTemplateExample;
import com.zl.pojo.TbTypeTemplateExample.Criteria;
import com.zl.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 类型模板服务实现层
 *
 * @author Administrator
 */
@Service(timeout = 30000)
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        saveToRedis();//存入数据到缓存
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 返回所有模板的可选项id和name as text
     *
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return typeTemplateMapper.selectOptionList();
    }

    /**
     * 获取规格和规格选项
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findSpecList(Long id) {

        //根据模板的id获取到对应的所有的规格
        TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> specifications = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);


        if (specifications != null && specifications.size() != 0) {
            for (Map map : specifications) {

                //根据规格的id去查询对应的规格选项
                Long specId = Long.valueOf((Integer) map.get("id"));

                TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
                criteria.andSpecIdEqualTo(specId);
                List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(tbSpecificationOptionExample);
                map.put("options", tbSpecificationOptions);
            }
        }

        return specifications;
    }

    /**
     * 将数据存入缓存
     */
    private void saveToRedis() {
        //获取模板数据
        List<TbTypeTemplate> typeTemplateList = findAll();
        //循环模板
        for (TbTypeTemplate typeTemplate : typeTemplateList) {
            //存储品牌列表
            //[{"id":15,"text":"飞利浦"},{"id":17,"text":"海信"},{"id":13,"text":"长虹"}]
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
            //存储规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表、包括规格选项
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
        }
    }


}
