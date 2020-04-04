<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <!--
            Charisma v1.0.0

            Copyright 2012 Muhammad Usman
            Licensed under the Apache License v2.0
            http://www.apache.org/licenses/LICENSE-2.0

            http://usman.it
            http://twitter.com/halalit_usman
        -->
        <meta charset="utf-8">
        <title>YM管理后台</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Charisma, a fully featured, responsive, HTML5, Bootstrap admin template.">
        <meta name="author" content="Muhammad Usman">

        <!-- The styles -->
        <link href="/statics/css/bootstrap-cerulean.css" rel="stylesheet">
        <style type="text/css">
            body {
                padding-bottom: 40px;
            }

            .sidebar-nav {
                padding: 9px 0;
            }

            .navbar .nav li a {
                border: 0px;
            }

            .custom-setting {
            }

            .clear {
                clear: both;
            }

            div .modal-body label {
                color: black;
            }
        </style>

        <link href="/statics/css/bootstrap-responsive.css" rel="stylesheet">
        <link href="/statics/css/charisma-app.css" rel="stylesheet">
        <link href="/statics/css/jquery-ui-1.8.21.custom.css" rel="stylesheet">
        <link href='/statics/css/fullcalendar.css' rel='stylesheet'>
        <link href='/statics/css/fullcalendar.print.css' rel='stylesheet' media='print'>
        <link href='/statics/css/chosen.css' rel='stylesheet'>
        <link href='/statics/css/uniform.default.css' rel='stylesheet'>
        <link href='/statics/css/colorbox.css' rel='stylesheet'>
        <link href='/statics/css/jquery.cleditor.css' rel='stylesheet'>
        <link href='/statics/css/jquery.noty.css' rel='stylesheet'>
        <link href='/statics/css/noty_theme_default.css' rel='stylesheet'>
        <link href='/statics/css/elfinder.min.css' rel='stylesheet'>
        <link href='/statics/css/elfinder.theme.css' rel='stylesheet'>
        <link href='/statics/css/jquery.iphone.toggle.css' rel='stylesheet'>
        <link href='/statics/css/opa-icons.css' rel='stylesheet'>
        <link href='/statics/css/uploadify.css' rel='stylesheet'>
        <!-- 华丽丽滴分割线 start -->
        <!-- start-->
        <link href='/statics/localcss/userlist.css' rel='stylesheet'>
        <link href='/statics/localcss/rolelist.css' rel='stylesheet'>
        <link href='/statics/localcss/authoritymanage.css' rel='stylesheet'>
        <link href='/statics/localcss/dicmanage.css' rel='stylesheet'>
        <link href='/statics/localcss/affiche.css' rel='stylesheet'>
        <link href='/statics/localcss/information.css' rel='stylesheet'>
        <link href='/statics/localcss/addgoodspack.css' rel='stylesheet'>
        <link href='/statics/localcss/mymessage.css' rel='stylesheet'>
        <link href='/statics/localcss/messagelist.css' rel='stylesheet'>
        <!-- end-->
        <script src="/statics/js/const.js"></script>
        <!-- 华丽丽滴分割线  end -->
        <!-- The HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- The fav icon -->
        <link rel="shortcut icon" href="/statics/img/favicon.ico">
        <script src="/statics/js/jquery.js"></script>
        <script>
            var User = getMenus()
            var tt = User.menus;
            $(function () {

            })

            function getMenus() {
                var data;
                $.ajax({
                    url:"isLogin.html"
                    , type: "get"
                    , dataType: "json"
                    , async: false
                    , success: function (jsonData) {
                      //console.log(jsonData)
                        if (jsonData.success == 1) {
                            data = jsonData.data;

                        }
                    }
                    , error: function (res) {
                        ////////////////console.log("ajax提交错误")
                    }
                })
                return data;
            }


        </script>
    </head>
    <body>
        <!-- topbar starts -->
        <div class="navbar">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a class="btn btn-navbar" data-toggle="collapse"
                       data-target=".top-nav.nav-collapse,.sidebar-nav.nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>
                    <a class="brand" href="/main.html"> <img alt="Charisma Logo" src="/statics/img/logo20.png"/> <span>YM管理后台</span></a>
                    <div class="btn-group pull-right" id="UserTing">
                        <ul class="nav">
                            <%--<li><a href="#">{{Code}}</a></li>--%>
                            <li><a href="#">{{name}}</a></li>
                            <li><a href="/main.html">首页</a></li>
                            <%--<li><a href="#">购物车</a></li>--%>
                            <li><a href="#">留言板</a></li>
                            <li><span  class="btn-setting modifypwd">修改密码</span></li>
                            <li><a href="/logout.html">注销</a></li>
                        </ul>
                    </div>
                    <div class="modal hide fade" id="myModal">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">×</button>
                            <h3>修改密码</h3>
                        </div>
                        <div class="modal-body">
                            <p>
                                <label>请输入原密码：</label>
                                <input id="oldpassword" type="password">
                                <span style="color:red;font-weight: bold;">*</span>
                                <label>请输入新密码：</label>
                                <input id="newpassword" type="password">
                                <span style="color:red;font-weight: bold;">*新密码必须6位以上</span>
                                <label>再次输入新密码：</label>
                                <input id="aginpassword" type="password">
                                <span style="color:red;font-weight: bold;">*</span>
                            </p>
                            <p id="modifypwdtip">
                            </p>
                        </div>
                        <div class="modal-footer">
                            <a href="#" class="btn" data-dismiss="modal">取消</a>
                            <a href="#" id="modifySavePassword" class="btn btn-primary">修改</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- topbar ends -->
        <div class="container-fluid">
            <div class="row-fluid">

                <!-- left menu starts -->
                <div class="span2 main-menu-span">
                    <div class="well nav-collapse sidebar-nav">

                        <!--左边菜单,通过脚本控制动态写入-->
                        <ul class="nav nav-tabs nav-stacked main-menu" id="menus">


                        </ul>
                        <!--
                        <label id="for-is-ajax" class="hidden-tablet" for="is-ajax"><input id="is-ajax" type="checkbox"> Ajax on menu</label>
                         -->
                    </div><!--/.well -->
                </div><!--/span-->
                <!-- left menu ends -->

                <noscript>
                    <div class="alert alert-block span10">
                        <h4 class="alert-heading">Warning!</h4>
                        <p>You need to have <a href="http://en.wikipedia.org/wiki/JavaScript"
                                               target="_blank">JavaScript</a> enabled to use this site.</p>
                    </div>
                </noscript>

                <div id="content" class="span10">
