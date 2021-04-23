package com.xuecheng.manage_course.service;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    /**
     * @Description:添加课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:01
     */
    @Transactional
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        if (null == courseBase) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程状态默认为未发布
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);
        //用户选择课程分类后，所选分类ID绑定到前端参数categoryActive中
        return new AddCourseResult(CommonCode.SUCCESS, save.getId());
    }


    /**
     * @Description:查询课程计划
     * @author: heyz
     * @date: 2021/4/21 18:04
     */
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    /**
     * @Description:添加课程计划
     * @author: heyz
     * @date: 2021/4/21 18:03
     */
    @Transactional
    public ResponseResult add(Teachplan teachplan) {
        //校验课程id和课程计划名称
        if (teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //取出课程id
        String courseid = teachplan.getCourseid();
        //取出父结点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            //如果父结点为空则获取根结点
            parentid = getTeachplanRoot(courseid);
        }
        //取出父结点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //父结点
        Teachplan teachplanParent = teachplanOptional.get();
        //父结点级别
        String parentGrade = teachplanParent.getGrade();
        //设置父结点
        teachplan.setParentid(parentid);
        teachplan.setStatus("0");//未发布
        // 子结点的级别，根据父结点来判断
        if (parentGrade.equals("1")) {
            teachplan.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplan.setGrade("3");
        }
        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());
        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getTeachplanRoot(String courseId) {
        //校验课程id
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //查询出是否存在上级节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        //不存在就是根节点
        if (null == teachplanList && teachplanList.size() == 0) {
            Teachplan teachplan = new TeachplanNode();
            teachplan.setCourseid(courseId);
            teachplan.setPname(courseBase.getName());
            teachplan.setParentid("0");
            teachplan.setGrade("1");//1级
            teachplan.setStatus("0");//未发布
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        String id = teachplanList.get(0).getId();
        return id;
    }

    /**
     * @Description:修改课程计划
     * @author: heyz
     * @date: 2021/4/21 18:03
     */
    @Transactional
    public ResponseResult edit(String id, Teachplan teachplan) {
        Optional<Teachplan> optional = teachplanRepository.findById(id);
        if(!optional.isPresent()){
           ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        Teachplan one = optional.get();
        BeanUtils.copyProperties(teachplan,one);
        teachplanRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @Description:查询课程信息
     * @author: heyz
     * @date: 2021/4/23 14:19
     */
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (null == courseListRequest) {
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }
        //分页查询
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseList = courseMapper.findCourseListPage(courseListRequest);
        long total = courseList.getTotal();
        List<CourseInfo> result = courseList.getResult();
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(result);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult<>(CommonCode.SUCCESS, courseIncfoQueryResult);
    }

    /**
     * @Description:根据id查询课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:21
     */
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        return null;
    }

    /**
     * @Description:更新课程基本信息
     * @author: heyz
     * @date: 2021/4/23 16:21
     */
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        CourseBase one = this.getCourseBaseById(id);
        if (one == null) {
            ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        //修改课程信息
        // one.setName(courseBase.getName());
        // one.setMt(courseBase.getMt());
        // one.setSt(courseBase.getSt());
        // one.setGrade(courseBase.getGrade());
        // one.setStudymodel(courseBase.getStudymodel());
        // one.setUsers(courseBase.getUsers());
        // one.setDescription(courseBase.getDescription());
        BeanUtils.copyProperties(courseBase, one);
        courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @Description:根据id查询课程营销信息
     * @author: heyz
     * @date:  2021/4/23 17:06
     */
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (!optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    /**
     * @Description:修改和保存课程营销信息
     * @author: heyz
     * @date:  2021/4/23 17:06
     */
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket marketById = this.getCourseMarketById(id);
        CourseMarket market = null;
        if(null == marketById){
            //添加课程营销信息
            marketById = new CourseMarket(); BeanUtils.copyProperties(courseMarket, marketById);
            // 设置课程id
            marketById.setId(id);
            market = courseMarketRepository.save(marketById);
        }else {
            //one.setCharge(courseMarket.getCharge());
            // one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            // one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            // one.setPrice(courseMarket.getPrice());
            // one.setQq(courseMarket.getQq());
            // one.setValid(courseMarket.getValid());
            BeanUtils.copyProperties(marketById,courseMarket);
            market = courseMarketRepository.save(marketById);
        }
        if(null == market){
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }


}
