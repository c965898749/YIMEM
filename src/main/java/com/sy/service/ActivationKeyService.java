package com.sy.service;

import com.sy.entity.ActivationKey;
import com.sy.model.resp.BaseResp;

public interface ActivationKeyService {
    int insert(ActivationKey record);

    int update(ActivationKey record);

    int updateRandomCode(String randomCode);

    BaseResp queryBytype(ActivationKey record);
}
