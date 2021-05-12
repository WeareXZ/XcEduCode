package com.xuecheng.manage_course.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CoursePubRepository coursePubRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

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
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        Teachplan one = optional.get();
        BeanUtils.copyProperties(teachplan, one);
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
     * @date: 2021/4/23 17:06
     */
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    /**
     * @Description:修改和保存课程营销信息
     * @author: heyz
     * @date: 2021/4/23 17:06
     */
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket marketById = this.getCourseMarketById(id);
        CourseMarket market = null;
        if (null == marketById) {
            //添加课程营销信息
            marketById = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, marketById);
            // 设置课程id
            marketById.setId(id);
            market = courseMarketRepository.save(marketById);
        } else {
            //one.setCharge(courseMarket.getCharge());
            // one.setStartTime(courseMarket.getStartTime());//课程有效期，开始时间
            // one.setEndTime(courseMarket.getEndTime());//课程有效期，结束时间
            // one.setPrice(courseMarket.getPrice());
            // one.setQq(courseMarket.getQq());
            // one.setValid(courseMarket.getValid());
            BeanUtils.copyProperties(marketById, courseMarket);
            market = courseMarketRepository.save(marketById);
        }
        if (null == market) {
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     * @Description:查询一条课程计划
     * @author: heyz
     * @date: 2021/4/23 17:06
     */
    public Teachplan findTeachplan(String courseId) {
        Optional<Teachplan> byId = teachplanRepository.findById(courseId);
        if (!byId.isPresent()) {
            ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        Teachplan teachplan = byId.get();
        return teachplan;
    }

    /**
     * @Description:删除课程计划
     * @author: heyz
     * @date: 2021/4/23 17:06
     */
    @Transactional
    public ResponseResult delete(String id) {
        Optional<Teachplan> byId = teachplanRepository.findById(id);
        if (!byId.isPresent()) {
            ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        Teachplan teachplan = byId.get();
        teachplanRepository.delete(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @Description:添加课程图片
     * @author: heyz
     * @date: 2021/4/28 1:30
     */
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (picOptional.isPresent()) {
            coursePic = picOptional.get();
        }
        //没有课程图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        //保存课程图片
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @Description:查询课程图片
     * @author: heyz
     * @date: 2021/4/28 1:45
     */
    public CoursePic findCoursepic(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (!byId.isPresent()) {
            ExceptionCast.cast(CommonCode.DATA_ISNULL);
        }
        CoursePic coursePic = byId.get();
        return coursePic;
    }

    /**
     * @Description:删除课程图片
     * @author: heyz
     * @date: 2021/4/28` 1:53
     */
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //执行删除，返回1表示删除成功，返回0表示删除失败
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * @Description:查询课程详情页内容
     * @author: heyz
     * @date: 2021/5/7 18:13
     */
    public CourseView getCoruseView(String courseId) {
        CourseView courseView = new CourseView(); //查询课程基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }
        //查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //查询课程图片信息
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(picOptional.get());
        }
        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    //根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    /**
     * @Description:课程详情预览
     * @author: heyz
     * @date: 2021/5/8 0:27
     */
    public CoursePublishResult preview(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);
        //课程预览站点
        // 模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //页面url
        String pageUrl = previewUrl + pageId;
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    /**
     * @Description:课程详情页发布
     * @author: heyz
     * @date: 2021/5/8 22:14
     */
    public CoursePublishResult publish(String courseId) {
        //课程信息
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程详情页面 
        CmsPostPageResult cmsPostPageResult = publish_page(courseId);
        if (!cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //更新课程状态
        CourseBase courseBase = saveCoursePubState(courseId);
        //创建课程索引  课程索引
        // 创建课程索引信息
        CoursePub coursePub = createCoursePub(courseId);
        //向数据库保存课程索引信息
        CoursePub newCoursePub = saveCoursePub(courseId, coursePub);
        if (newCoursePub == null) {
            //创建课程索引信息失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }
        //课程缓存...todo
        // 页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    //发布课程正式页面
    public CmsPostPageResult publish_page(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);
        //课程预览站点
        // 模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId + ".html");
        //页面别名
        cmsPage.setPageAliase(one.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        //发布页面
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        return cmsPostPageResult;
    }

    //更新课程发布状态
    private CourseBase saveCoursePubState(String courseId) {
        CourseBase courseBase = this.findCourseBaseById(courseId);
        //更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }


    //保存CoursePub
    public CoursePub saveCoursePub(String id, CoursePub coursePub) {
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CoursePub coursePubNew = null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else {
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub, coursePubNew);
        //设置主键
        coursePubNew.setId(id);
        //更新时间戳为最新时间
        coursePub.setTimestamp(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub.setPubTime(date);
        coursePubRepository.save(coursePub);
        return coursePub;
    }

    //创建coursePub对象
    private CoursePub createCoursePub(String id) {
        CoursePub coursePub = new CoursePub();
        coursePub.setId(id);
        //基础信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional == null) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()) {
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        //将课程计划转成json
        String teachplanString = JSONObject.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplanString);
        return coursePub;
    }
}
