package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-09-12 18:32
 **/
@Service
public class PageService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(PageService.class);

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;


    /**
     * @Description:将页面html保存到页面物理路径
     * @author: heyz
     * @date: 2021/4/20 15:59
     */
    public void savePageToServerPath(String pageId) {
        Optional<CmsPage> page = cmsPageRepository.findById(pageId);
        if (page.isPresent()) {
            CmsPage cmsPage = page.get();
            //物理路径
            String pagePhysicalPath = cmsPage.getPagePhysicalPath();
            //页面路径
            String pageWebPath = cmsPage.getPageWebPath();
            //文件路径
            String htmlPath = pagePhysicalPath + pageWebPath + cmsPage.getPageName();
            //拿到根据模板生产的html
            //查询页面静态文件
            String htmlFileId = cmsPage.getHtmlFileId();
            InputStream inputStream = this.getFileById(htmlFileId);
            if (inputStream == null) {
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            }
            //将html存入物理路径
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(new File(htmlPath));
                IOUtils.copy(inputStream, fileOutputStream);
                LOGGER.info("============================模板Id:{},模板物理路径:{}============================",htmlFileId,htmlPath);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据文件id获取文件内容
    public InputStream getFileById(String fileId) {
        try {
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
