package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameItemBase;
import com.sy.model.game.GamePlayerBag;
import com.sy.model.game.GamePlayerBagExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GamePlayerBagMapper extends BaseMapper<GamePlayerBag> {
    List<GamePlayerBag> goIntoListById(@Param("userId")String userId);
    GamePlayerBag goIntoListByIdAndItemId(@Param("userId")String userId,@Param("itemId")Integer itemId);

}