/**
 * 后台拿到评论数据,并渲染
 */
function getComments() {
    $.ajax({
        url:  "videos/" + videoId + "/comments",
        type: "get",
        data: {
            pageNo: pageNo,
            pageSize: pageSize
        },
        success: function (data) {
            console.log("评论信息：")
            console.log(data);
            renderComments(data.data);
        },
        error: function (xhr) {
            console.log("ajax失败-----")
            console.log("这里是评论ajax")
            // console.log(xhr.status);
            // console.log(xhr.code);
            // console.log(xhr.statusText);
            if (xhr.status === 404) {
                let notFoundMsg = "<span style='color: red'>谁都没有发表评论。</span>"
                $("#hotContent").html(notFoundMsg);
                // layer.msg("视频没有任何评论。。。。。");
            }
        }
    })

}

/**
 * 评论渲染方法
 */
function renderComments(dataList) {
    //评论总数
    $("#commentsCount").text(dataList.length);
    //待添加的容器清空是个好习惯。。。
    $("#hotContent").empty();
    $.each(dataList, function (index, eachData) {
        let itemHtml = `
                        <div class="layui-tab-item layui-show" >
                        <!--评论内容区域-->
                            <div class="otherKuang ">
          
                                        <!--这里是头像-->
                                        <img src="` +  eachData.userBean.icon + `" class="icon">
                                <div class="othername">`+eachData.userBean.nickname+`</div>
                        
                                <div class="talkMess2">
                                    <!--这里是评论-->
                                ` + eachData.commonMsg + `
                                </div>
                                <div class="talkTime">
                                    <!--这里是时间-->
                                ` + eachData.createTime + `
                                </div>
                                <div class="commLike">
                                    <i class="layui-icon layui-icon-praise" style="font-size: 20px; color: grey;"></i>
                                    <!--点赞数-->
                                ` + eachData.likeCount + `
                                </div>
                            </div>
                            <!--回复列表渲染区域-->
                            <!--<div class=".replyList">-->
                                <!--回复列表渲染-->
                            <!--</div>-->
                            <!--&lt;!&ndash;回复输入框&ndash;&gt;-->
                            <!--<div class="reply">-->
                                <!--回复输入框-->
                            <!--</div>-->
                        </div>
                        <hr>
        `;
        // //添加回复列表到每个评论。
        // $(itemHtml).find(".reply").append(renderReplies(eachData.replyList));
        //评论添加到评论评论区
        $("#hotContent").append(itemHtml);
    })

}

/**
 * 回复渲染方法
 * @param dataList
 */
function renderReplies(dataList) {
    $.each(dataList, function (index, eachData) {
        let itemHtml = ``;

        //Todo 添加到对应评论尾部。
    });
}

/***
 * 添加评论发表框点击事件
 */
function addCommClickListener() {
    $("#addComm").on("click", function () {
        console.log("点击了发表。。。");
        userBean = isLogin();
        if (!userBean) {
            toLoginPage();
        } else {
            let newCommMsg = $("#newCommMsg").val();
            console.log($("#newCommMsg"))
            console.log("新评论内容。。。。" + newCommMsg)

            addComments(newCommMsg);
        }
    })
}

/**
 * 添加评论区的输入内容到数据库。
 */
function addComments(msg) {

    $.ajax({
        // users/{userId}/videos/{videoId}/comments
        url:  "users/" + userBean.userId + "/videos/" + videoId + "/comments",
        type: "post",
        contentType: "application/json;charset=utf-8",
        data: JSON.stringify({
            "userId": userBean.userId,
            "videoId": videoId,
            "commonMsg": msg
        }),
        dataType: "json",
        success: function (data) {
            console.log(data);
            //成功渲染。
            renderNewComm(data.data);
            //清空评论输入框。
            $("#newCommMsg").val("");
        },
        error: function (xhr) {
            console.log("ajax失败...")
        }
    });
}

function renderNewComm(data) {
    if ($("#commentsCount").text() == 0) {
        $("#hotContent").empty();
    }
    let itemHtml = `
                        <div class="layui-tab-item layui-show" >
                        <!--评论内容区域-->
                            <div class="otherKuang ">
          
                                        <!--这里是头像-->
                                        <img src="` +  data.userBean.icon + `" class="icon">
                                
                        
                                <div class="talkMess2">
                                    <!--这里是评论-->
                                ` + data.commonMsg + `
                                </div>
                                <div class="talkTime">
                                    <!--这里是时间-->
                                ` + data.createTime + `
                                </div>
                                <div class="commLike">
                                    <i class="layui-icon layui-icon-praise" style="font-size: 20px; color: grey;"></i>
                                    <!--点赞数-->
                                ` + data.likeCount + `
                                </div>
                            </div>
                            <!--回复列表渲染区域-->
                            <!--<div class=".replyList">-->
                                <!--回复列表渲染-->
                            <!--</div>-->
                            <!--回复输入框-->
                            <!--<div class="reply">-->
                                <!--回复输入框-->
                            <!--</div>-->
                        </div>
        `;
    // //添加回复列表到每个评论。
    // $(itemHtml).find(".reply").append(renderReplies(eachData.replyList));
    //评论添加到评论评论区
    $("#hotContent").append(itemHtml);
    let commCount = parseInt($("#commentsCount").text());
    $("#commentsCount").text(commCount+1);

}