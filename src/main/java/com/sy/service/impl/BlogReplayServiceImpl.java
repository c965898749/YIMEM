package com.sy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.sy.mapper.BlogMapper;
import com.sy.mapper.BlogReplayMapper;
import com.sy.mapper.InformationMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.BlogReplay;
import com.sy.model.Information;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogReplayService;
import com.sy.tool.Constants;
import com.sy.tool.RedisCache;
import com.sy.tool.RedisUtil;

import com.sy.tool.Xtool;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class BlogReplayServiceImpl implements BlogReplayService {
    @Autowired
    private BlogReplayMapper blogReplayMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private InformationMapper informationMapper;
//    private MessageSender messageSender;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public BaseResp addReplay(BlogReplay blogReplay) {
        Integer status = 0;
        BaseResp baseResp = new BaseResp();
        Integer replayUserId = blogMapper.queryUserIdById(blogReplay.getBlogid());
        Integer userid = blogMapper.queryUserIdById(blogReplay.getBlogid());
        User user = userMapper.selectUserByUserId(userid);
        if (blogReplay.getCommentuserid() != replayUserId) {
            Integer unreadreplaycount = user.getUnreadreplaycount() + 1;
            user.setUnreadreplaycount(unreadreplaycount);
            blogReplay.setReplayUserId(replayUserId);
            status = 1;
        }
        blogReplay.setStatus(status);
//        StringEscapeUtils对html转义
        blogReplay.setComment(StringEscapeUtils.escapeHtml4(blogReplay.getComment()));
        int result = blogReplayMapper.addReplay(blogReplay);
        Information information = new Information();
        information.setBlogId(blogReplay.getBlogid());
        information.setContent(blogReplay.getComment());
        information.setReplayUserId(blogReplay.getCommentuserid());
        information.setUserId(blogReplay.getReplayUserId());
        Integer c = informationMapper.insert(information);
        if (result != 0) {
            System.out.println(userid);
            Integer commentCount = user.getCommentCount() + 1;
            System.out.println("评论数" + commentCount);
            user.setCommentCount(commentCount);
            userMapper.updateuser(user);
            amqpTemplate.convertAndSend("message.messge", JSON.toJSONString(userid));
            baseResp.setSuccess(1);
        } else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    public BaseResp queryByBlogId(int blog_id, int pageNum) {
        BaseResp baseResp = new BaseResp();
        Integer pageSize = 5;
        PageHelper.startPage(pageNum, pageSize);
        List<BlogReplay> blog_replayList = blogReplayMapper.queryByBlogId(blog_id);
        Page<BlogReplay> blogPage = (Page<BlogReplay>) blog_replayList;
        if (blog_replayList.size() != 0) {
            for (BlogReplay blog_replay : blog_replayList) {
                int replayUserId = blog_replay.getCommentuserid();
                User user = userMapper.selectUserByUserId(replayUserId);
                User user1 = new User();
                user1.setNickname(user.getNickname());
                user1.setUserId(user.getUserId());
                user1.setHeadImg(user.getHeadImg());
                blog_replay.setUser(user1);
            }
            baseResp.setSuccess(1);
            baseResp.setData(blog_replayList);
            baseResp.setCount(blogPage.getTotal());
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("没有回复");
        }

        return baseResp;
    }

    @Override
    public Map<String, Object> queryByUserId(int userId, int page) {
        int pageSize = 6;
        Map<String, Object> map = new HashMap<>();
        String key = Constants.REPLAY_INFORMATION + userId;
        //缓存机制
        if (RedisUtil.getJedisInstance().exists(key)) {
            System.out.println("进入缓存");
            List<String> json = RedisUtil.getJedisInstance().lrange(key, pageSize * (page - 1), pageSize * page - 1);
            map.put("success", 1);
            map.put("count", RedisUtil.getJedisInstance().get("count" + key));
            map.put("count2", RedisUtil.getJedisInstance().llen(key));
            map.put("data", json);
            RedisUtil.closeJedisInstance();
            return map;
        } else {
            System.out.println("进入数据库");
            List<String> lists = new ArrayList<>();
            //通过userId查找评论表
            List<Information> informations = informationMapper.select(userId);
            if (informations.size() != 0) {
                for (Information information : informations) {
                    int commentUserId = information.getReplayUserId();
                    User user = userMapper.selectUserByUserId(commentUserId);
                    String name = user.getNickname();
                    List<String> list = new ArrayList<>();
                    list.add(name);
                    list.add(information.getContent());
                    list.add(information.getBlogId() + "");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    list.add(simpleDateFormat.format(information.getTime()));
                    if (information.getStatus() == 0) {
                        continue;
                    }
                    list.add(information.getStatus() + "");
                    list.add(user.getUserId() + "");
                    list.add(information.getId() + "");
                    list.add(information.getBlogId() + "");
                    String json = JSONObject.toJSONString(list);
                    RedisUtil.getJedisInstance().rpush(key, json);
                    lists.add(json);
                }
                map.put("count2", lists.size());
//                list分页
                try {
                    map.put("data", lists.subList(pageSize * (page - 1), pageSize * page));//从下标0开始，找到第10个 即0-9
                } catch (IndexOutOfBoundsException e) {
                    map.put("data", lists.subList(pageSize * (page - 1), lists.size()));//数组越界异常时，取到最后一个元素
                }
                map.put("success", 1);
                Integer count = userMapper.selectUserByUserId(userId).getUnreadreplaycount();
                RedisUtil.getJedisInstance().set("count" + key, count + "");
                RedisUtil.getJedisInstance().expire("count" + key, 600);
                RedisUtil.getJedisInstance().expire(key, 600);
                RedisUtil.closeJedisInstance();
                map.put("count", count);
            } else {
                RedisUtil.getJedisInstance().set("count" + key, 0 + "");
                RedisUtil.getJedisInstance().expire("count" + key, 600);
                RedisUtil.closeJedisInstance();
                map.put("count", 0);
                map.put("success", 0);
            }
            RedisUtil.closeJedisInstance();
            return map;
        }
    }

    @RedisCache(Constants.REPLAY_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void removecommentreq(int userId) {
        informationMapper.removecommentreq(userId);
        userMapper.readcommentreq(userId);
    }


    @RedisCache(Constants.REPLAY_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void onclickcommentreq(int id, int userId) {
        Integer a= informationMapper.selectStatus(id);
        informationMapper.onclickcommentreq(userId, id);
       if (a==1){
           User user = userMapper.selectUserByUserId(userId);
           Integer unreadreplaycount = user.getUnreadreplaycount() - 1;
           if (unreadreplaycount >= 0) {
               user.setUnreadreplaycount(unreadreplaycount);
               userMapper.updateuser(user);
           }
       }
    }


}
