package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.xml.crypto.dsig.keyinfo.PGPData;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;


    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        //页面别名与名称模糊查询
        ExampleMatcher matching = ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains()).
                withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());
        //站点ID 页面类型精确查询
        CmsPage cmsPage = new CmsPage();
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if(StringUtils.isNotEmpty(queryPageRequest.getPageType())){
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        if(StringUtils.isNotEmpty(queryPageRequest.getPageName())){
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> cmsPageExample = Example.of(cmsPage, matching);
        //分页参数
        if(page <=0){
            page = 1;
        }
        page = page -1;
        if(size<=0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(cmsPageExample,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    /**
     * @Description:新增页面
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageResult add(CmsPage cmsPage){
        if(null != cmsPage){
            //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
            CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
            if(page != null){
                //校验页面是否存在，已存在则抛出异常
                ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
            }
            cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
     * @Description:修改页面
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageResult edit(String pageId,CmsPage cmsPage) {
        if(null != cmsPage){
            CmsPage page = cmsPageRepository.findByPageId(pageId);
            if(null != page){
                BeanUtils.copyProperties(cmsPage,page);
               /* //更新模板id
                page.setTemplateId(cmsPage.getTemplateId());
                //更新所属站点
                page.setSiteId(cmsPage.getSiteId());
                //更新页面别名
                page.setPageAliase(cmsPage.getPageAliase());
                // 更新页面名称
                page.setPageName(cmsPage.getPageName());
                // 更新访问路径
                page.setPageWebPath(cmsPage.getPageWebPath());
                // 更新物理路径
                page.setPagePhysicalPath(cmsPage.getPagePhysicalPath());*/
                CmsPage save = cmsPageRepository.save(page);
                if (save != null) {
                    //返回成功
                    return new CmsPageResult(CommonCode.SUCCESS, save);
                }
            }else {
                return new CmsPageResult(CommonCode.FAIL,null);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }



    /**
     * @Description:删除页面
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageResult delete(String pageId) {
        CmsPage page = cmsPageRepository.findByPageId(pageId);
        if(null != page){
            cmsPageRepository.delete(page);
            return new CmsPageResult(CommonCode.SUCCESS, null);
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    /**
     * @Description:根据id查询页面
     * @author: heyz
     * @date:  2021/4/13 16:36
     */
    public CmsPageResult findById(String pageId) {
        if(StringUtils.isNotEmpty(pageId)){
            CmsPage page = cmsPageRepository.findByPageId(pageId);
            if(null != page){
                return new CmsPageResult(CommonCode.SUCCESS,page);
            }
        }
        return new CmsPageResult(CommonCode.FAIL,null);
    }
}
