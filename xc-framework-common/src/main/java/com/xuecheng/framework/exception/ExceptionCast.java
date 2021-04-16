package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName: com.xuecheng.framework.exception.ExceptionCast.java
 * @Description:异常抛出类
 * @author: heyz
 * @date:  2021/4/14 16:05
 * @version V1.0
 */
public class ExceptionCast {

    //静态方法 抛出自定义异常
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }

}
