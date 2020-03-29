package com.sy.service;

import com.sy.model.BlogReplay;
import com.sy.model.resp.BaseResp;

public interface BlogReplaySonService {
    BaseResp insert(BlogReplay blogReplaySon);
    BaseResp queryBlogReplaySonByReplayId(Integer blogReplayId);
}
