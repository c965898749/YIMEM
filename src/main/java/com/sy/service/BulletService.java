package com.sy.service;

import com.sy.model.Bullet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BulletService {
    int deleteByPrimaryKey(Integer bulletid);

    int insert(Bullet record);

    int insertSelective(Bullet record);

    Bullet selectByPrimaryKey(Integer bulletid);

    List<Bullet> selectByVideoId (@Param("videoId") Integer videoId);

    int updateByPrimaryKeySelective(Bullet record);

    int updateByPrimaryKey(Bullet record);
}
