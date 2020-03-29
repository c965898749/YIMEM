package com.sy.service.impl;

import com.alibaba.fastjson.JSON;
import com.sy.mapper.BlogMapper;
import com.sy.mapper.BlogReplayMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.BlogReplay;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogReplaySonService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BlogReplaySonServiceImpl implements BlogReplaySonService {
    @Autowired
    private BlogReplayMapper blogReplayMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public BaseResp insert(BlogReplay blogReplaySon) {
        BaseResp baseResp = new BaseResp();
        blogReplaySon.setStatus(1);
        blogReplayMapper.updateCount(blogReplaySon.getBlogReplayId());
        Integer count = blogReplayMapper.insert(blogReplaySon);

//            给博主发消息
            Integer userid = blogMapper.queryUserIdById(blogReplaySon.getBlogid());
            if (userid!=blogReplaySon.getCommentuserid()){
                //            如果帖子博主不是自己 未读回复+1
                User user = userMapper.selectUserByUserId(userid);
                this.countjia(user);
                amqpTemplate.convertAndSend("message.messge", JSON.toJSONString(userid));
                //            如果帖子的回复不是自己 未读回复+1且也不是博主
                if (blogReplaySon.getCommentuserid()!=blogReplaySon.getReplayUserId()&&userid!=blogReplaySon.getReplayUserId()){
                    System.out.println("wwww");
                    User user1 = userMapper.selectUserByUserId(blogReplaySon.getReplayUserId());
                    this.countjia(user1);
                    amqpTemplate.convertAndSend("message.messge", JSON.toJSONString(blogReplaySon.getReplayUserId()));

                }
            }else {
                User user = userMapper.selectUserByUserId(userid);
                Integer commentCount= user.getCommentCount()+1;
                user.setCommentCount(commentCount);
                userMapper.updateuser(user);
                if (blogReplaySon.getCommentuserid()!=blogReplaySon.getReplayUserId()&&userid!=blogReplaySon.getReplayUserId()){
                    System.out.println("wwww");
                    User user1 = userMapper.selectUserByUserId(blogReplaySon.getReplayUserId());
                    this.countjia(user1);
                    amqpTemplate.convertAndSend("message.messge", JSON.toJSONString(blogReplaySon.getReplayUserId()));

                }
            }
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("回复成功");
            return baseResp;

    }

    public void countjia( User user){
        Integer unreadreplaycount = user.getUnreadreplaycount() + 1;
        user.setUnreadreplaycount(unreadreplaycount);
        Integer commentCount= user.getCommentCount()+1;
        user.setCommentCount(commentCount);
        userMapper.updateuser(user);
    }
    @Override
    public BaseResp queryBlogReplaySonByReplayId(Integer blogReplayId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        BaseResp baseResp=new BaseResp();
        List<BlogReplay> list=new ArrayList<>();
        try {
            list = blogReplayMapper.queryBlogReplaySonByReplayId(blogReplayId);
            if (list!=null){
                for (BlogReplay blogReplaySon : list) {

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", blogReplaySon.getId());
                    map.put("blogReplayId", blogReplaySon.getBlogReplayId());
                    map.put("comment", blogReplaySon.getComment());
                    map.put("commentuserid", blogReplaySon.getCommentuserid());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(blogReplaySon.getTime());
                    map.put("time", dateString);
                    System.out.println(blogReplaySon.getTime());
                    map.put("replayUserId", blogReplaySon.getReplayUserId());
                    User user = userMapper.selectUserByUserId(blogReplaySon.getCommentuserid());
                    map.put("commentnickname", user.getNickname());
                    map.put("commentheadimg", user.getHeadImg());
                    User user1 = userMapper.selectUserByUserId(blogReplaySon.getReplayUserId());
                    map.put("replayUsernickname", user1.getNickname());
                    map.put("replayUserheadimg", user1.getHeadImg());
                    mapList.add(map);
                }
                baseResp.setSuccess(0);
                baseResp.setData(mapList);
                return baseResp;
            }else {
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("未找到数据");
                return baseResp;
            }

        } catch (Exception e) {
            e.printStackTrace();
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("系统异常");
            return baseResp;

        }

    }
}
