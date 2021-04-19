package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

@Data
public class CmsPageTemplateResult extends ResponseResult {

    CmsTemplate cmsTemplate;
    public CmsPageTemplateResult(ResultCode resultCode, CmsTemplate cmsTemplate) {
        super(resultCode);
        this.cmsTemplate = cmsTemplate;
    }
}
