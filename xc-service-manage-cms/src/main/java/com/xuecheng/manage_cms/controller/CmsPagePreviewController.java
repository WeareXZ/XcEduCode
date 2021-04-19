package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @ClassName: com.xuecheng.manage_cms.controller.CmsPagePreviewController.java
 * @Description:页面预览控制层
 * @author: heyz
 * @date:  2021/4/17 14:15
 * @version V1.0
 */
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    private PageService pageService;

    /**
     * @Description:页面预览
     * @author: heyz
     * @date:  2021/4/17 14:20
     */
    @RequestMapping("/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId){
        String html = pageService.getPageHtml(pageId);
        if(StringUtils.isNotEmpty(html)){
            try {
                //直接将页面输出到浏览器
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(html.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
