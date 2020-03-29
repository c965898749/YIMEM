package com.sy.mapper;


import com.sy.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectMapper {
    //根据用户id查找收藏夹
    List<Collect> queryByUserId(int userid);
    //新增收藏夹
    Integer add(@Param("userid") int userid, @Param("name") String name);

    //根据收藏夹ID统计收藏夹内的数据数量
    Integer dataCountByCollectId(Integer collectID);
    //根据收藏夹ID统计收藏夹被关注的总人数
    Integer invCountByCollectId(Integer collectID);
    //根据用户Id查询所有关注的收藏夹
    List<Fa_att> selectAllFa_attByUserid(Integer userId);
    //根据收藏夹ID查找收藏夹
    Collect selectCollectByID(Integer collectId);
    //根据收藏夹ID 获取收藏夹内包含的明细
    List<Collectitems>  selectAllCollectitemsByCollectId(Integer collectId);
    //根据ID获取问答
    Ask selectAskByCollectId(Integer askId);
    //根据ID获取博客
    Blog selectBlogByCollectId(Integer blogId);
    //根据ID获取论坛
    Forum selectForumByCollectId(Integer forumId);
    //根据用户ID和收藏夹ID删除关注记录
    Integer deleteFaAttByUseridAndCollectId(@Param("userId") Integer userId,@Param("collectId") Integer collectId);
    //根据收藏夹ID和收藏博客或问答或论坛Id去除收藏记录
    int deleteCollectitemsByCollectIdAndBidOrAidOrFid(Collectitems collectitems);
    //根据收藏夹ID删除收藏夹关注记录
    int delectFaAttByCollectId(Integer collectId);
    //根据收藏夹ID删除收藏夹收藏明细
    int delectCollectitemsByCollectId(Integer collectId);
    //根据收藏夹ID删除收藏夹
    int delectCollectByCollectId(Integer collectId);
    //新增收藏夹
    int insertCollect(Collect collect);
    //根据用户Id获取收藏夹名
    List<String> selectCollectNameByUserid(Integer userid);
    //修改收藏夹
    int updateCollect(Collect collect);
    //根据用户Id获取所有收藏夹
    List<Collect> selectAllCollectByuserId(Integer userId);
    //根据用户ID和收藏夹ID判断该收藏夹是否是该用户的
    int selectCollectByCollectidAndUserId(@Param("collectId")Integer collectId,@Param("userId")Integer userId);
    //根据收藏者ID和收藏夹ID判断用户是否收藏过该收藏夹
    int selectFaAttByUseridAndCollectId(@Param("userId")Integer userId,@Param("collectId")Integer collectId);
    //收藏收藏夹
    int insertNewFaAtt(@Param("favoriteId")Integer favoriteId,@Param("userId")Integer userId,@Param("collectId")Integer collectId);
}
