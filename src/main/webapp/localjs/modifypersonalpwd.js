$("#saveloginpwd").click(function(){
		var tip = $("#modifyloginpwdtip");
		tip.html("");
		var oldpwd = $("#oldloginpwd").val();
		var newpwd = $("#newloginpwd").val();
		var aginpwd = $("#againloginpwd").val();
		if("" == oldpwd){
			tip.css("color","red");
			tip.html("对不起，请输入原密码，谢谢。");
			$("#oldloginpwd").focus();
		}else if("" == newpwd){
			tip.css("color","red");
			tip.html("对不起，请输入新密码，谢谢。");
			$("#newloginpwd").focus();
		}else if("" == aginpwd){
			tip.css("color","red");
			tip.html("对不起，请再次输入新密码，谢谢。");
			$("#againloginpwd").focus();
		}else if(newpwd.length < 6){
			tip.css("color","red");
			tip.html("对不起，密码长度不能小于6位，谢谢。");
			$("#newloginpwd").focus();
		}else if(newpwd != aginpwd){
			tip.css("color","red");
			tip.html("对不起，您两次输入的密码不一致，请重新输入，谢谢。");
			$("#againloginpwd").focus();
		}else{
			//userJson
			user = new Object();
			user.password = oldpwd;
			user.password2 = newpwd;
			$.ajax({
				url: '/backend/modifyPwd.html',
				type: 'POST',
				data:{userJson:JSON.stringify(user)},
				dataType: 'html',
				timeout: 1000,
				error: function(){
					alert("修改密码失败！请重试。");
				},
				success: function(result){
					if(result != "" && "success" == result){
						tip.css("color","green");
						tip.html("修改密码成功 ，下次登录记得使用新密码哦。^_^");
					}else if("failed" == result){
						tip.css("color","red");
						tip.html("修改密码失败！请重试。");
					}else if("oldpwdwrong" == result){
						tip.css("color","red");
						tip.html("原密码不正确！请重试。");
					}else if("nodata" == result){
						tip.css("color","red");
						tip.html("对不起，没有任何数据需要处理！请重试。");
					}
				}
				});
		}
	});

$("#savesecondpwd").click(function(){
	var tip = $("#modifysecondpwdtip");
	tip.html("");
	var oldpwd = $("#oldsecondpwd").val();
	var newpwd = $("#newsecondpwd").val();
	var aginpwd = $("#againsecondpwd").val();
	if("" == oldpwd){
		tip.css("color","red");
		tip.html("对不起，请输入原密码，谢谢。");
		$("#oldsecondpwd").focus();
	}else if("" == newpwd){
		tip.css("color","red");
		tip.html("对不起，请输入新密码，谢谢。");
		$("#newsecondpwd").focus();
	}else if("" == aginpwd){
		tip.css("color","red");
		tip.html("对不起，请再次输入新密码，谢谢。");
		$("#againsecondpwd").focus();
	}else if(newpwd.length < 6){
		tip.css("color","red");
		tip.html("对不起，密码长度不能小于6位，谢谢。");
		$("#newloginpwd").focus();
	}else if(newpwd != aginpwd){
		tip.css("color","red");
		tip.html("对不起，您两次输入的密码不一致，请重新输入，谢谢。");
		$("#againsecondpwd").focus();
	}else{
		//userJson
		user = new Object();
		user.password = oldpwd;
		user.password2 = newpwd;
		$.ajax({
			url: '/member/savesecondpwd.html',
			type: 'POST',
			data:{userJson:JSON.stringify(user)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("修改密码失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					tip.css("color","green");
					tip.html("修改密码成功 ，下次登录记得使用新密码哦。^_^");
				}else if("failed" == result){
					tip.css("color","red");
					tip.html("修改密码失败！请重试。");
				}else if("oldpwdwrong" == result){
					tip.css("color","red");
					tip.html("原密码不正确！请重试。");
				}else if("nodata" == result){
					tip.css("color","red");
					tip.html("对不起，没有任何数据需要处理！请重试。");
				}
			}
		});
	}
});