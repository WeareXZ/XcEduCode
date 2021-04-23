package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeachplanRepository extends JpaRepository<Teachplan,String> {

    public List<Teachplan> findByCourseidAndParentid(String courseid, String parentid);
}
