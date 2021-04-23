package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: com.xuecheng.api.cms.CategoryControllerApi.java
 * @Description:课程分类api
 * @author: heyz
 * @date:  2021/4/23 15:17
 * @version V1.0
 */
@Api(value = "课程分类管理",description = "课程分类管理",tags = {"课程分类管理"})
public interface CategoryControllerApi {

    @ApiOperation("查询分类")
    public CategoryNode findList();


}
