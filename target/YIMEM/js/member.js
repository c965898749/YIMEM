
/*
**************************
(C)2010-2015 phpMyWind.com
update: 2012-10-16 14:31:32
person: Feng
**************************
*/


$(function(){
	$(".input").focus(function(){
		$(this).attr("class","inputon");
	}).blur(function(){
		$(this).attr("class","input");
	});

	$(".sub").mouseover(function(){
		$(this).attr("class","subon");
	}).mouseout(function(){
		$(this).attr("class","sub");
	}).mousedown(function(){
		$(this).attr("class","subdown");
	});

	$("#username").focus();


	$(".class_input").focus(function(){
		$(this).attr("class","class_input_on");
	}).blur(function(){
		$(this).attr("class","class_input");
	});

	$(".class_areatext").focus(function(){
		$(this).attr("class","class_areatext_on");
	}).blur(function(){
		$(this).attr("class","class_areatext");
	});

});

//用户注册验证
function CheckReg() {
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	var user_name = $("#username").val();
	if (user_name == "") {
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if (user_name.length < 5 || user_name.length > 32) {
		alert("用户名长度为5~32位字符！");
		$("#username").focus();
		return false;
	}

	if (user_name.indexOf("\'") != -1 || user_name.indexOf("\"") != -1 || user_name.indexOf("\\") != -1 || user_name.indexOf("/") != -1 || user_name.indexOf(";") != -1 || user_name.indexOf("%") != -1 || user_name.indexOf("#") != -1 || user_name.indexOf("(") != -1 || user_name.indexOf(")") != -1 || user_name.indexOf("*") != -1 || user_name.indexOf("&") != -1 || user_name.indexOf("|") != -1 || user_name.indexOf("<") != -1 || user_name.indexOf(">") != -1 || user_name.indexOf(",") != -1 || user_name.indexOf("$") != -1 || user_name.indexOf("^") != -1 || user_name.indexOf("{") != -1 || user_name.indexOf("}") != -1 || user_name.indexOf("[") != -1 || user_name.indexOf("]") != -1)
	{
		alert("用户名不能包含特殊字符！");
		$("#username").focus();
		return false;
	}

	var ckupwd = /^[0-9a-zA-Z_-]+$/;
	if ($("#userpassword").val() == "") {
		alert("密码不能为空！");
		$("#userpassword").focus();
		return false;
	}
	else if ($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16) {
		alert("密码长度为6~16位字符！");
		$("#userpassword").focus();
		return false;
	}
	else if (!ckupwd.test($("#userpassword").val())) {
		alert("密码请使用[数字/字母/中划线/下划线]！");
		$("#userpassword").focus();
		return false;
	}


	if ($("#userpassword2").val() == "") {
		alert("确认密码不能为空！");
		$("#userpassword2").focus();
		return false;
	}
	else if ($("#userpassword").val() != $("#userpassword2").val()) {
		alert("两次输入的密码不相同！");
		$("#userpassword2").focus();
		return false;
	}

	if ($("#contactqq").val() == "") {
		alert("联系QQ不能为空！");
		$("#contactqq").focus();
		return false;
	}
}

function CheckReg_m() {
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	var user_name = $("#username").val();
	if (user_name == "") {
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if ( user_name.length < 5 || user_name.length > 32) {
		alert("用户名长度为5~32位字符！");
		$("#username").focus();
		return false;
	}

	if (user_name.indexOf("\'") != -1 || user_name.indexOf("\"") != -1 || user_name.indexOf("\\") != -1 || user_name.indexOf("/") != -1 || user_name.indexOf(";") != -1 || user_name.indexOf("%") != -1 || user_name.indexOf("#") != -1 || user_name.indexOf("(") != -1 || user_name.indexOf(")") != -1 || user_name.indexOf("*") != -1 || user_name.indexOf("&") != -1 || user_name.indexOf("|") != -1 || user_name.indexOf("<") != -1 || user_name.indexOf(">") != -1 || user_name.indexOf(",") != -1 || user_name.indexOf("$") != -1 || user_name.indexOf("^") != -1 || user_name.indexOf("{") != -1 || user_name.indexOf("}") != -1 || user_name.indexOf("[") != -1 || user_name.indexOf("]") != -1)
	{
		alert("用户名不能包含特殊字符！");
		$("#username").focus();
		return false;
	}

	var ckupwd = /^[0-9a-zA-Z_-]+$/;
	if ($("#userpassword").val() == "") {
		alert("密码不能为空！");
		$("#userpassword").focus();
		return false;
	}
	else if ($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16) {
		alert("密码长度为6~16位字符！");
		$("#userpassword").focus();
		return false;
	}
	else if (!ckupwd.test($("#userpassword").val())) {
		alert("密码请使用[数字/字母/中划线/下划线]！");
		$("#userpassword").focus();
		return false;
	}


	if ($("#userpassword2").val() == "") {
		alert("确认密码不能为空！");
		$("#userpassword2").focus();
		return false;
	}
	else if ($("#userpassword").val() != $("#userpassword2").val()) {
		alert("两次输入的密码不相同！");
		$("#userpassword2").focus();
		return false;
	}

	if ($("#contactqq_m").val() == "") {
		alert("联系QQ不能为空！");
		$("#contactqq_m").focus();
		return false;
	}
}

//用户登陆验证
function CheckLog()
{
	var user_name = $("#username").val();
	if( user_name == "")
	{
		alert("请输入用户名！");
		$("#username").focus();
		return false;
	}

	if (user_name.indexOf("\'") != -1 || user_name.indexOf("\"") != -1 || user_name.indexOf("\\") != -1 || user_name.indexOf("/") != -1 || user_name.indexOf(";") != -1 || user_name.indexOf("%") != -1 || user_name.indexOf("#") != -1 || user_name.indexOf("(") != -1 || user_name.indexOf(")") != -1 || user_name.indexOf("*") != -1 || user_name.indexOf("&") != -1 || user_name.indexOf("|") != -1 || user_name.indexOf("<") != -1 || user_name.indexOf(">") != -1 || user_name.indexOf(",") != -1 || user_name.indexOf("$") != -1 || user_name.indexOf("^") != -1 || user_name.indexOf("{") != -1 || user_name.indexOf("}") != -1 || user_name.indexOf("[") != -1 || user_name.indexOf("]") != -1)
	{
		alert("用户名不能包含特殊字符！");
		$("#username").focus();
		return false;
	}

	if($("#userpassword").val() == "")
	{
		alert("请输入密码！");
		$("#userpassword").focus();
		return false;
	}
}

function CheckLog_m()
{
	var user_name = $("#username").val();
	if($("#username").val() == "")
	{
		alert("请输入用户名！");
		$("#username").focus();
		return false;
	}

	if (user_name.indexOf("\'") != -1 || user_name.indexOf("\"") != -1 || user_name.indexOf("\\") != -1 || user_name.indexOf("/") != -1 || user_name.indexOf(";") != -1 || user_name.indexOf("%") != -1 || user_name.indexOf("#") != -1 || user_name.indexOf("(") != -1 || user_name.indexOf(")") != -1 || user_name.indexOf("*") != -1 || user_name.indexOf("&") != -1 || user_name.indexOf("|") != -1 || user_name.indexOf("<") != -1 || user_name.indexOf(">") != -1 || user_name.indexOf(",") != -1 || user_name.indexOf("$") != -1 || user_name.indexOf("^") != -1 || user_name.indexOf("{") != -1 || user_name.indexOf("}") != -1 || user_name.indexOf("[") != -1 || user_name.indexOf("]") != -1)
	{
		alert("用户名不能包含特殊字符！");
		$("#username").focus();
		return false;
	}

	if($("#userpassword").val() == "")
	{
		alert("请输入密码！");
		$("#userpassword").focus();
		return false;
	}
}

//修改资料验证
function CheckEditInfo() {
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	if ($("#username").val() == "") {
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if ($("#username").val().length < 5 || $("#username").val().length > 32) {
		alert("用户名长度为5~32位字符！");
		$("#username").focus();
		return false;
	}
/*
	if ($("#phone").val() == "") {
		alert("手机号不能为空");
		$("#phone").focus();
		return false;
	}
	else if (!/^1\d{10}$/.test($("#phone").val())) {
		alert("请使用正确的手机号！");
		$("#phone").focus();
		return false;
	}
*/
	if ($("#phone").val() != "")
	{
		if (!/^1\d{10}$/.test($("#phone").val()))
		{
		alert("请使用正确的手机号！");
		$("#phone").focus();
		return false;
		}
	}

	var chemail = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if ($("#email").val() == "") {
		alert("邮箱不能为空");
		$("#email").focus();
		return false;
	}
	else if (!chemail.test($("#email").val())) {
		alert("请使用正确的邮箱！");
		$("#email").focus();
		return false;
	}

	if ($("#select").val() == "") {
		alert("请选择收款方式！");
		$("#select").focus();
		return false;
	}

	if ($("#shoukuanaccount").val() == "") {
		alert("账号不能为空！");
		$("#shoukuanaccount").focus();
		return false;
	}
}

function CheckEditInfo_m()
{
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	if ($("#username").val() == "") {
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if ($("#username").val().length < 5 || $("#username").val().length > 32) {
		alert("用户名长度为5~32位字符！");
		$("#username").focus();
		return false;
	}
/*
	if ($("#phone").val() == "") {
		alert("手机号不能为空");
		$("#phone").focus();
		return false;
	}
	else if (!/^1\d{10}$/.test($("#phone").val())) {
		alert("请使用正确的手机号！");
		$("#phone").focus();
		return false;
	}
*/
	if ($("#phone_m").val() != "")
	{
		if (!/^1\d{10}$/.test($("#phone_m").val()))
		{
		alert("请使用正确的手机号！");
		$("#phone_m").focus();
		return false;
		}
	}

	var chemail = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if ($("#email_m").val() == "") {
		alert("邮箱不能为空");
		$("#email_m").focus();
		return false;
	}
	else if (!chemail.test($("#email_m").val())) {
		alert("请使用正确的邮箱！");
		$("#email_m").focus();
		return false;
	}

	if ($("#m_select").val() == "") {
		alert("请选择收款方式！");
		$("#m_select").focus();
		return false;
	}

	if ($("#shoukuanaccount_m").val() == "") {
		alert("账号不能为空！");
		$("#shoukuanaccount_m").focus();
		return false;
	}
}

function CheckFind()
{
	if($("#username").val() == "")
	{
		alert("请输入用户名！");
		$("#username").focus();
		return false;
	}

	if($("#validate").val() == "")
	{
		alert("请输入验证码！");
		$("#validate").focus();
		return false;
	}
}

function CheckFindQues()
{
	if($("#question").val() == "-1")
	{
		alert("请选择验证问题！");
		$("#question").focus();
		return false;
	}
	if($("#answer").val() == "")
	{
		alert("请输入问题答案！");
		$("#answer").focus();
		return false;
	}
}

function CheckFindMail()
{
	var ckmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	if($("#email").val() == "")
	{
		alert("请输入E-mail！");
		$("#email").focus();
		return false;
	}
	else if(!ckmail.test($("#email").val()))
	{
		alert("E-mail格式不正确！");
		$("#email").focus();
		return false;
	}
}

function CheckNewPwd()
{
	var ckupwd = /^[0-9a-zA-Z_=]+$/;
	if($("#userpassword").val() == "")
	{
		alert("密码不能为空！");
		$("#userpassword").focus();
		return false;
	}
	else if($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16)
	{
		alert("密码长度为6~16位字符！");
		$("#userpassword").focus();
		return false;
	}
	else if(!ckupwd.test($("#userpassword").val()))
	{
		alert("请使用[数字/字母/中划线/下划线]！");
		$("#userpassword").focus();
		return false;
	}


	if($("#userpassword2").val() == "")
	{
		alert("确认密码不能为空！");
		$("#userpassword2").focus();
		return false;
	}
	else if($("#userpassword").val() != $("#userpassword2").val())
	{
		alert("两次输入的密码不相同！");
		$("#userpassword2").focus();
		return false;
	}
}

function cfm_upmember()
{
	if($("#userpassword").val() != "")
	{
		var ckupwd = /^[0-9a-zA-Z_-]+$/;
		if($("#userpassword").val() == "")
		{
			alert("密码不能为空！");
			$("#userpassword").focus();
			return false;
		}
		else if($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16)
		{
			alert("密码长度为6~16位字符！");
			$("#userpassword").focus();
			return false;
		}
		else if(!ckupwd.test($("#userpassword").val()))
		{
			alert("请使用[数字/字母/中划线/下划线]！");
			$("#userpassword").focus();
			return false;
		}

		if($("#userpassword2").val() == "")
		{
			alert("确认密码不能为空！");
			$("#userpassword2").focus();
			return false;
		}
		else if($("#userpassword").val() != $("#userpassword2").val())
		{
			alert("两次输入的密码不相同！");
			$("#userpassword2").focus();
			return false;
		}

		var ckmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
		if($("#email").val() == "")
		{
			alert("E-mail不能为空！");
			$("#email").focus();
			return false;
		}
		else if(!ckmail.test($("#email").val()))
		{
			alert("E-mail格式不正确！");
			$("#email").focus();
			return false;
		}
	}
	else
	{
		var ckmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
		if($("#email").val() == "")
		{
			alert("E-mail不能为空！");
			$("#email").focus();
			return false;
		}
		else if(!ckmail.test($("#email").val()))
		{
			alert("E-mail格式不正确！");
			$("#email").focus();
			return false;
		}
	}
}



/*
 * 级联获取城市
 *
 * @access   public
 * @val      string  选择的省枚举值
 * @input    string  返回的select
 * @return   string  返回的option
 */

function SelProv(val,input)
{
	$("#"+input+"_country").html("<option>--</option>");

	$.ajax({
		url : "?a=getarea&datagroup=area&level=1&areaval="+val,
		type:'get',
		dataType:'html',
		success:function(data){
			$("#"+input+"_city").html(data);
		}
	});
}


/*
 * 级联选择区县
 *
 * @access   public
 * @val      string  选择的市枚举值
 * @input    string  返回的select
 * @return   string  返回的option
 */

function SelCity(val,input)
{
	$.ajax({
		url : "?a=getarea&datagroup=area&level=2&areaval="+val,
		type:'get',
		dataType:'html',
		success:function(data){
			$("#"+input+"_country").html(data);
		}
	});
}


//选择所有
function CheckAll(value)
{
	$("input[type='checkbox'][name^='checkid']").attr("checked",value);
}


//删除选中提示
function ConfDelAll(i)
{
	var tips = Array();
	tips[0] = "确定要删除选中的信息吗？";
	tips[1] = "系统会自动删除类别下所有子类别以及信息，确定删除吗？";
	tips[2] = "系统会自动删除类别下所有子类别，确定删除吗？";

	if($("input[type='checkbox'][name!='checkid'][name^='checkid']:checked").size() > 0)
	{
		if(confirm(tips[i])) return true;
		else return false;
	}
	else
	{
		alert('没有任何选中信息！');
		return false;
	}
}

//删除所有(不包含子分类)
function DelAllNone(url)
{
	$("#form").attr("action", url).submit();
}


//绑定账号
function cfm_binding()
{
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	if($("#username").val() == "")
	{
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if($("#username").val().length < 6 || $("#username").val().length > 16)
	{
		alert("用户名长度为6~16位字符！");
		$("#username").focus();
		return false;
	}
	else if(!ckuname.test($("#username").val()))
	{
		alert("请使用[数字/字母/中划线/下划线/@.]！");
		$("#username").focus();
		return false;
	}

	var ckupwd = /^[0-9a-zA-Z_-]+$/;
	if($("#userpassword").val() == "")
	{
		alert("密码不能为空！");
		$("#userpassword").focus();
		return false;
	}
	else if($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16)
	{
		alert("密码长度为6~16位字符！");
		$("#userpassword").focus();
		return false;
	}
	else if(!ckupwd.test($("#userpassword").val()))
	{
		alert("请使用[数字/字母/中划线/下划线]！");
		$("#userpassword").focus();
		return false;
	}
}


function cfm_perfect()
{
	var ckuname = /^[0-9a-zA-Z_@\.-]+$/;
	if($("#username").val() == "")
	{
		alert("用户名不能为空！");
		$("#username").focus();
		return false;
	}
	else if($("#username").val().length < 6 || $("#username").val().length > 16)
	{
		alert("用户名长度为6~16位字符！");
		$("#username").focus();
		return false;
	}
	else if(!ckuname.test($("#username").val()))
	{
		alert("请使用[数字/字母/中划线/下划线/@.]！");
		$("#username").focus();
		return false;
	}



	var ckupwd = /^[0-9a-zA-Z_-]+$/;
	if($("#userpassword").val() == "")
	{
		alert("密码不能为空！");
		$("#userpassword").focus();
		return false;
	}
	else if($("#userpassword").val().length < 6 || $("#userpassword").val().length > 16)
	{
		alert("密码长度为6~16位字符！");
		$("#userpassword").focus();
		return false;
	}
	else if(!ckupwd.test($("#userpassword").val()))
	{
		alert("请使用[数字/字母/中划线/下划线]！");
		$("#userpassword").focus();
		return false;
	}


	if($("#userpassword2").val() == "")
	{
		alert("确认密码不能为空！");
		$("#userpassword2").focus();
		return false;
	}
	else if($("#userpassword").val() != $("#userpassword2").val())
	{
		alert("两次输入的密码不相同！");
		$("#userpassword2").focus();
		return false;
	}



	var ckmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	if($("#email").val() == "")
	{
		alert("E-mail不能为空！");
		$("#email").focus();
		return false;
	}
	else if(!ckmail.test($("#email").val()))
	{
		alert("E-mail格式不正确！");
		$("#email").focus();
		return false;
	}
}

