package com.sy.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.sy.mapper.*;
import com.sy.model.Blog;
import com.sy.model.Fans;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.FansService;
import com.sy.tool.Constants;
import com.sy.tool.HTMLSpirit;
import com.sy.tool.RedisCache;
import com.sy.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FansServiceImpl implements FansService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private FansMapper fansMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private BlogReplayMapper blogReplayMapper;


    @Override
    public BaseResp queryByFansIdResult(Integer fansId) {
        BaseResp baseResp = new BaseResp();
        List<String> list = fansMapper.queryByfansid(fansId);
        Map<Integer, List<Blog>> map = new HashMap<>();

        if (list != null) {
            for (String string : list) {
                int userId = Integer.parseInt(string);
//
                Blog blogCondition = new Blog();
                blogCondition.setUserid(userId);
                List<Blog> blogList = blogMapper.queryByUserId(blogCondition);
                map.put(userId, this.filtration(blogList));
            }
            baseResp.setSuccess(1);
            baseResp.setData(map);
            return baseResp;
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("用户没有关注任何人");
        }
        return null;
    }

    @Override
    public BaseResp queryIsFocus(int fansedid, int fansid) {
        BaseResp baseResp = new BaseResp();
        List<Fans> fansList = fansMapper.queryIsFocus(fansedid, fansid);
        if (fansList.size() != 0) {
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("关注");
        } else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("未关注");
        }
        return baseResp;
    }

    private String buildLock(Integer userId, Integer productId) {
        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(":Focus:");
        sb.append(productId);
        String lock = sb.toString().intern();

        return lock;
    }

    @Override
    public BaseResp addFocus(int fansedid, int fansid) {
        String lock = buildLock(fansedid, fansid);
        synchronized (lock) {
            BaseResp baseResp = new BaseResp();
            Integer flag = userMapper.selectFansByFansedidAndFansid(fansedid, fansid);
            if (flag > 0) {
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("已关注过");
            } else {
                fansMapper.addFocus(fansedid, fansid);
                fansMapper.addfocusLog(fansedid, fansid,1);
                baseResp.setSuccess(1);
                baseResp.setErrorMsg("关注成功");
//                if (result != 0) {
//                    User user = userMapper.selectUserByUserId(fansedid);
//                    Integer unreadfanscount = user.getUnreadfanscount() + 1;
////            Integer FansCount= user.getFansCount()+1;
//                    user.setUnreadfanscount(unreadfanscount);
////            user.setFansCount(FansCount);
//                    userMapper.updateuser(user);
//                    baseResp.setSuccess(1);
//                    baseResp.setErrorMsg("关注成功");
//                } else {
//                    baseResp.setSuccess(0);
//                }
            }
            return baseResp;
        }

    }

    @Override
    public BaseResp deleteFocus(int fansedid, int fansid) {
        BaseResp baseResp = new BaseResp();
//        Integer count=fansMapper.queryStatusByFocus(fansedid,fansid);
//        System.out.println(count);
//        if (count>0){
//            User user=userMapper.selectUserByUserId(fansedid);
//            Integer unreadfanscount = user.getUnreadfanscount() - 1;
//            Integer FansCount= user.getFansCount()-1;
//            user.setUnreadfanscount(unreadfanscount);
//            user.setFansCount(FansCount);
//            user.setUnreadfanscount(unreadfanscount);
//            userMapper.updateuser(user);
//        }
        int result = fansMapper.deleteFocus(fansedid, fansid);
        if (result != 0) {
            fansMapper.addfocusLog(fansedid, fansid,0);
            baseResp.setSuccess(1);
            baseResp.setErrorMsg("取消关注成功");
        } else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    public Map queryAllFans(int userId, int page) {
        int pageSize = 6;
        Map<String, Object> map = new HashMap<>();
        //查询粉丝日志
        List<Fans> fansList = fansMapper.selectpage(userId, page-1, pageSize);
        List<String> nameLists = new ArrayList<>();
        if (fansList.size() != 0) {
            for (Fans fans : fansList) {
                List<String> nameList = new ArrayList<>();
                int fansId = fans.getFansid();
                if (fans.getStatus() == 0) {
                    continue;
                }
                String fansName = userMapper.selectUserByUserId(fansId).getNickname();
                nameList.add(fansName);
                nameList.add(fans.getFansid() + "");
                nameList.add(fans.getStatus() + "");
                String json = JSONObject.toJSONString(nameList);
                nameLists.add(json);
            }
            map.put("data", nameLists);
            Integer count = userMapper.selectUserByUserId(userId).getUnreadfanscount();
            map.put("count", count);
            map.put("count2", fansMapper.selectNoReadCount(userId));
        } else {
            map.put("data", "");
            map.put("count", 0);
            map.put("count2", 0);
        }
        return map;
        //弃用redis
//        String key = Constants.FANS_INFORMATION + userId;
//        if (RedisUtil.getJedisInstance().exists(key)) {
//            System.out.println("进入缓存2");
//            List<String> json = RedisUtil.getJedisInstance().lrange(key, pageSize * (page - 1), pageSize * page - 1);
//            map.put("success", 1);
//            map.put("count", RedisUtil.getJedisInstance().get("fcount" + key));
//            map.put("count2", RedisUtil.getJedisInstance().llen(key));
//            map.put("data", json);
//            RedisUtil.closeJedisInstance();
//            return map;
//        }else {
//            System.out.println("进入数据库2");
//            List<Fans> fansList = fansMapper.queryAllFans(userId);
//            List<String> nameLists = new ArrayList<>();
//            if (fansList.size() != 0) {
//                for (Fans fans : fansList) {
//                    List<String> nameList = new ArrayList<>();
//                    int fansId = fans.getFansid();
//                    if (fans.getStatus() == 0) {
//                        continue;
//                    }
//                    String fansName = userMapper.selectUserByUserId(fansId).getNickname();
//                    nameList.add(fansName);
//                    nameList.add(fans.getFansid() + "");
//                    nameList.add(fans.getStatus() + "");
//                    String json = JSONObject.toJSONString(nameList);
//                    RedisUtil.getJedisInstance().rpush(key, json);
//                    nameLists.add(json);
//                }
//                map.put("count2", nameLists.size());
////                list分页
//                try {
//                    map.put("data", nameLists.subList(pageSize * (page - 1), pageSize * page));//从下标0开始，找到第10个 即0-9
//                } catch (IndexOutOfBoundsException e) {
//                    map.put("data", nameLists.subList(pageSize * (page - 1), nameLists.size()));//数组越界异常时，取到最后一个元素
//                }
//                map.put("success", 1);
//                Integer count = userMapper.selectUserByUserId(userId).getUnreadfanscount();
//                RedisUtil.getJedisInstance().set("fcount" + key, count + "");
//                RedisUtil.getJedisInstance().expire("fcount" + key, 600);
//                RedisUtil.getJedisInstance().expire(key, 600);
//                RedisUtil.closeJedisInstance();
//                map.put("count", count);
//            } else {
//                RedisUtil.getJedisInstance().set("fcount" + key, 0 + "");
//                RedisUtil.getJedisInstance().expire("fcount" + key, 600);
//                RedisUtil.closeJedisInstance();
//                map.put("count", 0);
//                map.put("success", 0);
//            }
//            return map;
//    }

    }

    //个人详情页增加取消关注合并
    @Override
    public BaseResp addAndremoveFans(int viweUserId, int userId, String type) throws Exception {
        BaseResp baseResp = new BaseResp();
        int result = 0;
        if ("attention".equals(type)) {
            result = fansMapper.addFocus(viweUserId, userId);
        } else {
            result = fansMapper.deleteFocus(viweUserId, userId);
        }
        if (result > 0) {
            baseResp.setErrorMsg("取消关注成功");
            baseResp.setSuccess(1);
        } else {
            baseResp.setErrorMsg("取消关注失败");
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    //    @RedisCache(Constants.FANS_INFORMATION)
    @Override
    public void removefansaa(Integer userId) {
//        List<Integer> list =blogMapper.queryBlogIdByUserId(userId);
//        blogReplayMapper.removecommentreq(list);
        List<Fans> list = fansMapper.queryAllFans(userId);
        fansMapper.removefansaa(userId);
        userMapper.removefansaa(userId);
    }


    //    过滤博客
    public List<Blog> filtration(List<Blog> blogList) {
//        System.out.println("进行过滤");
        if (!blogList.isEmpty() && blogList != null) {
            for (int i = 0; i < blogList.size(); i++) {
                Blog blog = blogList.get(i);
                User user = userMapper.selectUserByUserId(blog.getUserid());
                Integer likeCount = likeMapper.queryCountByBlogId(blog.getId());
                Integer replayCount = blogReplayMapper.queryReplayCountByBlogId(blog.getId());
                Integer fansCount = userMapper.selectFansCountbyUserId(blog.getUserid());
                if (user != null) {
                    blog.setContent(HTMLSpirit.delHTMLTag(blogList.get(i).getContent()));
                    blog.setLikeCount(likeCount);
                    blog.setUsername(user.getNickname());
                    blog.setHeadimg(user.getHeadImg());
                    blog.setUserFansCount(fansCount);
                    blog.setReplayCount(replayCount);
                    blog.setUserIndustry(user.getIndustry());
                    blog.setUserDescr(user.getDescription());
                }
                blogList.set(i, blog);
            }
        }
        return blogList;
    }
}
