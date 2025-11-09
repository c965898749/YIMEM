package com.sy.mapper.game;

import com.sy.model.game.GameGiftRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameGiftRuleMapper {
    int deleteByPrimaryKey(Long ruleId);

    int insert(GameGiftRule record);

    int insertSelective(GameGiftRule record);

    GameGiftRule selectByPrimaryKey(Long ruleId);

    int updateByPrimaryKeySelective(GameGiftRule record);

    int updateByPrimaryKey(GameGiftRule record);

    List<GameGiftRule> selectByGiftId( @Param("giftId")Long giftId);
}