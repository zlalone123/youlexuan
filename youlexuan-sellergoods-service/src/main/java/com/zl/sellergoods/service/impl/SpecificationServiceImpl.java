package com.zl.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zl.entity.PageResult;
import com.zl.group.SpecificationVO;
import com.zl.mapper.TbSpecificationMapper;
import com.zl.mapper.TbSpecificationOptionMapper;
import com.zl.pojo.TbSpecification;
import com.zl.pojo.TbSpecificationExample;
import com.zl.pojo.TbSpecificationExample.Criteria;
import com.zl.pojo.TbSpecificationOption;
import com.zl.pojo.TbSpecificationOptionExample;
import com.zl.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 规格服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(SpecificationVO specificationVO) {
        try {
            //添加规格
            int m = specificationMapper.insert(specificationVO.getSpecification());

            //添加规格选项
            for (TbSpecificationOption specificationOption : specificationVO.getSpecificationOptionList()) {
                //补全规格选项的id
                specificationOption.setSpecId(specificationVO.getSpecification().getId());
                int n = specificationOptionMapper.insert(specificationOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 修改
     */
    @Override
    public void update(SpecificationVO specificationVO) {

        //修改规格
        try {
            int i = specificationMapper.updateByPrimaryKey(specificationVO.getSpecification());

            //根据规格的主键删除原有的规格选项
            TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
            criteria.andSpecIdEqualTo(specificationVO.getSpecification().getId());

            specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);

            //删除成功之后，重新加入数据
            for (TbSpecificationOption tbSpecificationOption : specificationVO.getSpecificationOptionList()) {
                //补全规格的id
                tbSpecificationOption.setSpecId(specificationVO.getSpecification().getId());
                specificationOptionMapper.insert(tbSpecificationOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public SpecificationVO findOne(Long id) {
        //获取当前的规格
        TbSpecification specification = specificationMapper.selectByPrimaryKey(id);

        //获取规格对应的规格选项
        TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = specificationOptionExample.createCriteria();
        criteria.andSpecIdEqualTo(specification.getId());//更据规格id查询

        List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(specificationOptionExample);


        return new SpecificationVO(specification, specificationOptions);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {

            try {
                //删除规格选项
                TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
                criteria.andSpecIdEqualTo(id);
                specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);

                //删除规格数据
                specificationMapper.deleteByPrimaryKey(id);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
