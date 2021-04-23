package com.xuecheng.manage_cms.dao;


import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
    //根据模板名称查询
    CmsTemplate findByTemplateName(String templateName);

    //根据模板名称和站点查询
    CmsTemplate findBySiteIdAndTemplateName(String siteId,String templateName);

    //根据模板Id查询
    CmsTemplate findByTemplateId(String templateId);
}
