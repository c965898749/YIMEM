//rich text editor
$('#afficheContent').cleditor();

$(".addAffiche").click(function(e){
	e.preventDefault();
	$("#afficheCode").val("");
	$("#afficheTitle").val("");
	$("#startTime").val("");
	$("#endTime").val("");
	$("#afficheContent").val("");
	$("#add_formtip").html("");
	$('#addAfficheDiv').modal('show');
});

//js日期比较（yyyy-MM-dd）
function comtime(s,e){
	var arr1 = s.split("-");
	var starttime = new Date(arr1[0],arr1[1],arr1[2]);
	var starttimes = starttime.getTime();
	var arr2 = e.split("-");
	var endtime = new Date(arr2[0],arr2[1],arr2[2]);
	var endtimes = endtime.getTime();
	if(starttimes >= endtimes)
		return false;
	else
		return true;
}


function addAfficheFunction(){
	//afficheCode afficheTitle  startTime endTime afficheContent
	afficheCode = $("#afficheCode");
	afficheTitle = $("#afficheTitle");
	startTime = $("#startTime");
	endTime = $("#endTime");
	add_formtip = $("#add_formtip");
	afficheContent = $("#afficheContent");
	//alert("["+afficheCode+afficheTitle+startTime+endTime+afficheContent+"]");
	
	if( $.trim(afficheCode.val()) == "" || afficheCode.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告代码不能为空。");
		afficheCode.focus();
		return false;
	}else if( $.trim(afficheTitle.val()) == "" || afficheTitle.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告标题不能为空。");
		afficheTitle.focus();
		return false;
	}else if( $.trim(startTime.val()) == "" || startTime.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告有效期开始时间不能为空。");
		startTime.focus();
		return false;
	}else if( $.trim(endTime.val()) == "" || endTime.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告有效期结束时间不能为空。");
		endTime.focus();
		return false;
	}else if(comtime(startTime.val(),endTime.val()) == false){
		add_formtip.css("color","red");
		add_formtip.html("生效时间大于失效时间，请检查");
		endTime.focus();
		return false;
	}else if(afficheContent.val() == "" || afficheContent.val() == "<br>"){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告内容不能为空。");
		afficheContent.focus();
		return false;
	}else{
		return true;
	}
	
}

function modifyAfficheFunction(){
	//afficheCode afficheTitle  startTime endTime afficheContent
	afficheCode = $("#afficheCodeModify");
	afficheTitle = $("#afficheTitleModify");
	startTime = $("#startTimeModify");
	endTime = $("#endTimeModify");
	add_formtip = $("#add_formtipModify");
	afficheContent = $("#afficheContentModify");
	//alert("["+afficheCode+afficheTitle+startTime+endTime+afficheContent+"]");
	if( $.trim(afficheCode.val()) == "" || afficheCode.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告代码不能为空。");
		afficheCode.focus();
		return false;
	}else if( $.trim(afficheTitle.val()) == "" || afficheTitle.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告标题不能为空。");
		afficheTitle.focus();
		return false;
	}else if( $.trim(startTime.val()) == "" || startTime.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告有效期开始时间不能为空。");
		startTime.focus();
		return false;
	}else if( $.trim(endTime.val()) == "" || endTime.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告有效期结束时间不能为空。");
		endTime.focus();
		return false;
	}else if(comtime(startTime.val(),endTime.val()) == false){
		add_formtip.css("color","red");
		add_formtip.html("生效时间大于失效时间，请检查");
		endTime.focus();
		return false;
	}else if(afficheContent.val() == "" || afficheContent.val() == "<br>"){
		add_formtip.css("color","red");
		add_formtip.html("对不起，公告内容不能为空。");
		afficheContent.focus();
		return false;
	}else{
		return true;
	}
}

$(".viewaffiche").click(function(e){
	id = $(this).attr("id");
	
	afficheCodeText = $("#afficheCodeText");
	afficheTitleText = $("#afficheTitleText");
	startTimeText = $("#startTimeText");
	endTimeText = $("#endTimeText");
	afficheContentText = $("#afficheContentText");
	
	$.ajax({
		url: '/informanage/viewAffiche.html',
		type: 'POST',
		data:{id:id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("获取"+title+"失败！请重试。");
		},
		success: function(result){
			if(result != ""){
				
				jsonStrAffiche = eval("("+result+")");
				
				afficheCodeText.val(jsonStrAffiche.code);
				afficheTitleText.val(jsonStrAffiche.title);
				startTimeText.val(jsonStrAffiche.startTime);
				endTimeText.val(jsonStrAffiche.endTime);
				afficheContentText.html(jsonStrAffiche.content);
				e.preventDefault();
				$('#viewAfficheDiv').modal('show');
				
			}else if("failed" == result){
				alert("获取"+title+"失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
});

$(".modifyaffiche").click(function(e){
	id = $(this).attr("id");
	afficheCodeText = $("#afficheCodeModify");
	afficheTitleText = $("#afficheTitleModify");
	startTimeText = $("#startTimeModify");
	endTimeText = $("#endTimeModify");
	afficheContentText = $("#afficheContentText");
	$("#afficheIdModify").val(id);
	$.ajax({
		url: '/informanage/viewAffiche.html',
		type: 'POST',
		data:{id:id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("获取"+title+"失败！请重试。");
		},
		success: function(result){
			if(result != ""){
				
				jsonStrAffiche = eval("("+result+")");
				
				afficheCodeText.val(jsonStrAffiche.code);
				afficheTitleText.val(jsonStrAffiche.title);
				startTimeText.val(jsonStrAffiche.startTime);
				endTimeText.val(jsonStrAffiche.endTime);
				$("#modifyafficheli").html("");
				$("#modifyafficheli").append("<span>公告内容：</span> <br/><textarea id=\"afficheContentModify\" name=\"content\" rows=\"3\">"+jsonStrAffiche.content+"</textarea>");
				$('#afficheContentModify').cleditor();
				e.preventDefault();
				$('#modifyAfficheDiv').modal('show');
				
			}else if("failed" == result){
				alert("获取"+title+"失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
});

$(".delaffiche").click(function(){
	id = $(this).attr("id");
	title = $(this).attr("title");
	if(confirm("您确定要删除" + title + "吗？" )){
		$.ajax({
			url: '/informanage/delAffiche.html',
			type: 'POST',
			data:{id:id},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("删除"+title+"失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					window.location.href="/informanage/affiche.jsp";
				}else if("failed" == result){
					alert("删除"+title+"失败！请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
});

