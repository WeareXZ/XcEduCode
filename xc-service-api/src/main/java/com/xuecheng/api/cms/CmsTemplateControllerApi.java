package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageTemplateRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageTemplateResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.FileNotFoundException;

@Api(value="cms页面模板管理接口",description = "cms页面模板管理接口，提供页面模板的增、删、改、查")
public interface CmsTemplateControllerApi {
    //页面查询
    @ApiOperation("分页查询模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="queryPageTemplateRequest",value = "查询条件对象",required=false,paramType="path",dataType="QueryPageTemplateRequest")
    })
    public QueryResponseResult findList(int page, int size, QueryPageTemplateRequest queryPageTemplateRequest);

    /**
     * @Description:新增模板
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("新增模板")
    public CmsPageTemplateResult add(CmsTemplate cmsTemplate);

    /**
     * @Description:修改模板
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("修改模板")
    public CmsPageTemplateResult edit(String templateId, CmsTemplate cmsTemplate);

    /**
     * @Description:根据id查询模板
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("根据id查询模板")
    public CmsPageTemplateResult findById(String templateId);

    /**
     * @Description:删除模板
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("删除模板")
    @ApiImplicitParam(name = "pageId",value = "模板Id",required = true,paramType="path",dataType="string")
    public CmsPageTemplateResult delete(String templateId);
}
