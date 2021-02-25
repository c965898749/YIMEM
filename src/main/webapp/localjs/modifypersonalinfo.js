function checkEmail(str){
	var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
	if(str == null || str == "" || reg.test(str))
		return true;
	else
		return false;
}

$('.modifypersonalinfocancel').click(function(e){
	$("#m_idPic").html('');
	$("#m_bankPic").html('');
	$("#modify_formtip").html('');
});

$("#m_uploadbtnID").click(function(){
	TajaxFileUpload($("#m_id").val(),'m_fileInputID','m_uploadbtnID','m_idPic','m_fileInputIDPath');
});
$("#m_uploadbtnID_h").click(function(){
	TajaxFileUpload($("#m_id").val(),'m_fileInputID','m_uploadbtnID_h','m_idPic','m_fileInputIDPath');
});

$("#m_uploadbtnBank").click(function(){
	TajaxFileUpload($("#m_id").val(),'m_fileInputBank','m_uploadbtnBank','m_bankPic','m_fileInputBankPath');
});
$("#m_uploadbtnBank_h").click(function(){
	TajaxFileUpload($("#m_id").val(),'m_fileInputBank','m_uploadbtnBank_h','m_bankPic','m_fileInputBankPath');
});

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

$("#m_email").blur(function(){
	var flag = checkEmail($("#m_email").val());
	if(flag == false){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").html("<li>email格式不正确</li>");
		$("#modify_formtip").attr("email","1");
	}else{
		$("#modify_formtip").html("");
		$("#modify_formtip").attr("email","0");
	}
		
});
function modifyPersonalInfoFunc(){
	$("#modify_formtip").html("");
	var result = true;
	
	if($.trim($("#m_username").val()) == "" || $("#m_username").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，真实姓名不能为空。</li>");
		result = false;
	}
	if($("#m_cardtype").val() == ""){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，证件类型不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_idcard").val()) == "" || $("#m_idcard").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，证件号码不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_mobile").val()) == "" || $("#m_mobile").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，联系电话不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_bankname").val()) == "" || $("#m_bankname").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，开户行不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_bankaccount").val()) == "" || $("#m_bankaccount").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，开户卡号不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_accountholder").val()) == "" || $("#m_accountholder").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，开户人不能为空。</li>");
		result = false;
	}
	if($.trim($("#m_email").val()) != "" && $("#m_email").val() != null && $("#modify_formtip").attr("email") == "1"){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>email格式不正确</li>");
		result = false;
	}
	if(result == true) alert("修改成功 ^_^");
	return result;
}