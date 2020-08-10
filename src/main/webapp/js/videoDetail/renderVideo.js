// console.log(" %c 该项目基于Dplayer.js", 'color:red')

function getDplayer() {
    console.log("这是"+videoUrl)
    dp = new DPlayer({
        element: document.getElementById('Dplayer'),
        video: {
            // url: 'xiaoli.mp4',
            // url: 'http://media.w3.org/2010/05/sintel/trailer.mp4',
            // url: 'http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4',
            // url: videoUrl,
            url: videoUrl,
            // url: 'http://www.yimem.com/group1/M00/00/01/wKgBBV8rtdqALyYBAiZGVZwzj1s71.flac',
            // url: 'http://huo.hongjiaozuida.com/20200705/6585_9d441eb5/无声的证言第十一季-05.mp4',
            // url: 'dpplay.zuidajiexi.com/?url=https://xigua-cdn.haima-zuida.com/20200528/7617_d15ce169/index.m3u8',
            // url: 'https://vd1.bdstatic.com/mda-hgu96tg1ddv2gie2/hd/mda-hgu96tg1ddv2gie2.mp4?auth_key=1566017205-0-0-25ba1683b1aca9cae40098f98022fcbc&bcevod_channel=searchbox_feed&pd=wisenatural&abtest=all',
            // url: 'C:/Users/Administrator.USER-20190521DJ/Desktop',
            pic: coverUrl
            // pic: '../img/danmu.jpg'
        },
        danmaku: {
            maximum: 1000,
            bottom: '100px',
            unlimited: true
        },
        // theme: "yellow",
        hotkey: true,
        // screenshot:true, //跨域问题
        apiBackend: {
            //后端读入数据接口
            read: function (endpoint, callback) {
                // console.log('Pretend to connect WebSocket');
                // console.log("endPoint:" + endpoint);
                // console.log("callBack:" + callback);
                // callback();
                // console.log("调用查询方法。。。。");
                alert_back(); //弹幕正在生成中，弹出框
                // console.log("获取弹幕中：" + videoId);
                // readFromServer(videoId);
                // console.log("调用查询方法结束。。。。");
            },
            //发送到后端保存接口
            send: function (endpoint, danmakuData, callback) {
                // console.log('Pretend to send danamku via WebSocket:', danmakuData);
                // console.log('-------------')
                // console.log("endPoint:" + endpoint);
                // console.log("callBack:" + callback);
                // callback();
                console.log("调用发送方法。。。。");
                console.log("原始弹幕");
                console.log(danmakuData);
                sendToServer(danmakuData);
                // console.log("调用发送方法结束。。。。");
            }
        },

    });


}

function alert_back(text) {
    $(".alert_back").html(text).show();
    setTimeout(function () {
        $(".alert_back").fadeOut();
    }, 1200)
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
    console.log("time:" + time);

    let times = (time + "").split(":");
    console.log("times:" + times[0] + times[1] + times[2]);
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
        url:  "videos/" + videoID + "/bullets",
        type: "get",
        // data:jQuery.serialize(),
        dataType: "json",
        success: function (data) {
            console.log(data);
            if (data.code === 200) {
                danmakuList = data.data;
                //渲染右侧弹幕列表
                renderBulletsList();
                $(".dplayer .dplayer-danmaku").css("bottom", "60px");
                addListener();
                setTimeout(function () {
                    $(".dplayer-danloading").hide();
                }, 1200);
            }
        },
        error: function (xhr) {
            console.log("ajax失败...");
            console.log(JSON.parse(xhr.responseText));
            switch (xhr.status) {
                case 400: {
                    $(".dplayer-danloading").text("弹幕加载失败,非法入参错误。");
                }
                case 404: {
                    $(".dplayer-danloading").text("弹幕加载失败,资源未找到。");
                    setTimeout(function () {
                        $(".dplayer-danloading").hide();
                    }, 3000);
                    break;
                }
                case 500: {
                    $(".dplayer-danloading").text("弹幕加载失败,服务器异常。");
                    break;
                }
                default: {
                    $(".dplayer-danloading").text("弹幕加载失败,其他异常。");
                    break;
                }
            }
        }
    });
}

/**
 * video绑定事件
 * @author qml
 */
function addListener() {
    //dataList默认按time升序排
    /**
     * 当发生跳转时，游标置0，重新开始渲染逻辑。
     */
    dp.video.onseeked = function () {
        let newTime = dp.video.currentTime;
        // console.log("newTime:" + newTime);
        // console.log("===================================");

        for (let i = 0; i < danmakuList.length; i++) {
            // console.log(formartSeconds(danmakuList[i].currentTime));
            // console.log(formartSeconds(danmakuList[i].currentTime) > parseFloat(newTime));
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
        let data = danmakuList[dataIndex];
        // console.log("打印单条数据：");
        // console.log(data);
        if (data && currentTime == data.currentTime) {
            // console.log("匹配到了一个弹幕,dataIndex:" + dataIndex);
            const danmaku = {
                text: data.msg,
                color: data.color,
                type: data.type
            }
            // console.log("需要发送的弹幕：" + danmaku)
            dp.danmaku.draw(danmaku);
            dataIndex++;
        }
    };
    dp.on("play", function () {
        console.log("点击了播放。。。。。。");

        userBean = isLogin();
        if(userBean){
            addHistory();
        }else{
            layer.msg("登录后可以添加到历史记录中哦");
        }

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
function sendToServer(newDanmaku) {
    console.log("发送弹幕，验证用户信息");
    userBean = isLogin();
    if (!userBean) {
        toLoginPage();
    }
    console.log("isLogin返回值：" + userBean.userId);
    console.log("发送弹幕，验证用户信息结束。。。。。登录状态");
    $.ajax({
        type: "post",
        contentType: "application/json;charset=utf-8",
        // /users/{userId}/videos/{videoId}/bullets
        url:  "users/" + userBean.userId + "/videos/" + videoId + "/bullets/",
        data: JSON.stringify({
            userId: userBean.userId,
            videoId: videoId,
            color: newDanmaku.color,
            msg: newDanmaku.text,
            currentTime: formatTime(dp.video.currentTime),
            type: newDanmaku.type
        }),
        success: function (data) {
            console.log(data);
            //更新弹幕总数：
            // if (data.code == 201) {
            let count = parseInt($(".smallTitle #bulletCount").text());
            $(".smallTitle #bulletCount").text(count + 1);
            // dp.danmaku.draw(data.data); //发送后自动显示，不需要。
            // }
            //更新弹幕列表。
            readFromServer(videoId);
        },
        error: function (xhr) {
            console.log("ajax失败。。。");
        }

    })
}

/**
 * 把视频添加到历史记录。。。。。
 */
function addHistory(){
    jQuery.ajax({
        // users/{usersId}/histories
        url:"users/"+userBean.userId+"/histories",
        type:"post",
        contentType:"application/json",
        data:JSON.stringify({
            userId:userBean.userId,
            videoId: videoId
        }),
        dataType:"json",
        success:function (data) {
            console.log(data);
            layer.msg("已添加到历史记录，(づ￣3￣)づ╭❤～");

        },
        error:function (xhr) {
            console.log("ajax失败...")
        }
    });

}
