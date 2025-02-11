package com.sy.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.sy.mapper.BlogMapper;
import com.sy.mapper.BlogReplayMapper;
import com.sy.mapper.InformationMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Information;
import com.sy.model.User;
import com.sy.service.UpdateMessage;
import com.sy.tool.Constants;
//import com.sy.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateMessageImpl implements UpdateMessage {
    @Autowired
    private BlogReplayMapper blogReplayMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private InformationMapper informationMapper;

    @Override
    public Integer delRediskey(int userId) {
//        if (RedisUtil.getJedisInstance().exists(Constants.REPLAY_INFORMATION + userId)) {
//            RedisUtil.getJedisInstance().del(Constants.REPLAY_INFORMATION + userId);
//            RedisUtil.getJedisInstance().del("count" + userId);
//            RedisUtil.closeJedisInstance();
//            return 1;
//        }
        return 0;
    }

    @Override
    public void updateUserInfo(int userId) {
        String key = Constants.REPLAY_INFORMATION + userId;
        System.out.println("进入数据库");
        List<String> lists = new ArrayList<>();
        //通过userId查找评论表
        List<Information> blog_replays = informationMapper.select(userId);
        if (blog_replays.size() != 0) {
            for (Information blog_replay : blog_replays) {
                int commentUserId = blog_replay.getReplayUserId();
                User user = userMapper.selectUserByUserId(commentUserId);
                String name = user.getNickname();
                List<String> list = new ArrayList<>();
                list.add(name);
                list.add(blog_replay.getContent());
                list.add(blog_replay.getBlogId() + "");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                list.add(simpleDateFormat.format(blog_replay.getTime()));
                if (blog_replay.getStatus() == 0) {
                    continue;
                }
                list.add(blog_replay.getStatus() + "");
                list.add(user.getUserId() + "");
                list.add(blog_replay.getId() + "");
                list.add(blog_replay.getBlogId() + "");
                String json = JSONObject.toJSONString(list);
//                RedisUtil.getJedisInstance().rpush(key, json);
                lists.add(json);
            }
            Integer count = userMapper.selectUserByUserId(userId).getUnreadreplaycount();
//            RedisUtil.getJedisInstance().set("count" + key, count + "");
//            RedisUtil.getJedisInstance().expire("count" + key, 600);
//            RedisUtil.getJedisInstance().expire(key, 600);
//            RedisUtil.closeJedisInstance();
//            RedisUtil.getJedisInstance().set("count" + key, 0 + "");
//            RedisUtil.getJedisInstance().expire("count" + key, 600);
//            RedisUtil.closeJedisInstance();
        }
//        RedisUtil.closeJedisInstance();
    }
}
