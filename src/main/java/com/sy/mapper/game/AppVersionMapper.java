package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.AppVersion;
import com.sy.model.game.EqCharactersRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppVersionMapper  extends BaseMapper<AppVersion> {

    AppVersion selectListLast();
}