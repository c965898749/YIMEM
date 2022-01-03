// ////console.log(" %c 该项目基于Dplayer.js", 'color:red')

function getDplayer(videoId) {
     // console.log("这是"+videoUrl)
    // const userId = 1
    dp = new DPlayer({
        element: document.getElementById('Dplayer'),
        // screenshot: true,
        video: {
            url: videoUrl,
            // pic: coverUrl
            // pic: 'http://www.yimem.com/group1/M00/00/00/wKgBBV7dFPCAKpdxAAA-0c-9Y4o668.jpg'
            pic: '/imgs/loading/fm.jpg'
        },
        danmaku: {
            id: videoId,
            // user: userId,
            api: 'http://www.yimem.com/bullets/',    //这里填写弹幕地址
            // addition: ['https://s-sh-17-dplayercdn.oss.dogecdn.com/1678963.json'],
            // addition: ['http://localhost:8889/v3/bullets?aid='+videoId],
            // maximum: 1000,
            bottom: '100px',
            // top:'100px',
            unlimited: true,

        },
        // theme: "yellow",
        // live:true,
        hotkey: true,
        loop: true,
        logo:'/imgs/logo.png',
        contextmenu: [
        {
            text: '观看更多……',
            link: 'http://www.yimem.com/app.html',
        },
        {
            text: '下载视屏',
            click: (player) => {
                location.href = "downloadResource?id=" + videoId;
            },
        },
     ],
    });
    dp.danmaku.opacity(1);
    dp.on('danmaku_send', function () {
        if (getUser()==null){
            layer.open({
                content: '你还未登录'
                , btn: ['登录', '退出']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    location.href = "login.html";
                    layer.close(index);
                }
                , btn2: function (index, layero) {
                    layer.close(index);
                }
            });
            return false;
        }
    });

}



//秒转分秒
function formatTime(seconds) {
    return [
        parseInt(seconds / 60 / 60),
        parseInt(seconds / 60 % 60),
        parseInt(seconds % 60)
    ]
        .join(":")
        .replace(/\b(\d)\b/g, "0$1");
}

//时分秒转秒数
function formartSeconds(time) {
    ////console.log("time:" + time);

    let times = (time + "").split(":");
    ////console.log("times:" + times[0] + times[1] + times[2]);
    return parseFloat(times[0] * 60 * 60)
        + parseFloat(times[1] * 60)
        + parseFloat(times[2]);

}

/**
 * 从后台读取弹幕数据
 * 成功，提示弹幕正在加载中 1.2秒
 * 不成功，提示弹幕加载失败，并显示内容。
 * @param videoID
 * @author qml
 */
function readFromServer(videoID) {

    $.ajax({
        url: "/bullets/"+ videoID ,
        type: "get",
        // data:jQuery.serialize(),
        dataType: "json",
        success: function (data) {
            console.log(data);
            if (data.success === 200) {
                danmakuList = data.data;
                addListener();
            }
        },
        error: function (xhr) {

        }
    });
}

/**
 * video绑定事件
 * @author qml
 */
function addListener() {
    ////console.log("弹幕开始")
    //dataList默认按time升序排
    /**
     * 当发生跳转时，游标置0，重新开始渲染逻辑。
     */
    dp.video.onseeked = function () {
        let newTime = dp.video.currentTime;
       ////console.log("newTime:" + newTime);
        // ////console.log("===================================");

        for (let i = 0; i < danmakuList.length; i++) {
            // ////console.log(formartSeconds(danmakuList[i].currentTime));
            // ////console.log(formartSeconds(danmakuList[i].currentTime) > parseFloat(newTime));
            if (formartSeconds(danmakuList[i].currentTime) > parseFloat(newTime)) {
                dataIndex = Math.max(i - 1, 0);
                break;
            }
        }
    }

    /**
     * video当前时间该变时，动态渲染video
     * 按video当前播放时间，添加弹幕显示
     */
    dp.video.ontimeupdate = function () {
        let currentTime = formatTime(dp.video.currentTime);
       ////console.log(dp.video.currentTime)
        let data = danmakuList[dataIndex];
        if (data && currentTime == data.currenttime) {
            const danmaku = {
                text: data.msg,
                color: data.color,
                type: data.type
            }
            dp.danmaku.draw(danmaku);
            dataIndex++;
        }
    };
    dp.on("play", function () {


    });

}


/**
 * 前端DPlayer生成的弹幕格式。
 * @type {{color: string, text: string, type: string}}
 */
// const danmaku = {
//     text: 'Get a danamku via WebSocket',
//     color: '#fff',
//     type: 'right'
// };
// dp.danmaku.draw(data.data); //发送后自动显示，不需要。
/**
 * 发送新生成的弹幕（默认格式），转换到自定义格式，再传送到后端。
 * @param newDanmaku
 */
function sendToServer(videoId,newDanmaku, callback) {
   //console.log("发送弹幕，验证用户信息"+videoId);
    userBean = getUser();
    if (userBean!=null) {
        $.ajax({
            type: "post",
            url:  "/kk",
            dataType: "json",
            data: {
                userid: userBean.userId,
                videoid: videoId,
                color: newDanmaku.color,
                msg: newDanmaku.text,
                currenttime: dp.video.currentTime,
                // currenttime: dp.video.currentTime,
                type: newDanmaku.type
            },
            success: function (data) {
              callback()
            },
            error: function (xhr) {

            }

        })
    }
    ////console.log("isLogin返回值：" + userBean.userId);
    ////console.log("发送弹幕，验证用户信息结束。。。。。登录状态");

}


