<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@ page import="com.sy.model.ScanRecord" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="gb2312"/>
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>支付宝 - 网上支付 安全快速！</title>
    <link rel="icon" href="https://i.alipayobjects.com/common/favicon/favicon.ico" type="image/x-icon"/>
    <link rel="shortcut icon" href="https://i.alipayobjects.com/common/favicon/favicon.ico" type="image/x-icon"/>
    <link href="https://a.alipayobjects.com" rel="dns-prefetch"/>
    <link href="https://app.alipay.com" rel="dns-prefetch"/>
    <link href="https://my.alipay.com" rel="dns-prefetch"/>
    <link href="https://lab.alipay.com" rel="dns-prefetch"/>
    <link href="https://cashier.alipay.com" rel="dns-prefetch"/>
    <link href="https://financeprod.alipay.com" rel="dns-prefetch"/>
    <link href="https://shenghuo.alipay.com" rel="dns-prefetch"/>
    <script src="../js/qrcode.min.js"></script>
    <!-- seajs以及插件 -->
    <script charset="utf-8" crossorigin="anonymous" id="seajsnode"
            onerror="window.monitor && monitor.lost && monitor.lost(this.src)"
            src="https://a.alipayobjects.com:443/??seajs/seajs/2.2.3/sea.js,seajs/seajs-combo/1.0.0/seajs-combo.js,seajs/seajs-style/1.0.2/seajs-style.js,seajs/seajs-log/1.0.0/seajs-log.js,jquery/jquery/1.7.2/jquery.js,gallery/json/1.0.3/json.js,alipay-request/3.0.3/index.js"></script>

    <!-- seajs config 配置 -->
    <
    script >
    seajs.config({
        alias: {
            '$': 'jquery/jquery/1.7.2/jquery',
            '$-debug': 'jquery/jquery/1.7.2/jquery',
            'jquery': 'jquery/jquery/1.7.2/jquery',
            'jquery-debug': 'jquery/jquery/1.7.2/jquery-debug',
            'seajs-debug': 'seajs/seajs-debug/1.1.1/seajs-debug'
        },
        crossorigin: function (uri) {

            function typeOf(type) {
                return function (object) {
                    return Object.prototype.toString.call(object) === '[object ' + type + ']';
                }
            }

            var isString = typeOf("String");
            var isRegExp = typeOf("RegExp");

            var whitelist = [];

            whitelist.push('https://a.alipayobjects.com/');

            for (var i = 0, rule, l = whitelist.length; i < l; i++) {
                rule = whitelist[i];
                if (
                    (isString(rule) && uri.indexOf(rule) === 0) ||
                    (isRegExp(rule) && rule.test(uri))
                ) {

                    return "anonymous";
                }
            }
        },
        vars: {
            locale: 'zh-cn'
        }
    });
    </script>
    <link charset="utf-8" rel="stylesheet" href="https://a.alipayobjects.com:443/excashier/front/1.0.0/front-old.css"
          media="all"/>
    <style>
        #header {
            height: 60px;
            background-color: #fff;
            border-bottom: 1px solid #d9d9d9;
            margin-top: 0px;
        }

        #header .header-title {
            width: 250px;
            height: 60px;
            float: left;
        }

        #header .logo {
            float: left;
            height: 31px;
            width: 95px;
            margin-top: 14px;
            text-indent: -9999px;
            background: none;
        !important
        }

        #header .logo-title {
            font-size: 16px;
            font-weight: normal;
            font-family: "Microsoft YaHei", 微软雅黑, "宋体";
            border-left: 1px solid #676d70;
            color: #676d70;
            height: 20px;
            float: left;
            margin-top: 15px;
            margin-left: 10px;
            padding-top: 10px;
            padding-left: 10px;
        }

        .header-container {
            width: 950px;
            margin: 0 auto;
        }

        body,
        #footer {
            background-color: #eff0f1;
        }

        #footer #ServerNum {
            color: #eff0f1;
        }

        .login-switchable-container {
            background-color: #fff;
        }

        #order.order-bow .orderDetail-base,
        #order.order-bow .ui-detail {
            border-bottom: 3px solid #bbb;
            background: #eff0f1;
            color: #000;
        }

        .order-ext-trigger {
            position: absolute;
            right: 20px;
            bottom: 0;
            height: 22px;
            padding: 2px 8px 1px;
            font-weight: 700;
            border-top: 0;
            background: #b3b3b3;
            z-index: 100;
            color: #fff;
        }

        #partner {
            margin-top: 0;
            padding-top: 0;
            background-color: #eff0f1;
        }

        #order.order-bow .orderDetail-base, #order.order-bow .ui-detail {
            border-bottom: 3px solid #b3b3b3;
        }

        .payAmount-area {
            bottom: 36px;
        }

        .alipay-logo {
            display: block;
            width: 114px;
            position: relative;
            left: 0;
            top: 10px;
            float: left;
            height: 40px;
            background-position: 0 0;
            background-repeat: no-repeat;
            background-image: url(https://t.alipayobjects.com/images/T1HHFgXXVeXXXXXXXX.png);
        }

        .ui-securitycore .ui-label, .mi-label {
            text-align: left;
            height: auto;
            line-height: 18px;
            padding: 0;
            display: block;
            padding-bottom: 8px;
            margin: 0;
            width: auto;
            float: none;
            font: 14px/1.5 tahoma, arial, \5b8b\4f53;
        }

        .ui-securitycore .ui-form-item {
            position: relative;
            padding: 0 0 10px 0;
            width: 350px;

        }

        .ui-securitycore .ui-form-explain {
            height: 18px;
            /*display: block;*/
            font-family: tahoma, arial, \5b8b\4f53;
        }

        .ui-securitycore .edit-link {
            position: absolute;
            top: -3px;
            right: 0;
        }

        .ui-securitycore .ui-input {
            height: 28px;
            font-size: 14px;
        }

        .ui-securitycore .standardPwdContainer .ui-input {
            width: 340px;
        }

        .ui-securitycore .mobile-section.checkcode-section {
            margin-top: 10px;
        }

        /*安全服务化必将覆盖的样式*/
        .mobile-form .ui-securitycore .ui-form-item-mobile {
            display: none;
        }

        .mobile-form .ui-securitycore .ui-form-item-mobile .ui-label {

        }

        .mobile-form .ui-securitycore .ui-form-item-mobile .ui-form-text {
            display: none;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter {
            padding-left: 0;
            padding-right: 0;
            padding-bottom: 20px;
            position: relative;
            height: 87px;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-label {
            display: block;
            float: none;
            margin-left: 0;
            text-align: left;
            line-height: 18px !important;
            padding: 0 0 8px 0;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-form-field {
            /*display: block;*/
            zoom: 1;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-form-field:after {
            visibility: hidden;
            display: block;
            font-size: 0;
            content: " ";
            clear: both;
            height: 0;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-checkcode-input {
            height: 24px;
            line-height: 24px;
            width: 148px;
            border: 1px solid #ccc;
            padding: 7px 10px;
            float: left;
            display: block;
            font-size: 14px;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-checkcode-input:focus {
            color: #4d4d4d;
            border-color: #07f;
            outline: 1px solid #8cddff;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .eSend-btn {
            float: left;
            color: #08c;
        }

        #mobileSend {
            position: absolute;
            right: 0;
            top: 26px;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-checkcode-messagecode-btn {
            float: left;
            width: 178px;
            height: 40px;
            _height: 38px;
            line-height: 38px;
            _line-height: 35px;
            color: #676d70;
            font-size: 14px;
            font-weight: bold;
            text-align: center;
            border: 1px solid #ccc;
            border-radius: 1px;
            background: #f3f3f3;
            margin-left: 2px;
            padding-left: 0;
            padding-right: 0;

        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-checkcode-messagecode-disabled-btn {
            background: #cacccd;
            border: 1px solid #cacccd;
            color: #aeb1b3;
            font-weight: normal;
            cursor: default;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .reSend-btn {
            float: left;
            margin-top: 10px;
            color: #08c;
        }

        .ui-checkcode-messagecode-disabled-btn {

        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-form-field {
            display: block;
        }

        .mobile-form .ui-securitycore .ui-form-item-counter .ui-form-field .fn-hide,
        .mobile-form .ui-securitycore .ui-form-item-counter .fn-hide .reSend-btn {
            display: none;
        }

        /*安全服务化必将覆盖的样式*/


        .alieditContainer object {
            width: 348px;
            height: 38px;
        }

        #container .alieditContainer {
            width: 348px;
            height: 38px;
        }

        #container .alieditContainer a.aliedit-install {
            line-height: 38px;
        }

        /* 安全服务化去控件升级 特木 temu.psc@alipay.com */
        #container .alieditContainer .ui-input {
            width: 324px;
            padding: 7px 10px;
            font-size: 14px;
            height: 20px;
            line-height: 24px;
        }

        #container .alieditContainer .ui-input:focus {
            color: #4D4D4D;
            border-color: #07F;
            outline: 1px solid #8CDDFF;
            *padding: 7px 3px 4px;
            *border: 2px solid #07F;
        }


        .teBox {
            height: auto;
        }

        #J_loginPwdMemberT {
            padding: 20px 0 60px 0;
        }

        #J_loginPwdMemberT #teLogin {
            height: auto;
        }

        #J_loginPwdMemberT .mi-form-item {
            padding: 0 0 10px 0;
        }

        #J_loginPwdMemberT .teBox-in {
            padding: 0;
            width: 350px;
            margin: 0 auto;
        }

        .t-contract-container {
            width: 76%;
        }

        .contract-container {
            width: 450px;
            margin: 0 auto;
            text-align: left;
            position: relative;
        }

        .contract-container .contract-container-label {
            width: 450px;
        }

        .mb-text {
            font-size: 14px;
            padding-top: 10px;
        }

        .ml5 {
            margin-left: 5px;
        }

        .user-login-account {
            font-size: 16px;
        }

        .mi-mobile-button {
            font-weight: bold;
        }

        .alipay-agreement-link {
            margin-left: 5px;
            color: #999;
        }

        .alipay-agreement {
            width: 600px;
            height: 270px;
            padding: 10px;
            text-align: center;
        }

        .alipay-agreement-content {
            height: 230px;
            width: 600px;
            margin-bottom: 5px;
        }

        #container .order-timeout-notice {
            margin-top: 30px;
            display: none;
        }

        .login-panel .fn-mb8 {
            margin-bottom: 8px;
        }

        .login-panel .fn-mt8 {
            margin-top: 8px;
        }

        /* 新版扫码页面样式 */


        .order-area {
            position: relative;
            z-index: 10;
        }

        .cashier-center-container {
            overflow: hidden;
            position: relative;
            z-index: 1;
            width: 950px;
            min-height: 460px;
            background-color: #fff;

            border-bottom: 3px solid #b3b3b3;
        }

        .cashiser-switch-wrapper {
            width: 1800px;
        }

        .cashier-center-view {
            position: relative;
            width: 803px;
        }

        .cashier-center-view.view-pc {
            display: block;
        }

        .cashier-center-view.view-pc .loginBox {
            padding: 60px 0 20px 238px;
            width: 350px;
            margin: 0;
        }

        .loginBox .login-title-area {
            margin: 0;
            margin-bottom: 30px;
        }

        .login-title .rt-text {
            font-size: 14px;
        }

        .teForm {
            padding: 0;
        }

        .mi-form-item {
            padding: 0 0 12px 0;
        }

        .submitContainer {
            margin-top: 6px;
        }

        /* 切换按钮 */
        .view-switch {
            width: 146px;
            height: 400px;
            padding-top: 126px;
            background-color: #e6e6e6;
            cursor: pointer;

            /* 禁止选中 */
            -webkit-user-select: none;
            -khtml-user-select: none;
            -moz-user-select: none;
            user-select: none;
        }

        .view-switch.qrcode-show {
            border-left: 1px solid #d9d9d9;
            border-top-left-radius: 4px;
            border-bottom-left-radius: 4px;
        }

        .view-switch.qrcode-hide {
            border-right: 1px solid #d9d9d9;
            border-top-right-radius: 4px;
            border-bottom-right-radius: 4px;
        }

        .switch-tip {
            text-align: center;
        }

        .switch-tip-font {
            font-size: 16px;
            font-family: tahoma, arial, '\5FAE\8F6F\96C5\9ED1', '\5B8B\4F53';
        }

        .switch-tip-icon {
            position: relative;
            z-index: 10;
            display: block;
            margin-top: 4px;
            font-size: 78px;
            color: #a6a6a6;
            cursor: pointer;
        }

        .switch-tip-btn {
            display: block;
            width: 106px;
            height: 36px;
            margin: 6px auto 0;
            border: 1px solid #0fa4db;
            background-color: #00aeef;
            border-radius: 5px;

            font-size: 12px;
            font-weight: 400;
            line-height: 36px;
            text-align: center;
            color: #fff;
            text-decoration: none;
        }

        .switch-tip-btn:hover {
            color: #fff;
            text-decoration: none;
        }

        .view-switch.qrcode-hide .view-switch-content {
            height: 334px;
            padding-top: 126px;
        }

        .switch-pc-tip .switch-tip-icon {
            position: relative;
            z-index: 10;
            margin-top: 4px;
            font-size: 78px;
        }

        .switch-tip-icon-wrapper {
            position: relative;
        }

        .switch-tip-icon-wrapper:before {
            content: '';
            position: absolute;
            left: 47px;
            top: 24px;
            z-index: 0;
            width: 50px;
            height: 70px;
            background-color: #fff;
        }

        .switch-qrcode-tip .switch-tip-icon-wrapper:before {
            left: 38px;
            top: 25px;
            width: 70px;
            height: 47px;
        }

        .switch-tip-icon-img {
            position: absolute;
            left: 58px;
            top: 35px;
            z-index: 11;
        }

        .switch-qrcode-tip .switch-tip-icon-img {
            left: 48px;
            top: 39px;
        }

        .standardPwdContainer object {
            width: 348px;
            height: 38px;
        }

        #container .standardPwdContainer {
            width: 348px;
            height: 38px;
        }

        #container .standardPwdContainer a.aliedit-install {
            line-height: 38px;
        }

        #container .standardPwdContainer .ui-input {
            width: 324px;
            padding: 7px 10px;
            font-size: 14px;
            height: 20px;
            line-height: 24px;
        }

        #container .standardPwdContainer .ui-input:focus {
            color: #4D4D4D;
            border-color: #07F;
            outline: 1px solid #8CDDFF;
            *padding: 7px 3px 4px;
            *border: 2px solid #07F;
        }

        .qrcode-area {
            margin: 0 auto;
            position: relative;
        }

        /* 扫码头部信息 */
        .qrcode-integration .qrcode-header {
            display: block;
            width: auto;
            margin: 0;
            padding: 0;
            margin-top: 75px;
            margin-bottom: 16px;
        }

        .qrcode-header-money {
            font-size: 26px;
            font-weight: 700;
            color: #f60;
        }

        .qrcode-integration .qrcode-img-area {
            width: 168px;
            height: 168px;
            text-align: center;
        }

        .qrcode-img-area.qrcode-img-crash {
            height: 220px;
        }

        .qrcode-reward-wrapper {
            text-align: center;
        }

        .qrcode-reward {
            display: inline-block;
            margin: 0;
            padding: 2px 5px;
            background-color: #0188cd;
            border-radius: 0;

            font-size: 12px;
            line-height: 16px;
            color: #fff;
        }

        .qrcode-reward-question {
            font-size: 12px;
            margin-left: 5px;
            margin-right: 0;
        }

        .qrcode-integration .qrcode-loading {
            top: 70px;
            left: 60px;
        }

        .qrcode-integration .qrcode-img {
            top: 70px;
            left: 70px;
        }

        .qrcode-integration .qrcode-img-wrapper {
            position: relative;
            width: 168px;
            height: auto;
            min-height: 168px;
            margin: 0 auto;
            padding: 6px;

            border: 1px solid #d3d3d3;
            -webkit-box-shadow: 1px 1px 1px #ccc;
            box-shadow: 1px 1px 1px #ccc;
        }

        .qrcode-img-area .qrcode-busy-icon {
            padding-top: 15px;
        }

        .qrcode-img-area .qrcode-busy-text {
            margin-top: 20px;
        }

        a.mi-button-lwhite .mi-button-text {
            padding: 8px 39px 4px 36px;
        }

        .qrcode-img-area .mi-button {
            margin-top: 40px;
        }

        /* 扫码图片下方提示 */
        .qrcode-img-explain {
            padding: 10px 0 6px;
        }

        .qrcode-img-explain img {
            margin-left: 20px;
            margin-top: 5px;
        }

        .qrcode-img-explain div {
            margin-left: 10px;
        }


        .qrcode-foot {
            text-align: center;
        }

        .qrcode-downloadApp,
        .qrcode-downloadApp:hover,
        .qrcode-downloadApp:active,
        .qrcode-explain a.qrcode-downloadApp:hover {
            font-size: 12px;
            color: #a6a6a6;
            text-decoration: underline;
        }

        .area-split {
            margin-top: 156px;
            width: 10px;
            height: 300px;

            background-image: url(https://t.alipayobjects.com/images/T1PspfXixsXXXXXXXX.png);
            background-repeat: no-repeat;
        }

        .qrguide-area {
            position: absolute;
            top: 62px;
            left: 505px;
            width: 204px;
            height: 183px;
            cursor: pointer;
        }

        .qrguide-area .qrguide-area-img {
            display: block;
            position: absolute;
            bottom: 0;
            left: 0;
            z-index: -1;
        }

        .qrguide-area .qrguide-area-img.active {
            z-index: 10;
        }

        .qrguide-area .qrguide-area-img.background {
            z-index: 9;
        }

        .qrcode-notice .qrcode-notice-title {
            padding: 10px 10px 11px 63px;
        }
    </style>

</head>
<body>


<div class="topbar">
    <div class="topbar-wrap fn-clear">
        <a href="https://help.alipay.com/lab/help_detail.htm?help_id=258086" class="topbar-link-last" target="_blank"
           seed="goToHelp">常见问题</a>
        <span class="topbar-link-first">你好，欢迎使用支付宝付款！</span>
    </div>
</div>

<div id="header">
    <div class="header-container fn-clear">
        <div class="header-title">
            <div class="alipay-logo"></div>
            <span class="logo-title">我的收银台</span>
        </div>
    </div>
</div>
<div id="container">
    <div class="mi-notice mi-notice-success mi-notice-titleonly order-timeout-notice" id="J_orderPaySuccessNotice">
        <div class="mi-notice-cnt">
            <div class="mi-notice-title">
                <i class="iconfont" title="支付成功">&#xF049;</i>
                <h3>支付成功，<span class="ft-orange" id="J_countDownSecond">3</span> 秒后自动返回商户。</h3>
            </div>
        </div>
    </div>

    <div class="mi-notice mi-notice-error mi-notice-titleonly order-timeout-notice" id="J_orderDeadlineNotice">
        <div class="mi-notice-cnt">
            <div class="mi-notice-title">
                <i class="iconfont" title="交易超时">&#xF045;</i>

                <h3>抱歉，您的交易因超时已失败。</h3>

                <p class="mi-notice-explain-other">
                    您订单的最晚付款时间为： <span id="J_orderDeadline"></span>，目前已过期，交易关闭。
                </p>
            </div>
        </div>
    </div>

    </span>

    <% ScanRecord scanRecord = (ScanRecord) session.getAttribute("scanRecord");%>
    <!-- 页面主体 -->
    <div id="content" class="fn-clear">

        <div id="J_order" class="order-area" data-module="excashier/login/2015.08.01/orderDetail">
            <div id="order" data-role="order" class="order order-bow">
                <div class="orderDetail-base" data-role="J_orderDetailBase">
                    <div class="order-extand-explain fn-clear">
            <span class="fn-left explain-trigger-area order-type-navigator" style="cursor: auto"
                  data-role="J_orderTypeQuestion">

            <span>正在使用即时到账交易</span>

    <span data-role="J_questionIcon" seed="order-type-detail" style="cursor: pointer;color: #08c;">[?]</span>
            </span>
                    </div>
                    <div class="commodity-message-row">
            <span class="first long-content">
               <%= scanRecord.getBody() %>
            </span>
                        <span class="second short-content">
                                                                    收款方：一梦工作室
                            </span>
                    </div>
                    <span class="payAmount-area" id="J_basePriceArea">
                                                     <strong class=" amount-font-22 "><%= scanRecord.getTotalamount()%></strong> 元

        </span>
                </div>

                <div class="ui-tip ui-question-tip fn-hide" seed="question-tip" data-role="J_orderTypeTip">
                    <div class="ui-dialog-container">
                        <div class="ui-dialog-head-text">
                            <span>付款后资金直接进入对方账户</span>
                        </div>

                        <ul class="ui-dialog-content">
                            <li>
                                若发生退款需联系收款方协商，如付款给陌生人，请谨慎操作。
                            </li>
                        </ul>
                    </div>
                    <div class="ui-icon-dialog-arrow">
                        ↓
                    </div>
                </div>

                <a id="J_OrderExtTrigger" class="order-ext-trigger" href="#" seed="order-detail-more"
                   data-role="J_oderDetailMore">
                    订单详情
                </a>


                <div class="ui-detail fn-hide" data-role="J_orderDetailCnt" id="J-orderDetail">
                    <div class="ajax-Account od-more-cnt fn-clear">
                        <div class="first  long-content"><%= scanRecord.getBody()%>
                        </div>
                        <ul class="order-detail-container">
                            <li class="order-item">
                                <table>
                                    <tbody>
                                    <tr>
                                        <th class="sub-th">收款方：</th>
                                        <td>
                                            一梦工作室
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="sub-th">订单号：</th>
                                        <td><%= scanRecord.getOuttradeno()%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="sub-th">商品名称：</th>
                                        <td>
                                            <%= scanRecord.getBody()%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="sub-th">商品描述：</th>
                                        <td>充值-<%= scanRecord.getTotalamount()%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="sub-th">交易金额：</th>
                                        <td><%= scanRecord.getTotalamount()%>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>


                            </li>
                        </ul>
                    </div>
                    <span class="payAmount-area payAmount-area-expand">
                <strong class=" amount-font-22 "><%= scanRecord.getTotalamount()%></strong> 元
        </span>
                    <iframe src="javascript:''" class="ui-detail-iframe-fix" data-role="J_orderDetailFrameFix"></iframe>
                </div>

                <a id="J_OrderExtTrigger" class="order-ext-trigger fn-hide" href="#" seed="order-detail-more"
                   data-role="J_oderDetailShrink">
                    订单详情
                </a>
            </div>
            <input name="oid" type="hidden" value="7d48a5325f7c45dcb2cfd71d67c36a3a.20" id="J_orderId"/>
            <input name="pid" type="hidden" value="2088131838942490" id="J_partnerId"/>
            <input name="pid" type="hidden" value="18625_2020081818440256253381" id="J_outBizID"/>
            <input name="qrContextId" type="hidden" value="20200818ccmc-54-20Tce22663e1785401" id="J_qrContextId"/>
            <input name="qrPayLoopCheckUrl" type="hidden"
                   value="https://tradeexprod.alipay.com/fastpay/qrPayLoopCheck.json" id="J_qrPayLoopCheckUrl"/>
            <input name="qrDiscountText" type="hidden" value="" id="J_qrDiscountText"/>
            <input name="qrDiscountDesc" type="hidden" value="" id="J_qrDiscountDesc"/>

        </div>
        <!-- 操作区 -->
        <div class="cashier-center-container">

            <div data-module="excashier/login/2020.07.27/loginPwdMemberT" id="J_loginPwdMemberTModule"
                 class="cashiser-switch-wrapper fn-clear">

                <!-- 扫码支付页面 -->
                <div class="cashier-center-view view-qrcode fn-left" id="J_view_qr">
                    <div data-role="qrPayArea" class="qrcode-integration qrcode-area" id="J_qrPayArea">
                        <div class="qrcode-header">
                            <div class="ft-center">扫一扫付款（元）</div>
                            <div class="ft-center qrcode-header-money"><%= scanRecord.getTotalamount()%>
                            </div>
                        </div>


                        <div data-role="qrPayCrash" class="qrcode-img-area qrcode-img-crash fn-hide"
                             style="position: relative">
                            <div style="top: 78px;left: 73px;;position: absolute;z-index: 999999"><img
                                    style="width: 42px;height: 42px" src="../imgs/gz/T1Z5XfXdxmXXXXXXXX.png" alt="">
                            </div>
                            <div id="qrcode" style="position: absolute;top: 18px;left: 14px;"></div>
                            <div class="ma"><img src="../imgs/gz/ma.png" alt=""></div>
                        </div>
                        <input type="hidden" id="ma" name="#" value="<%= scanRecord.getQrcode()%>">
                        <script src="js/jquery-3.3.1.min.js"></script>
                        <script>

                            var url = $("#ma").val()
                            var status = $("#status").val();
                            console.log("支付状态" + status)
                            //初始化存放二维码的div
                            var qrcode = new QRCode(document.getElementById("qrcode"), {
                                width: 160,
                                height: 160
                            });
                            qrcode.makeCode(url);
                        </script>
                        <% while (true) {
                                String stus = (String) session.getAttribute(scanRecord.getOuttradeno());
                                if (stus == "TRADE_SUCCESS") {
                                    response.sendRedirect("zhongzhuan.jsp");
                                }
//                                Thread.sleep(5000);
//                                Thread.sleep(5000);
                            }%>
                        <div class="qrcode-img-wrapper" data-role="qrPayImgWrapper">
                            <div data-role="qrPayImg" class="qrcode-img-area">
                                <div class="ui-loading qrcode-loading" data-role="qrPayImgLoading">加载中</div>
                            </div>

                            <div class="qrcode-img-explain fn-clear">
                                <img class="fn-left" src="https://t.alipayobjects.com/images/T1bdtfXfdiXXXXXXXX.png"
                                     alt="扫一扫标识">
                                <div class="fn-left">打开手机支付宝<br>扫一扫继续付款</div>
                            </div>
                        </div>

                        <div class="qrcode-foot" data-role="qrPayFoot">
                            <div data-role="qrPayExplain" class="qrcode-explain fn-hide">
                                <a href="https://mobile.alipay.com/index.htm" class="qrcode-downloadApp"
                                   data-boxUrl="https://cmspromo.alipay.com/down/new.htm" data-role="dl-app"
                                   target="_blank" seed="NewQr_qr-pay-download">首次使用请资源手机支付宝</a>
                            </div>

                            <div data-role="qrPayScanSuccess"
                                 class="mi-notice mi-notice-success mi-notice-titleonly qrcode-notice fn-hide">
                                <div class="mi-notice-cnt">
                                    <div class="mi-notice-title qrcode-notice-title">
                                        <i class="iconfont qrcode-notice-iconfont" title="扫描成功">&#xF049;</i>
                                        <p class="mi-notice-explain-other qrcode-notice-explain ft-break">
                                            <span class="ft-orange fn-mr5" data-role="qrPayAccount"></span>已创建订单，请在手机支付宝上完成付款
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>

                    <!-- 指引区域 -->
                    <div class="qrguide-area" id="J_qrguideArea" seed="NewQr_animationClick">
                        <img src="https://t.alipayobjects.com/images/rmsweb/T13CpgXf8mXXXXXXXX.png"
                             class="qrguide-area-img active">
                        <img src="https://t.alipayobjects.com/images/rmsweb/T1ASFgXdtnXXXXXXXX.png"
                             class="qrguide-area-img background">
                    </div>


                </div>


                <!-- 点击切换区域 -->
                <div class="view-switch qrcode-show fn-left" style="pointer-events: none;" id="J_viewSwitcher"
                     unselectable="on" onselectstart="return false;" seed="NewQr_viewSwitch">

                    <div class="switch-tip switch-qrcode-tip " id="J_tip_qr">
                        <div class="switch-tip-font">&nbsp;</div>
                        <div class="switch-tip-icon-wrapper">
                            <i class="switch-tip-icon iconfont" title="显示器">&#xF02E;</i>
                            <img class="switch-tip-icon-img"
                                 src="https://t.alipayobjects.com/images/T1HHFgXXVeXXXXXXXX.png" alt="支付宝图标" width="50"
                                 height="17">
                        </div>
                        <a class="switch-tip-btn" href="javascript:void(0)">&lt;&nbsp;登录账户付款</a>
                    </div>
                </div>

                </span>
            </div>
        </div>
    </div>
</div>
</form>
</div>
</div>
</div>
<!-- 操作区 结束 -->
</div>
<!-- 页面主体 结束 -->
<script type="text/javascript" charset="utf-8"
        src='https://rds.alipay.com/ua_excashier_rds_auth.js?t=20200818'></script>
<script src="https://a.alipayobjects.com/sensor-sdk/2.0.0/index.js"></script>
<!--防止钓鱼确认-->
<input type="hidden" name="hasAntiFishingRisk" value="false"/>
<input type="hidden" name="needCheckIframe" value="true"/>
<div class="fn-hide" data-role="fishing-popup">
    <style>
        .anti-fishing {
            width: 450px;
            padding: 15px;
        }

        .anti-fishing h3 {
            font-size: 14px;
            font-weight: bold;
            margin: 8px 0;
            padding: 3px 8px 3px 0;
        }

        .anti-fishing p {
            padding: 8px 0;
        }

        .anti-fishing ul {
            padding: 0 0 0 20px;
        }

        .anti-fishing li {
            padding: 5px 0;
        }

        .anti-fishing li input {
            margin: 0 5px 0 0;
            vertical-align: middle;
        }

        span.btn {
            display: inline-block;
            line-height: 100%;
            text-align: left;
            text-decoration: none;
            vertical-align: middle;
        }

        span.btn, span.btn input, span.btn button {
            background-image: url("https://i.alipayobjects.com/e/201309/187HQtMqDP.png");
            background-repeat: repeat-x;
            border: medium none;
            cursor: pointer;
            outline: medium none;
        }

        span.btn-ok, span.btn-ok-hover, span.btn-ok-disabled {
            border: 1px solid #D74C00;
            font-family: SimSun;
            font-size: 14px;
            padding: 1px;
        }

        span.btn-ok {
            background-position: 0 0;
            margin-left: 0;
        }

        span.btn input, span.btn button {
            vertical-align: baseline;
        }

        span.btn-ok button, span.btn-ok input, span.btn-ok-hover button, span.btn-ok-hover input {
            color: #FFFFFF;
            font-weight: bold;
            height: 29px;
            line-height: 28px;
            padding: 0 16px 3px;
        }

        span.btn-ok button, span.btn-ok input {
            background-position: 0 -70px;
            font-family: SimSun;
            height: 29px;
        }

        .view-case {
            padding: 8px 0 10px 20px;
        }
    </style>
    <div class="anti-fishing">

        <p>您购买的商品为：<span class="ft-break ft-bold ft-red">充值5元-客服QQ:2371184609</span>，收款方为：<strong><font color="red">深圳雅科网络科技有限公司</font></strong>，请确认。
        </p>
        <p>建议您付款前<a href="http://bbs.taobao.com/catalog/thread/154504-251045688.htm" target="_blank">先查看防骗案例</a></p>

        <h3>是否仍继续付款？</h3>

        <ul>
            <li><label for="J_antiFishingStop"><input type="radio" name="pay" value="N" id="J_antiFishingStop"
                                                      seed="excashier-antiFishing-cancelPay"/>否，我不想继续付款了</label></li>
            <li><label for="J_antiFishingPay"><input type="radio" name="pay" value="Y" id="J_antiFishingPay"
                                                     seed="excashier-antiFishing-confirmPay"/>是，我还要继续付款，自担风险。</label>
            </li>
        </ul>
        <div id="J_antiFishingViewCase" class="fn-clear fn-hide view-case">
        <span class="btn btn-ok">
            <input type="button" tabindex="3" seed="excashier-antiFishing-viewCase" value="查看相关案例"/>
            <input type="hidden" id="J_openUrl" value="https://bbs.taobao.com/catalog/thread/154504-251045688.htm"/>
        </span>
        </div>
    </div>
</div>


<input type="hidden" name="commonAgreementUrl"
       value="https://excashier.alipay.com:443/standard/agreementDetail.phtm?payOrderId=7d48a5325f7c45dcb2cfd71d67c36a3a.20&viewModel=standard%3AcommonAgreementViewModel.vm"/>
<input type="hidden" name="memoryPayAgreementUrl"
       value="https://excashier.alipay.com:443/standard/agreementDetail.phtm?payOrderId=7d48a5325f7c45dcb2cfd71d67c36a3a.20&viewModel=standard%3AmemoryPayAgreementViewModel.vm"/>
<div style="display:none">onlineServer</div>
<script>
    seajs.use('excashier/front/2020.07.27/newCashierFront');
</script>


<div id="footer">
    <!-- FD:231:alipay/foot/copyright.vm:START -->
    <!-- FD:231:alipay/foot/copyright.vm:2604:foot/copyright.schema:支付宝copyright:START -->
    <style>
        .copyright, .copyright a, .copyright a:hover {
            color: #808080;
        }
    </style>
    <div class="copyright">
        <a href="https://fun.alipay.com/certificate/jyxkz.htm" target="_blank">ICP证：沪B2-20150087</a>
    </div>
    <div class="server" id="ServerNum">
        excashier-54-5002 &nbsp;
    </div>
    <!-- FD:231:alipay/foot/copyright.vm:2604:foot/copyright.schema:支付宝copyright:END -->
    <!-- FD:231:alipay/foot/copyright.vm:END --></div>
</div><!-- /container -->
<div id=partner><img alt=合作机构 src="https://i.alipayobjects.com/e/201303/2R3cKfrKqS.png"></div>
<div class="ui-poptip ui-poptip-white qrpay-discount-tip fn-hide" id="J_qrPayTip">
    <div class="ui-poptip-shadow">
        <div class="ui-poptip-container qrpay-discount-container">
            <div class="ui-poptip-arrow ui-poptip-arrow-10">
                <em></em>
                <span></span>
            </div>
            <div class="ui-poptip-content">
                <p>使用扫码支付，不可与</p>
                <p>支付宝其他优惠同时使用。</p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
