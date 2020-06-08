package com.zl.group;

import com.zl.pojo.TbSpecification;
import com.zl.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 规格的组合实体类
 */
public class SpecificationVO implements Serializable {
    private TbSpecification specification;
    private List<TbSpecificationOption> specificationOptionList;

    public SpecificationVO() {
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public SpecificationVO(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }
    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }
    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

}
