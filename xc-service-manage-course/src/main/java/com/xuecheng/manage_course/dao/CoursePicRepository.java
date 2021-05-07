package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoursePicRepository extends JpaRepository<CoursePic,String> {

    //CoursePicRepository父类提供的delete方法没有返回值，无法知道是否删除成功，这里我们在 CoursePicRepository下边自定义方法
    // 删除成功返回1否则返回0
    long deleteByCourseid(String courseid);

}
