package com.xuecheng.manage_media.dao;

import com.xuecheng.framework.domain.media.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaFileDao extends MongoRepository<MediaFile,String> {
}
