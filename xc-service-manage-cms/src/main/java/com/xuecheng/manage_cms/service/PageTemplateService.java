package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageTemplateRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageTemplateResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName: com.xuecheng.manage_cms.service.PageTemplateService.java
 * @Description:页面模板service
 * @author: heyz
 * @date:  2021/4/16 15:58
 * @version V1.0
 */
@Service
public class PageTemplateService {

    @Autowired
    CmsTemplateRepository cmsPageTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    /**
     * 模板查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageTemplateRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageTemplateRequest queryPageTemplateRequest){
        //模板名称模糊查询
        ExampleMatcher matching = ExampleMatcher.matching().withMatcher("templateName",ExampleMatcher.GenericPropertyMatchers.contains());
        //站点ID 模板类型精确查询
        CmsTemplate cmsTemplate = new CmsTemplate();
        if(StringUtils.isNotEmpty(queryPageTemplateRequest.getSiteId())){
            cmsTemplate.setSiteId(queryPageTemplateRequest.getSiteId());
        }
        if(StringUtils.isNotEmpty(queryPageTemplateRequest.getTemplateName())){
            cmsTemplate.setTemplateName(queryPageTemplateRequest.getTemplateName());
        }
        //创建条件实例
        Example<CmsTemplate> cmsTemplateExample = Example.of(cmsTemplate, matching);
        //分页参数
        if(page <=0){
            page = 1;
        }
        page = page -1;
        if(size<=0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsTemplate> all = cmsPageTemplateRepository.findAll(cmsTemplateExample,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    /**
     * @Description:新增模板
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageTemplateResult add(CmsTemplate cmsTemplate){
        if(null != cmsTemplate){
            //校验模板是否存在，根据模板名称、站点Id、模板webpath查询
            CmsTemplate template = cmsPageTemplateRepository.findBySiteIdAndTemplateName(cmsTemplate.getSiteId(),cmsTemplate.getTemplateName());
            if(template != null){
                //校验模板是否存在，已存在则抛出异常
                ExceptionCast.cast(CmsCode.CMS_ADDPAGETEMPLATE_EXISTSNAME);
            }
            cmsTemplate.setTemplateId(null);//添加模板主键由spring data 自动生成
            //模板文件Id 用于查询对应的模板文件 需判断该模板是否存在 若不存在则添加失败
            Query query = new Query();
            Criteria criteria = new Criteria();
            //根据id查询
            criteria = criteria.where("_id").is(cmsTemplate.getTemplateId());
            GridFSFile gridFSFile = gridFsTemplate.findOne(query.addCriteria(criteria));
            if(null == gridFSFile){
                return new CmsPageTemplateResult(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL,null);
            }
            cmsPageTemplateRepository.save(cmsTemplate);
            return new CmsPageTemplateResult(CommonCode.SUCCESS,cmsTemplate);
        }
        return new CmsPageTemplateResult(CommonCode.FAIL,null);
    }

    /**
     * @Description:修改模板
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageTemplateResult edit(String templateId,CmsTemplate cmsTemplate) {
        if(null != cmsTemplate){
            CmsTemplate template = cmsPageTemplateRepository.findByTemplateId(templateId);
            if(null != template){
                BeanUtils.copyProperties(cmsTemplate,template);
                //模板文件Id 用于查询对应的模板文件 需判断该模板是否存在 若不存在则添加失败
                Query query = new Query();
                Criteria criteria = new Criteria();
                //根据id查询
                criteria = criteria.where("_id").is(cmsTemplate.getTemplateId());
                GridFSFile gridFSFile = gridFsTemplate.findOne(query.addCriteria(criteria));
                if(null == gridFSFile){
                    return new CmsPageTemplateResult(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL,null);
                }
                CmsTemplate save = cmsPageTemplateRepository.save(template);
                if (save != null) {
                    //返回成功
                    return new CmsPageTemplateResult(CommonCode.SUCCESS, save);
                }
            }else {
                return new CmsPageTemplateResult(CommonCode.FAIL,null);
            }
        }
        return new CmsPageTemplateResult(CommonCode.FAIL,null);
    }



    /**
     * @Description:删除模板
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageTemplateResult delete(String templateId) {
        CmsTemplate template = cmsPageTemplateRepository.findByTemplateId(templateId);
        if(null != template){
            cmsPageTemplateRepository.delete(template);
            return new CmsPageTemplateResult(CommonCode.SUCCESS, null);
        }
        return new CmsPageTemplateResult(CommonCode.FAIL,null);
    }

    /**
     * @Description:根据id查询模板
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageTemplateResult findById(String templateId) {
        if(StringUtils.isNotEmpty(templateId)){
            CmsTemplate template = cmsPageTemplateRepository.findByTemplateId(templateId);
            if(null != template){
                return new CmsPageTemplateResult(CommonCode.SUCCESS,template);
            }
        }
        return new CmsPageTemplateResult(CommonCode.FAIL,null);
    }
}
