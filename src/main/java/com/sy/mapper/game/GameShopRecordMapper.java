package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameItemPlayShop;
import com.sy.model.game.GameShopRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameShopRecordMapper extends BaseMapper<GameShopRecord> {
   Integer isRecord(@Param("userId") String userId,@Param("itemId") String itemId);
}