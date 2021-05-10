package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;
import sun.nio.ch.IOUtil;
import sun.nio.cs.ext.MS874;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 页面查询方法
     *
     * @param page             页码，从1开始记数
     * @param size             每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //页面别名与名称模糊查询
        ExampleMatcher matching = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains()).
                withMatcher("pageName", ExampleMatcher.GenericPropertyMatchers.contains());
        //站点ID 页面类型精确查询
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageType())) {
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageName())) {
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> cmsPageExample = Example.of(cmsPage, matching);
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(cmsPageExample, pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;
    }

    /**
     * @Description:新增页面
     * @author: heyz
     * @date: 2021/4/13 16:36
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (null != cmsPage) {
            //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
            CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
            if (page != null) {
                //校验页面是否存在，已存在则更新
                return this.edit(page.getPageId(), cmsPage);
            }
            cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
            cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * @Description:修改页面
     * @author: heyz
     * @date: 2021/4/13 16:36
     */
    public CmsPageResult edit(String pageId, CmsPage cmsPage) {
        if (null != cmsPage) {
            CmsPage page = cmsPageRepository.findByPageId(pageId);
            if (null != page) {
                BeanUtils.copyProperties(cmsPage, page);
               /* //更新模板id
                page.setTemplateId(cmsTemplate.getTemplateId());
                //更新所属站点
                page.setSiteId(cmsTemplate.getSiteId());
                //更新页面别名
                page.setPageAliase(cmsTemplate.getPageAliase());
                // 更新页面名称
                page.setPageName(cmsTemplate.getPageName());
                // 更新访问路径
                page.setPageWebPath(cmsTemplate.getPageWebPath());
                // 更新物理路径
                page.setPagePhysicalPath(cmsTemplate.getPagePhysicalPath());*/
                CmsPage save = cmsPageRepository.save(page);
                if (save != null) {
                    //返回成功
                    return new CmsPageResult(CommonCode.SUCCESS, save);
                }
            } else {
                return new CmsPageResult(CommonCode.FAIL, null);
            }
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }


    /**
     * @Description:删除页面
     * @author: heyz
     * @date: 2021/4/13 16:36
     */
    public CmsPageResult delete(String pageId) {
        CmsPage page = cmsPageRepository.findByPageId(pageId);
        if (null != page) {
            cmsPageRepository.delete(page);
            return new CmsPageResult(CommonCode.SUCCESS, null);
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * @Description:根据id查询页面
     * @author: heyz
     * @date: 2021/4/13 16:36
     */
    public CmsPageResult findById(String pageId) {
        if (StringUtils.isNotEmpty(pageId)) {
            CmsPage page = cmsPageRepository.findByPageId(pageId);
            if (null != page) {
                return new CmsPageResult(CommonCode.SUCCESS, page);
            }
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * @Description:页面预览
     * @author: heyz
     * @date: 2021/4/17 14:20
     */
    public String getPageHtml(String pageId) {
        CmsPage page = cmsPageRepository.findByPageId(pageId);
        if (null == page) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_ISNOTEXISTS);
        }
        //获取模板数据
        Map model = getModel(page);
        if (null == model) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //2.取出freemarker模板
        String content = getFreeMarkerModel(page);
        if (null == content) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //3.将数据和模板文件结合
        String html = generateHtml(model, content);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }


    /**
     * @Description:配置模板加载器
     * @author: heyz
     * @date: 2021/4/17 15:21
     */
    private String generateHtml(Map model, String content) {
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", content);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            //获取模板
            Template template = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description:获取模板文件的内容
     * @author: heyz
     * @date: 2021/4/17 15:13
     */
    private String getFreeMarkerModel(CmsPage page) {
        String templateId = page.getTemplateId();
        CmsTemplate template = cmsTemplateRepository.findByTemplateId(templateId);
        if (null == template) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String templateFileId = template.getTemplateFileId();
        //查询模板文件
        Query query = new Query();
        Criteria criteria = new Criteria();
        GridFSFile gridFSFile = gridFsTemplate.findOne(query.addCriteria(criteria.where("_id").is(templateFileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //将模板文件放入输入流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        try {
            //将模板文件转化成String
            String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @Description:页面预览 获取模板数据
     * @author: heyz
     * @date: 2021/4/17 15:00
     */
    private Map getModel(CmsPage page) {
        //1.获取dataurl 获取模板数据
        String dataUrl = page.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //访问dataurl取出数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * @Description:发布页面
     * @author: heyz
     * @date: 2021/4/20 16:32
     */
    public ResponseResult post(String pageId) {
        //执行静态化
        String pageHtml = getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        //保存静态化文件
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        if (null == cmsPage) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_ISNOTEXISTS);
        }
        //发送消息
        sendPostPage(cmsPage);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * @Description:发送消息
     * @author: heyz
     * @date: 2021/4/20 16:45
     */
    private void sendPostPage(CmsPage cmsPage) {
        Map map = new HashMap();
        map.put("pageId", cmsPage.getPageId());
        //站点id作为路由KEY
        String siteId = cmsPage.getSiteId();
        String jsonString = JSONObject.toJSONString(map);
        //交换机 路由 消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, jsonString);
    }

    /**
     * @Description:保存生成的html
     * @author: heyz
     * @date: 2021/4/20 16:44
     */
    private CmsPage saveHtml(String pageId, String pageHtml) {
        Optional<CmsPage> page = cmsPageRepository.findById(pageId);
        if (!page.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_ISNOTEXISTS);
        }
        CmsPage cmsPage = page.get();
        //先删除之前的文件
        String htmlFileId = cmsPage.getHtmlFileId();
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query((criteria.where("_id").is(htmlFileId))));
        }
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
            cmsPage.setHtmlFileId(objectId.toString());
            cmsPageRepository.save(cmsPage);
            return cmsPage;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //添加页面
        CmsPageResult save = this.add(cmsPage);
        if (!save.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = save.getCmsPage(); //要布的页面id
        String pageId = cmsPage1.getPageId();
        //发布页面
        ResponseResult responseResult = this.post(pageId);
        if (!responseResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        //得到页面的url
        // 页面url=站点域名+站点webpath+页面webpath+页面名称
        // 站点id
        String siteId = cmsPage1.getSiteId();
        // 查询站点信息
        CmsSite cmsSite = findCmsSiteById(siteId);
        //站点域名
        String siteDomain = cmsSite.getSiteDomain();
        //站点web路径
        String siteWebPath = cmsSite.getSiteWebPath();
        //页面web路径
        String pageWebPath = cmsPage1.getPageWebPath();
        //页面名称
        String pageName = cmsPage1.getPageName();
        //页面的web访问地址
        String pageUrl = siteDomain + siteWebPath + pageWebPath + pageName;
        return new CmsPostPageResult(CommonCode.SUCCESS, pageUrl);

    }

    //根据id查询站点信息
    public CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }
}
