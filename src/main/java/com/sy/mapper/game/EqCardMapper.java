package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.Card;
import com.sy.model.game.EqCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface EqCardMapper extends BaseMapper<EqCard> {
    EqCard selectByid(String id);
    List<EqCard>  selectByStr(@Param("str") String str);
}