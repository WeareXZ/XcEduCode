package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: com.xuecheng.framework.domain.cms.request.QueryPageTemplateRequest.java
 * @Description:
 * @author: heyz
 * @date:  2021/4/16 15:50
 * @version V1.0
 */
@Data
public class QueryPageTemplateRequest {
    //接收页面查询的查询条件
    //站点id
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //模版id
    @ApiModelProperty("模版id")
    private String templateId;
    //模版名称
    @ApiModelProperty("模版名称")
    private String templateName;
}
