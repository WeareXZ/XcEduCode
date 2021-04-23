package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName: com.xuecheng.api.cms.SysDicthinaryControllerApi.java
 * @Description:数据字典api
 * @author: heyz
 * @date:  2021/4/23 15:28
 * @version V1.0
 */
@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDicthinaryControllerApi {

    //数据字典
    @ApiOperation(value="数据字典查询接口")
    @ApiImplicitParam(name = "type",value = "数据字典类型",required = true,dataType = "string")
    public SysDictionary getByType(String type);
}
