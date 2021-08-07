$("#loginBtn").click(function(){
	var user = new Object();
	user.username = $.trim($("#username").val());
	user.userpassword = $.trim($("#userpassword").val());
	user.isStart = 1;

	if(user.username == "" || user.username == null){
		 $("#username").focus();
		 $("#formtip").css("color","red");
		 $("#formtip").html("对不起，登录账号不能为空。");
	}else if(user.userpassword == "" || user.userpassword == null){
		$("#userpassword").focus();
		$("#formtip").css("color","red");
		$("#formtip").html("对不起，登录密码不能为空。");
	}else{
		$("#formtip").html("");

		$.ajax({
			url: '/login.do',
			type: 'POST',
			data:{"username":user.username,"userpassword":user.userpassword},
			dataType: 'text',
			timeout: 1000,
			cache:false,
			error: function(){
				$("#formtip").css("color","red");
				$("#formtip").html("服务器无响应！请重试。");
			},
			success: function(result){
                    if(result != "" && "success" == result){
                        window.location.href='/main.html';
                    }else if("failed" == result){
                        $("#formtip").css("color","red");
                        $("#formtip").html("用户名或密码错误!请重试。");
                        $("#username").val('');
                        $("#userpassword").val('');
                    }
				}
			});
	}
});

