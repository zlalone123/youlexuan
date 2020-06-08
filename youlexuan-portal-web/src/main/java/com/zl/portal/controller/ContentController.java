package com.zl.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zl.content.service.ContentService;
import com.zl.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/content")
@RestController
public class ContentController {
    @Reference(timeout = 30000)
    private ContentService contentService;

    @RequestMapping("/findContent")
    public List<TbContent> findByCategoryId(Long categoryId) {
        return contentService.findByContentCategoryId(categoryId);
    }

}
