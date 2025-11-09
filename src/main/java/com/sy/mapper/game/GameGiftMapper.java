package com.sy.mapper.game;

import com.sy.model.game.GameGift;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GameGiftMapper {
    int deleteByPrimaryKey(Long giftId);

    int insert(GameGift record);

    int insertSelective(GameGift record);

    GameGift selectByPrimaryKey(Long giftId);

    int updateByPrimaryKeySelective(GameGift record);

    int updateByPrimaryKeyWithBLOBs(GameGift record);

    int updateByPrimaryKey(GameGift record);

    List<GameGift> selectValidGifts(@Param("now") LocalDateTime now);

    GameGift selectByGiftCode(@Param("giftCode") String giftCode);
}