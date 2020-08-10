<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-cmn-Hans">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
    <title>WeUI</title>
    <link rel="stylesheet" href="css/weui.min.css"/>
</head>
<body>
<div class="container" id="container">

    <div class="hd">
        <h1 class="page_title">  </h1>
    </div>
    <div class="bd">
        <div class="weui_cells_title">账号绑定</div>
        <div class="weui_cells weui_cells_form">
            <div class="weui_cell">
                <div class="weui_cell_hd"><label class="weui_label">yimem账号</label></div>
                <div class="weui_cell_bd weui_cell_primary">
                    <input class="weui_input" type="text" id="username"  placeholder="请输入账号"/>
                </div>
            </div>
            <div class="weui_cell">
                <div class="weui_cell_hd"><label class="weui_label">yimem密码</label></div>
                <div class="weui_cell_bd weui_cell_primary">
                    <input class="weui_input" type="password"  id="userpassword" placeholder="请输入密码"/>
                </div>
            </div>
        </div>
        <div class="weui_cells_tips">若未注册账号可以先 <a style="color: blue" href="">注册</a></div>
        <div class="weui_btn_area">
            <p class="weui_btn_area">
                <a href="javascript:;" class="weui_btn weui_btn_primary" id="showToast">确定</a>
                <a href="javascript:;" class="weui_btn weui_btn_default" id="showLoadingToast">取消</a>
            </p>
        </div>
        <!--BEGIN toast-->
        <div id="toast" style="display: none;">
            <div class="weui_mask_transparent"></div>
            <div class="weui_toast">
                <i class="weui_icon_toast"></i>
                <p class="weui_toast_content">已完成</p>
            </div>
        </div>
        <div id="toast2" style="display: none;">
            <div class="weui_mask_transparent"></div>
            <div class="weui_toast">
                <i class="weui_icon_msg weui_icon_warn"></i>
                <p class="weui_icon_msg weui_icon_warn_content">账号密码有误</p>
            </div>
        </div>

    </div>
</div>
<script type="text/javascript" src="js/zepto.min.js"></script>
<!--<script type="text/javascript" src="js/example.js"></script>-->
<script src="js/jquery-3.3.1.min.js"></script>
<script>
    // toast
    $('#container').on('click', '#showToast', function () {
        var username=$("#username").val()
        var userpassword=$("#userpassword").val()
        console.log(username+"------------"+userpassword)
        $.ajax({
            url: "bingding"
            , type: "POST"
            , data: {"username": username,"userpassword":userpassword}
            , dataType: "json"
            // , async: false
            , success: function (jsonData) {
                console.log(jsonData)
                if (jsonData.success==1){
                    $('#toast').show();
                    setTimeout(function () {
                        $('#toast').hide();
                    }, 2000);
                } else {
                    $('#toast2').show();
                    setTimeout(function () {
                        $('#toast2').hide();
                    }, 2000);
                }
            }
            , error: function (res) {
                //////////////console.log("ajax提交错误")
            }
        })

    }).on('click', '#showLoadingToast', function () {

    });
</script>
</body>