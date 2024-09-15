package com.sy.mapper;

import com.sy.entity.ActivationKey;

public interface ActivationKeyMapper {
    int remove(ActivationKey record);
    int insert(ActivationKey record);
    ActivationKey queryBytype(ActivationKey record);
}
