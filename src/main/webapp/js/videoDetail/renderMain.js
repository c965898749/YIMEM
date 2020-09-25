// // var videoId = location.search.split("=")[1] || 1;
// var videoId = location.search.split("=")[1];
// console.log("videoId"+videoId);
// var userBean;
// var dataIndex = 0; //后台获取的弹幕列表的游标，用于实时渲染弹幕。
// var danmakuList;
// var coverUrl;  //封面地址
// var videoUrl;   //视频地址
// var pageNo = 1
//     , pageSize = 3;
// var currentPage="detail?videoId="+videoId;
// var layer;
// layui.use(['element', 'laypage', 'layer'], function () {
//     var $ = layui.jquery
//         , element = layui.element;//Tab的切换功能，切换事件监听等，需要依赖element模块
//     var laypage = layui.laypage
//         ,
//         layer = layui.layer;
//     //从服务器拉取，视频+弹幕+视频信息+用户信息
//
//     getVideoDetail();
//     //渲染用户在线状态。。。
//     renderUserInfo();
//     //评论发表框点击事件。
//     addCommClickListener();
//
//     //添加收藏点击动态效果。
//     updateCollectCount();
//     //添加我喜欢点击效果。
//     updateLikeCount()
//     // //完整功能
//     // laypage.render({
//     //     elem: 'demo7'
//     //     , count: 100
//     //     , layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
//     //     , jump: function (obj) {
//     //         console.log(obj)
//     //     }
//     // });
// });
//
// /***
//  * 从后台，获取视频详情。
//  * @param id    视频主键id
//  */
// function getVideoDetail() {
//     $.ajax({
//         url:  "videos/" + videoId + "/",
//         type: "get",
//         async: false,
//         dataType: "json",
//         success: function (data) {
//             console.log("获取视频详情地址：");
//             console.log(data);
//             if (data.code == 200) {
//                 // console.log("获取视频详情");
//
//                 videoUrl = data.data.videoUrl;
//                 //如果是本地上传的视频，加前缀localhost;
//                 console.log("-----------------------------------")
//                 if (videoUrl.indexOf("http") === -1) {
//                     videoUrl =  videoUrl;
//                 }
//                 console.log("视频地址：" + videoUrl);
//                 console.log("-----------------------------------")
//                 coverUrl = data.data.coverUrl;
//                 //如果是本地上传的视频，加前缀localhost;
//                 if (coverUrl.indexOf("http") === -1) {
//                     coverUrl =  coverUrl;
//                 }
//                 console.log("封面地址：" + coverUrl);
//                 console.log("-----------------------------------")
//                 // 视频地址：http://localhost:8888/v1.0/upload/cover/b89be610c22d484591c7dfa22959f735.mp4
//                 // -----------------------------------
//                 // 封面地址：http://localhost:8888/v1.0/upload/video/91e6a9bdaaa44d39bd932fff93c2f9e0.jpg
//                 // -----------------------------------
//
//                 //加载视频详情,包括后台弹幕列表
//                 renderVideoInfo(data.data);
//                 // //加载评论详情
//                 getComments();
//                 // //加载弹幕播放器
//                 getDplayer();
//             }
//         },
//         error: function (xhr) {
//             console.log("ajax失败...")
//         }
//     });
//
// }
//
// function renderUserInfo(){
//     userBean = isLogin();
//     console.log("渲染初始用户信息，userBean：");
//     console.log(userBean);
//     //如果是登录状态。
//     if(userBean){
//         $(".userInfoIcon").attr("src",userBean.icon);
//         $(".userInfoName").text(userBean.nickname);
//         $(".userInfoRemark").text(userBean.remark);
//     }
// }
