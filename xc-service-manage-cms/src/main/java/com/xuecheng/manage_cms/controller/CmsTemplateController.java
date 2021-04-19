package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageTemplateRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPageTemplateResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.PageTemplateService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @ClassName: com.xuecheng.manage_cms.controller.CmsTemplateController.java
 * @Description:页面模板controller层
 * @author: heyz
 * @date:  2021/4/16 15:57
 * @version V1.0
 */
@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    private PageTemplateService pageTemplateService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageTemplateRequest queryPageTemplateRequest) {
        return pageTemplateService.findList(page,size,queryPageTemplateRequest);
    }

    @Override
    @PostMapping("/add")
    public CmsPageTemplateResult add(@RequestBody CmsTemplate cmsTemplate) {
        return pageTemplateService.add(cmsTemplate);
    }

    @Override
    @PutMapping("/edit/{templateId}")
    public CmsPageTemplateResult edit(@PathVariable("templateId") String templateId,@RequestBody CmsTemplate cmsTemplate) {
        return pageTemplateService.edit(templateId,cmsTemplate);
    }

    @Override
    @GetMapping("/findByTemplateId/{templateId}" )
    public CmsPageTemplateResult findById(@PathVariable("templateId") String templateId) {
        return pageTemplateService.findById(templateId);
    }

    @Override
    @DeleteMapping("/delete/{templateId}")
    public CmsPageTemplateResult delete(@PathVariable("templateId") String templateId) {
        return pageTemplateService.delete(templateId);
    }

}
