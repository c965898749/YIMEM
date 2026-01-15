package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameNotice;
import com.sy.model.game.GamePlayerBagExt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameNoticeMapper extends BaseMapper<GameNotice> {
    List<String> getAllNotice();
}