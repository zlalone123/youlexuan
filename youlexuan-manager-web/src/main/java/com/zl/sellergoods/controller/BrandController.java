package com.zl.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zl.entity.PageResult;
import com.zl.entity.Result;
import com.zl.pojo.TbBrand;
import com.zl.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference(timeout = 2000)
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "pageNumber",required = false,defaultValue = "1")int pageNumber,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize){
        return brandService.findPage(pageNumber,pageSize);

    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
    @RequestMapping("/save")
    public Result save(@RequestBody  TbBrand tbBrand){
        return brandService.addBrand(tbBrand);
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(@RequestBody TbBrand brand){
        return brandService.findOne(brand.getId());
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        return brandService.update(brand);
    }

    @RequestMapping("/del")
    public Result delete(@RequestBody Long[] selectids){
        return brandService.delete(selectids);
    }


    @RequestMapping("/findPageCriteria")
    public PageResult findPage(@RequestBody TbBrand brand,int pageNumber,int pageSize){
        return brandService.findPage(brand,pageNumber,pageSize);
    }
}
