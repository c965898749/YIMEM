package com.sy.service;

import com.sy.expection.CsdnExpection;
import com.sy.model.Upload;

import java.util.List;
import java.util.Map;

public interface UploadService {
    Integer save(Upload download) throws CsdnExpection;
    Integer remove(Integer userid, Integer id) throws CsdnExpection;
    List<Upload> findByUserid(Integer userid) throws CsdnExpection;
    List<Upload> findByLike(String keyword) throws CsdnExpection;
    Upload findById(Integer id) throws CsdnExpection;
    List<Upload> findAll(Upload upload)throws CsdnExpection;
    Integer findAllCount(Upload download)throws CsdnExpection;
    Integer modifReplyCount(Integer id, Integer replyCount);
    Integer updatahot(Integer hot);
    Map<String,Integer>  resourceProp(Integer userId);
    List<Upload> selecthot();
}
