package com.sy.mapper.game;

import com.sy.model.game.ItemPurchaseRecord;

public interface ItemPurchaseRecordMapper {
    int insert(ItemPurchaseRecord record);

    int insertSelective(ItemPurchaseRecord record);
}