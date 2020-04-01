package com.sy.mapper;

import com.sy.model.Upload;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UploadMapper {
    Integer insert(Upload download);
    Integer delete(Integer userid, Integer id);
    List<Upload> selectAll(Upload upload);
    Integer selectCountByUserId(Upload upload);
    List<Upload>  selectByUserid(Integer userid);
    List<Upload> selectByLike(String keyword);
    Upload selectById(@Param("id") Integer id);
    Integer selectAllCount(Upload download);
    Integer updataReplyCount(@Param("id") Integer id, @Param("replyCount") Integer replyCount, @Param("appraise") Integer appraise);
}
