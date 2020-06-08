package com.zl.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zl.entity.PageResult;
import com.zl.entity.Result;
import com.zl.mapper.TbBrandMapper;
import com.zl.pojo.TbBrand;
import com.zl.pojo.TbBrandExample;
import com.zl.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(timeout = 12000)
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());

    }

    @Override
    public Result addBrand(TbBrand brand) {
        if (StringUtils.isEmpty(brand.getName())) {
            return new Result(false,"未获取到数据");
        }

        try {
            int i= tbBrandMapper.insert(brand);
            if (i>0){
                return new Result(true,"添加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"添加失败");
    }

    @Override
    public Result update(TbBrand brand) {

        if (ObjectUtils.isEmpty(brand.getId())) {
            return new Result(false,"the modified data was not obtained");
        }

        try {
            int i = tbBrandMapper.updateByPrimaryKey(brand);
            if(i>0){
                return new Result(true,"修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"修改失败");
    }

    @Override
    public TbBrand findOne(long id) {

        if (ObjectUtils.isEmpty(id)) {
            return null;
        }

        try {
            TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);
            if (!ObjectUtils.isEmpty(tbBrand)) {
                return tbBrand;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result delete(Long[] ids) {
        if (ids.length==0) {
            return new Result(false,"未选中数据");
        }

        try {
            TbBrandExample tbBrandExample = new TbBrandExample();

            //设置自定义操作条件
            TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();

            //将ids数组转化为list集合
            List<Long> list = Arrays.stream(ids).map(id -> Long.valueOf(id)).collect(Collectors.toList());

            //添加条件
            criteria.andIdIn(list);
            int i = tbBrandMapper.deleteByExample(tbBrandExample);
            if(i>0){
                return new Result(true,"delete is success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"delete is failed");
    }

//条件查询
    @Override
    public PageResult findPage(TbBrand brand, int pageNumber, int pageSize) {
        //分页
        PageHelper.startPage(pageNumber,pageSize);

        //条件查询
        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        if (!ObjectUtils.isEmpty(brand)) {
            if (!StringUtils.isEmpty(brand.getName())) {
                //根据姓名迷糊查询
                criteria.andNameLike("%"+brand.getName()+"%");
            }

            if (!StringUtils.isEmpty(brand.getFirstChar())) {
                //根据首字母等值查询
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }

        Page<TbBrand> page = (Page) tbBrandMapper.selectByExample(tbBrandExample);
        return ObjectUtils.isEmpty(page)?null:new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {

        return tbBrandMapper.selectOptionList();
    }
}
