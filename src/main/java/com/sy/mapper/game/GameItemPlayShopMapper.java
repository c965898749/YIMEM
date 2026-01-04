package com.sy.mapper.game;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sy.model.game.GameItemBase;
import com.sy.model.game.GameItemPlayShop;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameItemPlayShopMapper  extends BaseMapper<GameItemPlayShop> {
    List<GameItemPlayShop> selectAll();
}