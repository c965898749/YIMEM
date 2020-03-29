package com.sy.mapper;

import com.sy.model.Collect;
import com.sy.model.Collectitems;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CollectItemsMapper {
    //向收藏夹中添加内容
    Integer addToCollect(@Param("blogId") int blogid, @Param("collectId") int collectid);
    //取消收藏
    Integer deleteCollect(@Param("blogId") int blogid, @Param("collectId") int collectid);
    //通过收藏夹id和博文Id查找收藏夹明细
    List<Collectitems> queryCollect(@Param("collectid") int collectid, @Param("blogid") int blogid);

}
