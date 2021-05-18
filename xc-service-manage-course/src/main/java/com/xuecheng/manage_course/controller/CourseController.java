package com.xuecheng.manage_course.controller;

import com.xuecheng.api.cms.CourseControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {


    @Autowired
    private CourseService courseService;

    /**
     * @Description:添加课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:01
     */
    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase CourseBase) {
        AddCourseResult addCourseResult = courseService.addCourseBase(CourseBase);
        return addCourseResult;
    }

    /**
     * @Description:查询课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:01
     */
    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) throws RuntimeException {
        return courseService.getCourseBaseById(courseId);
    }

    /**
     * @Description:修改课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:01
     */
    @Override
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id, courseBase);
    }

    /**
     * @Description:查询课程计划
     * @author: heyz
     * @date: 2021/4/21 16:10
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }


    /**
     * @Description:添加课程计划
     * @author: heyz
     * @date: 2021/4/21 17:58
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult add(@RequestBody Teachplan teachplan) {
        return courseService.add(teachplan);
    }

    /**
     * @Description:查询一条课程计划
     * @author: heyz
     * @date: 2021/4/21 17:58
     */

    @Override
    @GetMapping("/teachplan/findById/{id}")
    public Teachplan findTeachplan(@PathVariable("id") String courseId) {
        return courseService.findTeachplan(courseId);
    }


    /**
     * @Description:修改课程计划
     * @author: heyz
     * @date: 2021/4/21 17:58
     */
    @Override
    @PutMapping("/teachplan/edit/{id}")
    public ResponseResult edit(@PathVariable("id") String id, @RequestBody Teachplan teachplan) {
        return courseService.edit(id, teachplan);
    }

    /**
     * @Description:删除课程计划
     * @author: heyz
     * @date: 2021/4/21 17:58
     */
    @Override
    @DeleteMapping("/teachplan/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return courseService.delete(id);
    }


    /**
     * @Description:分页查询课程信息
     * @author: heyz
     * @date: 2021/4/23 13:48
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseService.findCourseList(page, size, courseListRequest);
    }


    /**
     * @Description:查询课程营销信息
     * @author: heyz
     * @date: 2021/4/23 16:55
     */
    @Override
    @GetMapping
    public CourseMarket getCourseMarketById(String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    /**
     * @Description:修改课程营销信息
     * @author: heyz
     * @date: 2021/4/23 16:55
     */
    @Override
    @PostMapping
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id, courseMarket);
    }

    /**
     * @Description:添加课程图片
     * @author: heyz
     * @date: 2021/4/28 1:30
     */
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        //保存课程图片
        return courseService.saveCoursePic(courseId, pic);
    }

    /**
     * @Description:查询课程图片
     * @author: heyz
     * @date: 2021/4/28 1:45
     */
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursepic(courseId);
    }

    /**
     * @Description:删除课程图片
     * @author: heyz
     * @date:  2021/4/28 1:54
     */
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    /**
     * @Description:查询课程详情页内容
     * @author: heyz
     * @date:  2021/5/7 18:11
     */
    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseView(@PathVariable("id") String courseId) {
        return courseService.getCoruseView(courseId);
    }

    /**
     * @Description:课程详情页预览
     * @author: heyz
     * @date:  2021/5/8 0:26
     */
    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }
}
