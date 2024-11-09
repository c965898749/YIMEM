package com.sy.mapper;

import com.sy.entity.ActivationKey;

public interface ActivationKeyMapper {
    int remove(ActivationKey record);
    int insert(ActivationKey record);
    ActivationKey queryBytype(ActivationKey record);
    ActivationKey queryNew();
    int update(ActivationKey record);
    int updateOpenId(ActivationKey record);
    int activationUpdate(ActivationKey record);
    int updateRandomCode(String randomCode);
    int queryBystatus(ActivationKey record);
    ActivationKey queryByOpenId(ActivationKey record);
}
