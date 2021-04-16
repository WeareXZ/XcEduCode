package com.xuecheng.framework.exception;


import com.xuecheng.framework.model.response.ResultCode;

/**
 * @ClassName: com.xuecheng.framework.exception.CustomException.java
 * @Description:自定义异常类
 * @author: heyz
 * @date:  2021/4/14 16:04
 * @version V1.0
 */
public class CustomException extends RuntimeException {

    //反馈代码接口 可以实现多个相应代码类
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        //异常信息为错误代码+异常信息
        super("错误代码：" + resultCode.code() + "错误信息：" + resultCode.message());
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return this.resultCode;
    }
}

