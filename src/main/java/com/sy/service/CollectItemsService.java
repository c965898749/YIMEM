package com.sy.service;

import com.sy.model.resp.BaseResp;

public interface CollectItemsService {
    //向收藏夹中添加博文
    BaseResp addToCollectResult(int blogid, int collectid);
    //取消收藏
    BaseResp delectCollectImemsResult(int blogid, int collectid);
}
