package com.sy.mapper;

import com.sy.model.*;

import java.util.List;

public interface SearchMapper {
    //搜索资源
    List<Upload> queryDownload(String key);
    //搜索论坛
    List<Invitation> queryForum(String key);
    //搜索问答
    List<Ask> queryAsk(String key);
    //搜索视频
    List<Video> queryVideo(String key);

    List<T8DocManage> queryDocManage(String key);

}
