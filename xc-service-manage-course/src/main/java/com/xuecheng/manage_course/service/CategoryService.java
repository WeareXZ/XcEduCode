package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version V1.0
 * @ClassName: com.xuecheng.manage_course.service.CategoryService.java
 * @Description:课程分类service层
 * @author: heyz
 * @date: 2021/4/23 15:25
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    //查询分类
    public CategoryNode findList() {
        CategoryNode categoryNode = categoryMapper.selectList();
        return categoryNode;
    }

}
