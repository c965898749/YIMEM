/**
 * 判断登录通用方法，
 * 如果不登录，跳转到登录页面
 * 如果登录了，赋值userId
 */
function isLogin() {
    // let sessionUser;
    console.log("------------------这是是否登录验证方法------------")
    var data;
    $.ajax({
        url: "isEnter"
        , type: "get"
        , dataType: "json"
        , async: false
        , success: function (jsonData) {
            ////////////////console.log(jsonData)
            if (jsonData.success == 1) {
                data = jsonData.data;
            }
        }
        , error: function (res) {
            ////////////////console.log("ajax提交错误")
        }
    })
    return data;
    // return sessionUser;
    console.log("------------------这是是否登录验证方法，结束------------")
}

/***
 * 跳到登录页面。。。。。
 */
function toLoginPage(){
    layer.msg('您未登录，是否跳转到登录页面。', {
        time: 0 //不自动关闭,
        , icon: 1
        , btn: ['好的', '不用了']
        , yes: function (index) {
            layer.close(index);
            // location.href = testPath + "/html/user/login.html#" + currentPage;
            window.open(testPath + "/html/user/login.html#" + currentPage, "_blank");
        }
    });
}