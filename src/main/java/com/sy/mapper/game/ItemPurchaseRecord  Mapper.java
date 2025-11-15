package com.sy.mapper.game;

import com.sy.model.game.ItemPurchaseRecord;

public interface ItemPurchaseRecord  Mapper {
    int insert(ItemPurchaseRecord record);

    int insertSelective(ItemPurchaseRecord record);
}