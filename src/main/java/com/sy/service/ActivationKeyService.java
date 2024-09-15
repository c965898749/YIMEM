package com.sy.service;

import com.sy.entity.ActivationKey;
import com.sy.model.resp.BaseResp;

public interface ActivationKeyService {
    int insert(ActivationKey record);

    BaseResp queryBytype(ActivationKey record);
}
