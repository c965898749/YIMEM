package com.sy.mapper.game;

import com.sy.model.game.GameGiftRecord;
import org.apache.ibatis.annotations.Param;

public interface GameGiftRecordMapper {
    int deleteByPrimaryKey(Long recordId);

    int insert(GameGiftRecord record);

    int insertSelective(GameGiftRecord record);

    GameGiftRecord selectByPrimaryKey(Long recordId);

    int updateByPrimaryKeySelective(GameGiftRecord record);

    int updateByPrimaryKey(GameGiftRecord record);

    int countByUserIdAndGiftId(@Param("userId") String userId, @Param("giftId") Long giftId);
}