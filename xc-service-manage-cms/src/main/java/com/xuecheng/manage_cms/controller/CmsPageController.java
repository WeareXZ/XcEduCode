package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import net.bytebuddy.implementation.bind.annotation.Pipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 17:24
 **/
@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size")int size,QueryPageRequest queryPageRequest) {

/*        //暂时用静态数据
        //定义queryResult
        QueryResult<CmsPage> queryResult =new QueryResult<>();
        List<CmsPage> list = new ArrayList<>();
        CmsPage cmsTemplate = new CmsPage();
        cmsTemplate.setPageName("测试页面");
        list.add(cmsTemplate);
        queryResult.setList(list);
        queryResult.setTotal(1);

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;*/
        //调用service
        return pageService.findList(page,size,queryPageRequest);
    }

    /**
     * @Description:新增页面
     * @author: heyz
     * @date:  2021/4/13 16:32
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    /**
     * @Description:
     * @author: heyz
     * @date:  2021/4/13 16:32
     */
    @Override
    @PutMapping("/edit/{pageId}")
    public CmsPageResult edit(@PathVariable("pageId") String pageId,@RequestBody CmsPage cmsPage) {
        return pageService.edit(pageId,cmsPage);
    }

    /**
     * @Description:
     * @author: heyz
     * @date:  2021/4/13 16:32
     */
    @Override
    @GetMapping("/findByPageId/{pageId}" )
    public CmsPageResult findById(@PathVariable("pageId") String pageId) {
        return pageService.findById(pageId);
    }

    /**
     * @Description:
     * @author: heyz
     * @date:  2021/4/13 16:32
     */
    @Override
    @DeleteMapping("/delete/{pageId}")
    public CmsPageResult delete(@PathVariable("pageId") String pageId) {
        return pageService.delete(pageId);
    }


    /**
     * @Description:发布页面
     * @author: heyz
     * @date:  2021/4/20 16:31
     */
    @Override
    @PostMapping("/post/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.post(pageId);
    }

    /**
     * @Description:快速发布页面
     * @author: heyz
     * @date:  2021/5/8 22:13
     */
    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return pageService.postPageQuick(cmsPage);
    }
}
