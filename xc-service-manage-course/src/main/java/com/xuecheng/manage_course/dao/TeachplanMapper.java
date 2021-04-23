package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: com.xuecheng.manage_course.dao.TeachplanMapper.java
 * @Description:课程计划mapper
 * @author: heyz
 * @date:  2021/4/21 14:46
 * @version V1.0
 */
@Mapper
public interface TeachplanMapper {

    public TeachplanNode selectList(String courseId);
}
