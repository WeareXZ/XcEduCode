package com.xuecheng.manage_cms_client.mq;


import com.alibaba.fastjson.JSONObject;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @version V1.0
 * @ClassName: com.xuecheng.manage_cms_client.mq.ConsumerPostPage.java
 * @Description:页面发布消费者客户端
 * @author: heyz
 * @date: 2021/4/20 16:19
 */
@Component
public class ConsumerPostPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);


    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    PageService pageService;


    /**
     * @Description:监听生产者发来的消息
     * @author: heyz
     * @date: 2021/4/20 16:21
     */
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        //解析消息
        Map map = JSONObject.parseObject(msg, Map.class);
        LOGGER.info("receive cms post page:{}", msg);
        // 取出页面id
        String pageId = (String) map.get("pageId");
        // 查询页面信息
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            LOGGER.error("receive cms post page,cmsPage is null:{}", msg);
            return;
        }
        //将页面保存到服务器物理路径
        pageService.savePageToServerPath(pageId);
    }
}
