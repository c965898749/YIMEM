//package com.sy.test;
//
//import com.sy.model.User;
//import com.sy.model.resp.BaseResp;
//import com.sy.service.AskService;
//import com.sy.service.SearchService;
//import com.sy.service.UserServic;
//import com.sy.service.WeixinPostService;
//import com.sy.tool.RedisUtil;
////import org.junit.Test;
////import org.junit.runner.RunWith;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
////@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:spring.xml")
//public class UserTest {
//    @Autowired
//    private UserServic userServic;
//    @Autowired
//    private AskService askService;
//    @Autowired
//    private SearchService searchService;
//    @Autowired
//    private WeixinPostService weixinPostService;
//    @Test
//    public void FindTest() throws Exception {
//    }
//
//    @Test
//    public void AA() throws Exception {
//      RedisUtil.getJedisInstance().set("aa","1111");
//       String aa=RedisUtil.getJedisInstance().get("aa");
//    }
//    @Test
//    public void AA1() throws Exception {
//        RedisUtil.getJedisInstance().del("aa");
//    }
//    @Test
//    public void AA2() throws Exception {
//        BaseResp baseResp=searchService.queryAll("生化危机");
//        System.out.println(baseResp.toString());
//    }
//    @Test
//    public void AA3() throws Exception {
//       String aa= weixinPostService.upload("D:\\VRMS-Workspace\\gz.jpg","","image");
//        System.out.println(aa);
//    }
////@Test
//////    public void addUserTest() throws Exception {
//////    }
//
////    @Test
////    public void modifyHeadImgByUseridTest() throws Exception {
////        System.out.println(userServic.modifyHeadImgByUserid(1,"headImg/45a64a8b287d4b7aa50ee22994767671.png"));
////    }
////@Test
////    public void findUserInforIncludeMsgTest() throws Exception {
////    System.out.println(userServic.findUserInforIncludeMsg(1));
////}
////    @Test
////    public void Test2() throws Exception {
////        System.out.println(askService.selectAsksByCondition(null,0,1,0,1));
////    }
//
//
//}
