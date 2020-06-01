/***
 * 渲染视频信息页，
 * 包括视频信息，用户信息。
 * @param data
 */
function renderVideoInfo(data) {
    console.log("渲染视频信息。。。。");
    console.log(data);
    //设置标题
    $(".bigtitle").text(data.title);

    //设置分类
    $("#classify1").text(data.classify.name);
    $("#classify2").text(data.classify.allClassify[0].name);

    console.log("封面地址：" + coverUrl);
    //设置播放次数，弹幕次数在，查找弹幕中写。。 //TODO
    $("#clickCount").text(data.clickCount);

    //设置点赞数
    $("#likeCount").text(data.likeCount);
    //设置收藏数
    $("#collectCount").text(data.collectCount);

    //设置简介
    $(".jianjie").html(data.info);

    //设置用户头像
    let iconUrl = data.userBean.icon;
    console.log("原始头像地址---------------------" + iconUrl);
    if (iconUrl.indexOf("http") === -1) {
        iconUrl =  iconUrl;
    }
    console.log("处理后的头像地址。---------------------" + iconUrl);
    $(".right .icon").attr("src", iconUrl);
    //设置用户名
    $(".right .name").text(data.userBean.nickname);
    //设置用户签名
    console.log(data.userBean.remark);
    $(".right .remark").text(data.userBean.remark);

    //设置视频简介
    $(".jianjie").text(data.info);
}

