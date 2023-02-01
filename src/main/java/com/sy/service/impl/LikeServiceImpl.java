package com.sy.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sy.mapper.BlogMapper;
import com.sy.mapper.LikeMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Blog;
import com.sy.model.Like;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.LikeService;
import com.sy.tool.Constants;
import com.sy.tool.RedisCache;
import com.sy.tool.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public BaseResp query(Integer blog_id, Integer userid) {
        BaseResp baseResp = new BaseResp();
        baseResp = new BaseResp();
        List<Like> likeList = likeMapper.queryByBlog_idAndUser_Id(blog_id,userid);
        if (likeList.size()!=0){

            baseResp.setSuccess(1);


        }else {
            baseResp.setSuccess(0);
            baseResp.setErrorMsg("该用户没有点赞该博客");
        }
        return baseResp;
    }



    private String buildLock(Integer userId, Integer productId) {
        StringBuilder sb = new StringBuilder();
        sb.append(userId);
        sb.append(":Like:");
        sb.append(productId);
        String lock = sb.toString().intern();

        return lock;
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp add(Integer blog_id, Integer userid) {
        String lock = buildLock(userid, blog_id);
        synchronized (lock) {
            BaseResp baseResp = new BaseResp();
            int i = likeMapper.addLike(blog_id,userid);
            if (i!=0){
                Integer userId=blogMapper.queryUserIdById(blog_id);
                User user=userMapper.selectUserByUserId(userId);
                Integer readquerylikecount= user.getReadquerylikecount()+1;
                Integer LikeCount =user.getLikeCount()+1;
                user.setLikeCount(LikeCount);
                user.setReadquerylikecount(readquerylikecount);
                userMapper.updateuser(user);
                baseResp.setSuccess(1);
            }else {
                baseResp.setSuccess(0);
            }
            return baseResp;
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public BaseResp delete(Integer blog_id, Integer userid) {
        BaseResp baseResp = new BaseResp();
        Integer count=likeMapper.queryStatusByUserId(blog_id,userid);
        if (count>0){
            Integer userId=blogMapper.queryUserIdById(blog_id);
            User user=userMapper.selectUserByUserId(userId);
            Integer readquerylikecount= user.getReadquerylikecount()-1;
            Integer LikeCount =user.getLikeCount()-1;
            user.setLikeCount(LikeCount);
            user.setReadquerylikecount(readquerylikecount);
            userMapper.updateuser(user);
        }
        int i = likeMapper.deleteLike(blog_id,userid);
        if (i!=0){
            baseResp.setSuccess(1);
        }else {
            baseResp.setSuccess(0);
        }
        return baseResp;
    }

    @Override
    public Map queryLikeInformation(int userId,int page) {
        int pageSize = 6;
        Map<String, Object> map = new HashMap<>();
        List<String> lists = new ArrayList<>();
        //首先通过userID查找所有的文章
        Blog blogCondition = new Blog();
        blogCondition.setUserid(userId);
        List<Blog> blogList = blogMapper.selectpage(userId, page, pageSize);
        if (blogList.size() != 0) {
            for (Blog blog : blogList) {
                int blogId = blog.getId();
                //通过blogId查找点赞表
                List<Like> likeList = likeMapper.queryByBlogId(blogId);
                for (Like like : likeList) {
                    //得到点赞的用户
                    int likeUserId = like.getUserid();
                    User user = userMapper.selectUserByUserId(likeUserId);
                    if (user != null) {
                        if (like.getStatus() == 0) {
                            continue;
                        }
                        String userName = user.getNickname();
                        List<String> list = new ArrayList<>();
                        list.add(blog.getTitle());
                        list.add(userName);
                        list.add(blogId + "");
                        list.add(like.getStatus() + "");
                        list.add(like.getId() + "");
                        String json = JSONObject.toJSONString(list);
                        lists.add(json);
                    }

                }
            }
            map.put("data", lists);
            Integer count = userMapper.selectUserByUserId(userId).getReadquerylikecount();
            map.put("count", count);
//            Integer count2=blogMapper.selectcount(userId);
            map.put("count2", lists.size());
            map.put("success", 1);
        }else {
            map.put("data", "");
            map.put("count", 0);
            map.put("count2", 0);
        }
        return map;
        //弃用redis
//        String key = Constants.LIKE_INFORMATION + userId;
//        if (RedisUtil.getJedisInstance().exists(key)) {
//            System.out.println("进入缓存3");
//            List<String> json = RedisUtil.getJedisInstance().lrange(key, pageSize * (page - 1), pageSize * page - 1);
//            map.put("success", 1);
//            map.put("count", RedisUtil.getJedisInstance().get("lcount" + key));
//            map.put("count2", RedisUtil.getJedisInstance().llen(key));
//            map.put("data", json);
//            RedisUtil.closeJedisInstance();
//            return map;
//        } else {
//            System.out.println("进入数据库3");
//            BaseResp baseResp = new BaseResp();
//            //存储姓名
//            //存储文章标题
//            List<String> lists = new ArrayList<>();
//            //首先通过userID查找所有的文章
//            Blog blogCondition=new Blog();
//            blogCondition.setUserid(userId);
//            List<Blog> blogList = blogMapper.queryByUserId(blogCondition);
//            if (blogList.size()!=0){
//                for (Blog blog: blogList){
//                    int blogId = blog.getId();
//                    //通过blogId查找点赞表
//                    List<Like> likeList = likeMapper.queryByBlogId(blogId);
//                    for (Like like :likeList){
//                        //得到点赞的用户
//                        int likeUserId = like.getUserid();
//                        User user = userMapper.selectUserByUserId(likeUserId);
//                        if (user!=null){
//                            if (like.getStatus()==0){
//                                continue;
//                            }
//                            String userName = user.getNickname();
//                            List<String> list=new ArrayList<>();
//                            list.add(blog.getTitle());
//                            list.add(userName);
//                            list.add(blogId+"");
//                            list.add(like.getStatus()+"");
//                            list.add(like.getId()+"");
//                            String json = JSONObject.toJSONString(list);
//                            RedisUtil.getJedisInstance().rpush(key, json);
//                            lists.add(json);
//                        }
//
//                    }
//                }
//                map.put("count2", lists.size());
////                list分页
//                try {
//                    map.put("data", lists.subList(pageSize * (page - 1), pageSize * page));//从下标0开始，找到第10个 即0-9
//                } catch (IndexOutOfBoundsException e) {
//                    map.put("data", lists.subList(pageSize * (page - 1), lists.size()));//数组越界异常时，取到最后一个元素
//                }
//                map.put("success", 1);
//                Integer count = userMapper.selectUserByUserId(userId).getReadquerylikecount();
//                System.out.println("count"+count);
//                RedisUtil.getJedisInstance().set("lcount" + key, count + "");
//                RedisUtil.getJedisInstance().expire("lcount" + key, 600);
//                RedisUtil.getJedisInstance().expire(key, 600);
//                RedisUtil.closeJedisInstance();
//                map.put("count", count);
//            }else {
//                RedisUtil.getJedisInstance().set("lcount" + key, 0 + "");
//                RedisUtil.getJedisInstance().expire("lcount" + key, 600);
//                RedisUtil.closeJedisInstance();
//                map.put("count", 0);
//                map.put("success", 0);
//            }
//            return map;
//        }

    }

//    @RedisCache(Constants.LIKE_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void removequeryLikeId(Integer userId) {
        List<Integer> list =blogMapper.queryBlogIdByUserId(userId);
        likeMapper.removequeryLikeId(list);
        userMapper.readqueryLikeId(userId);
    }

//    @RedisCache(Constants.LIKE_INFORMATION)
    @Override
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    public void onclickqueryLikeId(Integer blog_id, Integer userId) {
        likeMapper.onclickqueryLikeId(blog_id);
        User user=userMapper.selectUserByUserId(userId);
        Integer readquerylikecount= user.getReadquerylikecount()-1;
        if (readquerylikecount>=0){
            user.setReadquerylikecount(readquerylikecount);
            userMapper.updateuser(user);
        }
    }
}
