package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @version V1.0
 * @ClassName: com.xuecheng.api.cms.CourseControllerApi.java
 * @Description:课程管理api
 * @author: heyz
 * @date: 2021/4/21 14:33
 */
@Api(value = "cms课程管理接口", description = "cms课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {

    @ApiOperation("添加课程基础信息")
    @ApiImplicitParam(name = "CourseBase", value = "课程基本信息对象", required = true, dataType = "CourseBase")
    public AddCourseResult addCourseBase(CourseBase CourseBase);

    @ApiOperation("获取课程基础信息")
    @ApiImplicitParam(name = "courseId", value = "课程基础信息id", required = true, dataType = "string")
    public CourseBase getCourseBaseById(String courseId) throws RuntimeException;

    @ApiOperation("更新课程基础信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程基础信息id", required = true, dataType = "string"),
            @ApiImplicitParam(name = "CourseBase", value = "课程基本信息对象", required = true, dataType = "CourseBase")
    })
    public ResponseResult updateCourseBase(String id, CourseBase courseBase);

    @ApiOperation("课程计划查询")
    @ApiImplicitParam(name = "courseId", value = "课程计划id", required = true, dataType = "string")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    @ApiImplicitParam(name = "teachplan", value = "课程计划对象", required = true, dataType = "Teachplan")
    public ResponseResult add(Teachplan teachplan);

    @ApiOperation("查询一条课程计划")
    @ApiImplicitParam(name = "courseId", value = "课程计划id", required = true, dataType = "string")
    public Teachplan findTeachplan(String courseId);

    @ApiOperation("修改课程计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程计划id", required = true, dataType = "string"),
            @ApiImplicitParam(name = "teachplan", value = "课程营销对象", required = true, dataType = "Teachplan")
    })
    public ResponseResult edit(String id, Teachplan teachplan);

    @ApiOperation("删除课程计划")
    @ApiImplicitParam(name = "id", value = "课程计划id", required = true, dataType = "string")
    public ResponseResult delete(String id);

    @ApiOperation("查询我的课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "courseListRequest", value = "查询条件对象", required = false, paramType = "path", dataType = "CourseListRequest")
    })
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("获取课程营销信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, dataType = "string")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程id", required = true, dataType = "string"),
            @ApiImplicitParam(name = "courseMarket", value = "课程营销对象", required = true, dataType = "CourseMarket")
    })
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    @ApiOperation("添加课程图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程id", required = true, dataType = "string"),
            @ApiImplicitParam(name = "pic", value = "课程图片路径", required = true, dataType = "string")
    })
    public ResponseResult addCoursePic(String courseId, String pic);

    @ApiOperation("获取课程基础信息")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, dataType = "string")
    public CoursePic findCoursePic(String courseId);

    @ApiOperation("删除课程图片")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, dataType = "string")
    public ResponseResult deleteCoursePic(String courseId);

    @ApiOperation("课程视图查询")
    @ApiImplicitParam(name = "courseId", value = "课程id", required = true, dataType = "string")
    public CourseView courseView(String courseId);

    @ApiOperation("预览课程")
    @ApiImplicitParam(name = "id", value = "课程id", required = true, dataType = "string")
    public CoursePublishResult preview(String id);

    @ApiOperation("发布课程")
    @ApiImplicitParam(name = "id", value = "课程id", required = true, dataType = "string")
    public CoursePublishResult publish(String id);
}
