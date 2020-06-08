package com.zl.sellergoods.service;

import com.zl.entity.PageResult;
import com.zl.entity.Result;
import com.zl.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand> findAll();

    public PageResult findPage(int pageNumber,int pageSize);

    public Result addBrand(TbBrand brand);

    public TbBrand findOne(long id);

    public Result update(TbBrand brand);

    public Result delete(Long[] ids);

    public PageResult findPage(TbBrand brand,int pageNumber,int pageSize);

    public List<Map> selectOptionList();
}
