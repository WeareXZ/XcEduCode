package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

/**
 * @ClassName: com.xuecheng.api.cms.EsCourseControllerApi.java
 * @Description:ElasticSearch课程搜索API
 * @author: heyz
 * @date:  2021/5/12 15:00
 * @version V1.0
 */
@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface EsCourseControllerApi {

    @ApiOperation("课程搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="courseSearchParam",value = "查询条件对象",required=false,paramType="path",dataType="CourseSearchParam")
    })
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;
}
