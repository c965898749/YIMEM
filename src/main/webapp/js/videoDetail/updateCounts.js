// /***
//  *
//  */
// function updateCollectCount() {
//     $("#collectCollect").on("click", function () {
//         console.log("点击了收藏");
//         userBean = isLogin();
//         if (!userBean) {
//             layer.msg("还未登录，无法收藏");
//         } else {
//             console.log("已登录，可以收藏。");
//             addCollect();
//         }
//     })
// }
//
// function addCollect() {
//     jQuery.ajax({
//         // users/{userId}/collects
//         url:  "users/" + userBean.userId + "/collects",
//         contentType: "application/json;charset=utf-8",
//         type: "post",
//         data: JSON.stringify({
//             videoId: videoId,
//             userId: userBean.userId,
//         }),
//         dataType: "json",
//         success: function (data) {
//             console.log(data);
//             layer.msg("已发送收藏动态给up主，么么哒~");
//             //更新数据库收藏数后,再把显示收藏数局部刷新一下。
//             let collectCount = parseInt($("#collectCount").text()) + 1;
//             console.log(collectCount);
//             $("#collectCount").text(collectCount);
//
//         },
//         error: function (xhr) {
//             console.log("ajax失败...")
//         }
//     });
// }
//
// function updateLikeCount() {
//     $("#likeLike").on("click", function () {
//         console.log("点击了喜欢");
//         userBean = isLogin();
//         if (!userBean) {
//             layer.msg("还未登录，无法点赞，o(￣▽￣)ｄ");
//         } else {
//             let likeCount = parseInt($("#likeCount").text()) + 1;
//             updatelickCountDB(likeCount);
//         }
//     })
// }
//
// function updatelickCountDB(likeCount) {
//     jQuery.ajax({
//         url:  "users/" + userBean.userId + "/videos/"+videoId,
//         type: "put",
//         // contentTyp:"application/json;charset=utf-8",
//         contentType: 'application/json',
//         data: JSON.stringify({
//             videoId:videoId,
//             userId: userBean.userId,
//             likeCount: likeCount,
//         }),
//         dataType: "json",
//         success: function (data) {
//             console.log(data);
//             //更新点赞数
//             $("#likeCount").text(data.data.likeCount);
//             layer.msg("感谢点赞，数据库更新啦~⁽⁽ଘ( ˊᵕˋ )ଓ⁾⁾*")
//         },
//         error: function (xhr) {
//             console.log("ajax失败...")
//         }
//     });
//
// }
