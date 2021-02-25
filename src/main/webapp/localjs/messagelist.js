$('.delmessage').click(function(e){
	//delete
	var d = $(this);
	var d_createdBy = d.attr('createdBy');
	var d_id = d.attr('id');
	if(confirm("您确定要删除用户:【"+d_createdBy+"】的该条留言吗？")){
		//delete
		$.post("/backend/delmessage.html",{'delId':d_id},function(result){
			if("success" == result){
				alert("删除成功！");
				window.location.href="/message/messagelist.html";
			}else{
				alert("删除失败！");
			}
		},'html');
	}
});

$('.replymessage').click(function(e){
	$("#reply_formtip").html('');
	var r_id = $(this).attr('id');
	$.ajax({
		url: '/message/getmessage.html',
		type: 'POST',
		data:{id:r_id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("error");
		},
		success: function(result){
			if("failed" == result){
				alert("操作超时！");
			}else if("nodata" == result){
				alert("没有数据！");
			}else{
				m = eval('(' + result + ')');
				$("#message_id").val(m.leaveMessage.id);
				$("#message_createdBy").html(m.leaveMessage.createdBy);
				$("#message_messageContent").html(m.leaveMessage.messageContent);
				$("#message_createTime").html(m.leaveMessage.createTime);
				$("#replylist").html('');
				for(var i = 0; i < m.replyList.length; i++){
					$("#replylist").append("<li><lable><b>回复内容：</b></lable>"+m.replyList[i].replyContent+" ("+m.replyList[i].createdBy + " " + m.replyList[i].createTime +")</li>");
				}
				
				e.preventDefault();
				$('#replyMessageDiv').modal('show');
			}
		}
	});
});

$('.viewmessage').click(function(e){
	var r_id = $(this).attr('id');
	$.ajax({
		url: '/message/getmessage.html',
		type: 'POST',
		data:{id:r_id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("error");
		},
		success: function(result){
			if("failed" == result){
				alert("操作超时！");
			}else if("nodata" == result){
				alert("没有数据！");
			}else{
				m = eval('(' + result + ')');
				$("#viewmessage_createdBy").html(m.leaveMessage.createdBy);
				$("#viewmessage_messageContent").html(m.leaveMessage.messageContent);
				$("#viewmessage_createTime").html(m.leaveMessage.createTime);
				$("#viewreplylist").html('');
				for(var i = 0; i < m.replyList.length; i++){
					$("#viewreplylist").append("<li><lable><b>回复内容：</b></lable>"+m.replyList[i].replyContent+" ("+m.replyList[i].createdBy + " " + m.replyList[i].createTime +")</li>");
				}
				e.preventDefault();
				$('#viewreplyMessageDiv').modal('show');
			}
		}
		});
});

function replyMessageFunction(){
	$("#reply_formtip").html('');
	var result = true;
	if($.trim($("#r_content").val()) == "" || $("#r_content").val() == null){
		$("#reply_formtip").css("color","red");
		$("#reply_formtip").append("<li>对不起，回复内容不能为空。</li>");
		result = false;
	}
	return result;
}
