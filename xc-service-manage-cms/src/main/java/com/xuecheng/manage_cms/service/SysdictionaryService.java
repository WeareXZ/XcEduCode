package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysdictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    /**
     * @Description:查询数据字典类型
     * @author: heyz
     * @date:  2021/4/23 15:38
     */
    public SysDictionary findDictionaryByType(String type) {
        SysDictionary sysDictionary = sysDictionaryRepository.findBydType(type);
        return sysDictionary;
    }

}
