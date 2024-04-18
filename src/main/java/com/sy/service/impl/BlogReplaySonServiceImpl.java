package com.sy.service.impl;

import com.alibaba.fastjson.JSON;
import com.sy.mapper.BlogMapper;
import com.sy.mapper.BlogReplayMapper;
import com.sy.mapper.InformationMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.BlogReplay;
import com.sy.model.Information;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.BlogReplaySonService;
import org.apache.commons.lang3.StringEscapeUtils;
//import org.springframework.amqp.core.AmqpTemplate;
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
//    @Autowired
//    private AmqpTemplate amqpTemplate;
    @Autowired
    private InformationMapper informationMapper;


    /**
     * 消息模块
     * @param blogReplaySon
     * @return
     */
    @Override
    public BaseResp insert(BlogReplay blogReplaySon) {

        BaseResp baseResp = new BaseResp();
        blogReplaySon.setStatus(0);
        blogReplaySon.setComment(StringEscapeUtils.escapeHtml4(blogReplaySon.getComment()));
        blogReplayMapper.updateCount(blogReplaySon.getBlogReplayId());
        Integer userid = blogMapper.queryUserIdById(blogReplaySon.getBlogid());
        if(userid!=blogReplaySon.getCommentuserid()){
            blogReplaySon.setStatus(1);
        }
        if (blogReplayMapper.addReplay(blogReplaySon) != 0) {
            baseResp.setSuccess(1);
        } else {
            baseResp.setSuccess(0);
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("回复成功");
        return baseResp;

    }

    public void faxiaoxi(BlogReplay blogReplaySon, Integer userid) {
        this.informationadd(blogReplaySon, userid);
        User user = userMapper.selectUserByUserId(userid);
        this.countjia(user);
//        amqpTemplate.convertAndSend("message.messge", JSON.toJSONString(userid));
    }

    public void informationadd(BlogReplay blogReplaySon, Integer userId) {
        Information information = new Information();
        information.setUserId(userId);
        information.setReplayUserId(blogReplaySon.getCommentuserid());
        information.setBlogId(blogReplaySon.getBlogid());
        information.setContent(blogReplaySon.getComment());
        informationMapper.insert(information);
    }

    public void countjia(User user) {
        Integer unreadreplaycount = user.getUnreadreplaycount() + 1;
        user.setUnreadreplaycount(unreadreplaycount);
        Integer commentCount = user.getCommentCount() + 1;
        user.setCommentCount(commentCount);
        userMapper.updateuser(user);
    }

    @Override
    public BaseResp queryBlogReplaySonByReplayId(Integer blogReplayId) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        BaseResp baseResp = new BaseResp();
        List<BlogReplay> list = new ArrayList<>();
        try {
            list = blogReplayMapper.queryBlogReplaySonByReplayId(blogReplayId);
            if (list != null) {
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
            } else {
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
