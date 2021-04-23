package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.*;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    //页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="queryPageRequest",value = "查询条件对象",required=false,paramType="path",dataType="QueryPageRequest")
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * @Description:新增页面
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("新增页面")
    @ApiImplicitParam(name = "cmsPage",value = "页面对象",required = true,paramType="path",dataType="CmsPage")
    public CmsPageResult add(CmsPage cmsPage);

    /**
     * @Description:修改页面
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("修改页面")
    @ApiImplicitParam(name = "pageId",value = "页面Id",required = true,paramType="path",dataType="string")
    public CmsPageResult edit(String pageId,CmsPage cmsPage);

    /**
     * @Description:根据id查询页面
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("根据id查询页面")
    @ApiImplicitParam(name = "pageId",value = "页面Id",required = true,paramType="path",dataType="string")
    public CmsPageResult findById(String pageId);

    /**
     * @Description:删除页面
     * @author: heyz
     * @date:  2021/4/13 16:26
     */
    @ApiOperation("删除页面")
    @ApiImplicitParam(name = "pageId",value = "页面Id",required = true,paramType="path",dataType="string")
    public CmsPageResult delete(String pageId);

    /**
     * @Description:发布页面
     * @author: heyz
     * @date:  2021/4/20 16:29
     */
    @ApiOperation("发布页面")
    @ApiImplicitParam(name = "pageId",value = "页面Id",required = true,paramType="path",dataType="string")
    public ResponseResult post(String pageId);

}
