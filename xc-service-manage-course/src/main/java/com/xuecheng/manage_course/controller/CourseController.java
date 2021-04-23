package com.xuecheng.manage_course.controller;

import com.xuecheng.api.cms.CourseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
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
    public ResponseResult updateCourseBase(@PathVariable("id") String id,@RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id,courseBase);
    }

    /**
     * @Description:查询课程计划
     * @author: heyz
     * @date:  2021/4/21 16:10
     */
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }


    /**
     * @Description:添加课程计划
     * @author: heyz
     * @date:  2021/4/21 17:58
     */
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult add(@RequestBody Teachplan teachplan) {
        return courseService.add(teachplan);
    }


    /**
     * @Description:修改课程计划
     * @author: heyz
     * @date:  2021/4/21 17:58
     */
    @Override
    @PutMapping("/teachplan/edit/{id}")
    public ResponseResult edit(@PathVariable("id") String id,@RequestBody Teachplan teachplan) {
        return courseService.edit(id,teachplan);
    }


    /**
     * @Description:分页查询课程信息
     * @author: heyz
     * @date:  2021/4/23 13:48
     */
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size,CourseListRequest courseListRequest) {
        return courseService.findCourseList(page,size,courseListRequest);
    }


    /**
     * @Description:查询课程营销信息
     * @author: heyz
     * @date:  2021/4/23 16:55
     */
    @Override
    @GetMapping
    public CourseMarket getCourseMarketById(String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    /**
     * @Description:修改课程营销信息
     * @author: heyz
     * @date:  2021/4/23 16:55
     */
    @Override
    @PostMapping
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }


}
