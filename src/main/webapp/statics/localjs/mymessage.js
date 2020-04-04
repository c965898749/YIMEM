function addMsg(){
	if($.trim($("#messageContent").val()) == "" || $("#messageContent").val() == null){
		$("#messageContent").focus();
		$("#tip").css("color","red");
		$("#tip").html("对不起，留言内容不能为空。");
		return false;
	}else{
		return true;
	}
}

$(".msgReply").each(function(){
	var reply = $(this);
	var id = reply.attr("mid");
	reply.html("<img src=\"/statics/img/big_load.gif\" width=\"32px\"/>");
	
	$.ajax({
		url: '/message/reply.html',
		type: 'POST',
		data:{id:id},
		dataType: 'html',
		timeout: 5000,
		error: function(){
			reply.html("未回复");
		},
		success: function(result){
			if(result != "" && result != "[]"){
				var jsonObj = eval("("+result+")");
				
				var replyStr = "答复：<br/>";
				for(var i=0;i<jsonObj.length;i++){
					replyStr = replyStr + "<div>"+jsonObj[i].replyContent+" <span>(客服人员："+jsonObj[i].createdBy+")</span></div>";
				}
				reply.html(replyStr);
			}else if("failed" == result){
				reply.html("未回复");
			}else if("nodata" == result){
				reply.html("未回复");
			}else if("[]" == result){
				reply.html("未回复");
			}
		}
		});
	
});