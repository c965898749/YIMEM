function checkEmail(str){
	var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	if(str == null || str == "" || reg.test(str))
		return true;
	else
		return false;
}

$(".agreerule").click(function(){
	if($(".agreerule").attr("checked") == 'checked')
		$(".nextbtn").removeAttr("disabled");
	else
		$(".nextbtn").attr("disabled","disabled");
});

$(".rule").click(function(){
	window.open("/member/registrule.html");
});


$(".backbtn").click(function(){
	window.location.href="/member/registrule.html";
});

$("#a_cardType").change(function(){
	$("#a_cardTypeName").val($("#a_cardType").find("option:selected").text()) ;
});

$("#a_uploadbtnID").click(function(){
	TajaxFileUpload('0','a_fileInputID','a_uploadbtnID','a_idPic','a_fileInputIDPath');
});

$("#a_uploadbtnBank").click(function(){
	TajaxFileUpload('0','a_fileInputBank','a_uploadbtnBank','a_bankPic','a_fileInputBankPath');
});

$("#a_loginCode").blur(function(){
	var alc = $.trim($("#a_loginCode").val());
	if(alc != ""){
		$.post("/backend/logincodeisexit.html",{'loginCode':alc,'id':'-1'},function(result){
			if(result == "repeat"){
				$("#add_formtip").css("color","red");
				$("#add_formtip").html("<li>对不起，该用户名已存在。</li>");
				$("#add_formtip").attr("key","1");;
				result = false;
			}else if(result == "failed"){
				alert("操作超时!");
			}else if(result == "only"){
				$("#add_formtip").css("color","green");
				$("#add_formtip").html("<li>该用户名可以正常使用。</li>");
				$("#add_formtip").attr("key","0");
			}
		},'html');
	}
});

$("#a_email").blur(function(){
	var flag = checkEmail($("#a_email").val());
	if(flag == false){
		$("#add_formtip").css("color","red");
		$("#add_formtip").html("<li>email格式不正确</li>");
		$("#add_formtip").attr("email","1");
	}else{
		$("#add_formtip").html("");
		$("#add_formtip").attr("email","0");
	}
});

function delpic(id,closeSpan,uploadBtn,obj,picpath,picText,fileinputid){
	//delete
	$.post("/backend/delpic.html",{'id':id,'picpath':picpath},function(result){
		if("success" == result){
			alert("删除成功！");
			$('#'+picText).val('');
   			$("#uniform-"+fileinputid+" span:first").html('无文件');
			document.getElementById(closeSpan).removeChild(obj.parentElement);$('#'+uploadBtn).show();
		}else
			alert("删除失败！");
	},'html');
	
}

function TajaxFileUpload(flag,t1,t2,t3,t4)
{   
	if($("#"+t1+"").val() == '' || $("#"+t1+"").val() == null){
		alert("请选择上传文件！");
	}else{
		$.ajaxFileUpload
	    ({ 
	           url:'/backend/upload.html', //处理上传文件的服务端
	           secureuri:false,
	           fileElementId:t1,
	           dataType: 'json',
	           success: function(data) { 
	        	   data = data.replace(/(^\s*)|(\s*$)/g, "");
	        	   if(data == "1"){
	        		   alert("上传图片大小不得超过50K！");
	        		   $("#uniform-"+t1+" span:first").html('无文件');
	        		   $("input[name='"+t1+"']").change(function(){
	        			   var fn = $("input[name='"+t1+"']").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-"+t1+" span:first").html(fn);
	        		   });
	        	   }else if(data == "2"){
	        		   alert("上传图片格式不正确！");
	        		   $("#uniform-"+t1+" span:first").html('无文件');
	        		   $("input[name='"+t1+"']").change(function(){
	        			   var fn = $("input[name='"+t1+"']").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-"+t1+" span:first").html(fn);
	        		   });
	        	   } else{
	        		   $("#"+t3+"").append("<p><span onclick=\"delpic('"+flag+"','"+t3+"','"+t2+"',this,'"+data+"','"+t4+"','"+t1+"');\">x</span><img src=\""+data+"?m="+Math.random()+"\" /></p>");
	        		   $("#"+t2+"").hide();
	        		   $("#"+t4+"").val(data);
	        		   $("input[name='"+t1+"']").change(function(){
	        			   var fn = $("input[name='"+t1+"']").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-"+t1+" span:first").html(fn);
	        		   });
	        	   }
	           },  
	           error: function() {  
	              alert("上传失败！");
	           } 
	        });
	}
}

function registMemberFunc(){
	$("#add_formtip").html("");
	var result = true;
	if($.trim($("#a_loginCode").val()) == "" || $("#a_loginCode").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，用户名不能为空。</li>");
		result = false;
	}else{
		if($("#add_formtip").attr("key") == "1"){
			$("#add_formtip").append("<li>对不起，该用户名已存在。</li>");
			result = false;
		}
	}
	if($.trim($("#a_userName").val()) == "" || $("#a_userName").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，真实姓名不能为空。</li>");
		result = false;
	}
	if($("#a_cardType").val() == ""){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，证件类型不能为空。</li>");
		result = false;
	}
	if($.trim($("#a_idCard").val()) == "" || $("#a_idCard").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，证件号码不能为空。</li>");
		result = false;
	}else{
		if($("#a_idCard").val().length < 6){
			$("#add_formtip").css("color","red");
			$("#add_formtip").append("<li>对不起，证件号码长度必须超过6位。</li>");
			result = false;
		}
	}
	if($.trim($("#a_mobile").val()) == "" || $("#a_mobile").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，联系电话不能为空。</li>");
		result = false;
	}
	if($.trim($("#a_bankName").val()) == "" || $("#a_bankName").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，开户行不能为空。</li>");
		result = false;
	}
	if($.trim($("#a_bankAccount").val()) == "" || $("#a_bankAccount").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，开户卡号不能为空。</li>");
		result = false;
	}
	if($.trim($("#a_accountHolder").val()) == "" || $("#a_accountHolder").val() == null){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>对不起，开户人不能为空。</li>");
		result = false;
	}
	if($.trim($("#a_email").val()) != "" && $("#a_email").val() != null && $("#add_formtip").attr("email") == "1"){
		$("#add_formtip").css("color","red");
		$("#add_formtip").append("<li>email格式不正确</li>");
		result = false;
	}
	if(result == true) alert("添加成功 ^_^");
	return result;
	
}