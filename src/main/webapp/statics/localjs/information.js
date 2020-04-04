$('#informationContent').cleditor();

$(".addinfocancel").click(function(){
	$("#add_formtip").html("");
});
$(".modifyinfocancel").click(function(){
	$("#modify_formtip").html("");
});

$("#informationuploadbtn").click(function(){
	informationFileUpload();
});
$("#informationuploadMbtn").click(function(){
	informationFileUploadM();
});
$(".addInformation").click(function(e){
	e.preventDefault();
	/*
	$("#afficheCode").val("");
	$("#afficheTitle").val("");
	$("#startTime").val("");
	$("#endTime").val("");
	$("#afficheContent").val("");
	$("#add_formtip").html("");
	*/
	$('#addInformationDiv').modal('show');
	$("#uniform-uploadInformationFile span:first").html('无文件');
});

$("#docType").change(function(){
	$("#typeNamehide").val($("#docType").find("option:selected").text());
	//alert($("#typeNamehide").val());
});
$("#docTypeModity").change(function(){
	$("#typeNamehideM").val($("#docTypeModity").find("option:selected").text());
	//alert($("#typeNamehide").val());
});

function delFile(){
	$.ajax({
		url: '/informanage/delInfoFile.html',
		type: 'POST',
		data:{filePath:$("#uploadfilepathhide").val()},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("删除文件失败！请重试。");
		},
		success: function(result){
			if(result != "" && result == "success"){
				$("#informationuploadbtn").show();
				$("#filearea").html("");
       		    $("#uploadfilenamehide").val('');
    		    $("#uploadfilepathhide").val('');
    		    $("#fileSizehide").val('');
    		    $("#uniform-uploadInformationFile span:first").html('无文件');
			}else if("failed" == result){
				alert("删除文件失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
}
function delFileM(){
	if(confirm("您确定要删除文件吗？如果删除将永久删除。")){
	$.ajax({
		url: '/informanage/delInfoFile.html',
		type: 'POST',
		data:{filePath:$("#uploadfilepathhideM").val()},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("删除文件失败！请重试。");
		},
		success: function(result){
			if(result != "" && result == "success"){
				$("#uploadfilepathhideM").val('');
				$("#uploadfilenamehideM").val('');
				$("#fileSizehideM").val('');
				$("#informationuploadMbtn").show();
				$("#fileareaM").html("");
				$("#uniform-uploadInformationFileM span:first").html('无文件');
			}else if("failed" == result){
				alert("删除文件失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
	}
}

function informationFileUpload()
{   
	if($("#uploadInformationFile").val() == "" || $("#uploadInformationFile").val()  == null){
		alert("请选择上传文件！");
	}else{
		$.ajaxFileUpload
	    ({ 
	           url:'/informanage/upload.html', //处理上传文件的服务端
	           secureuri:false,
	           fileElementId:'uploadInformationFile',
	           dataType: 'json',
	           success: function(data) { 
	        	   data = data.replace(/(^\s*)|(\s*$)/g, "");
	        	   if(data == "1"){
	        		   alert("上传图片大小不得超过500M！");
	        		   $("#uniform-uploadInformationFile span:first").html('无文件');
	        		   $("#uploadInformationFile").change(function(){
	        			   var fn = $("#uploadInformationFile").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-uploadInformationFile span:first").html(fn);
	        		   });
	        	   }else{
	        		   var oldFile = data.substring(0,data.indexOf("[[[]]]"));
	        		   var newFile = data.substring(data.indexOf("[[[]]]")+6,data.indexOf("size:"));
	        		   var fileSize = data.substring(data.indexOf("size:")+5);
	        		   $("#uploadfilenamehide").val(oldFile);
	        		   $("#uploadfilepathhide").val(newFile);
	        		   $("#fileSizehide").val(fileSize);
	        		   $("#filearea").css("color","green");
	        		   $("#filearea").html("上传文件：" + oldFile + " 大小："+(fileSize/1000)+"KB <a style=\"color:red;\" href=\"javascript:delFile();\">X</a>");
	        		   $("#informationuploadbtn").hide();
	        		   $("#uploadInformationFile").change(function(){
	        			   var fn = $("#uploadInformationFile").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-uploadInformationFile span:first").html(fn);
	        		   });
	        	   }
	           },  
	           error: function() {  
	              alert("上传失败！");
	           } 
	        });
	}
}
function informationFileUploadM()
{   
	if($("#uploadInformationFileM").val() == "" || $("#uploadInformationFileM").val()  == null){
		alert("请选择上传文件！");
	}else{
		$.ajaxFileUpload
		({ 
			url:'/informanage/upload.html', //处理上传文件的服务端
			secureuri:false,
			fileElementId:'uploadInformationFileM',
			dataType: 'json',
			success: function(data) { 
				data = data.replace(/(^\s*)|(\s*$)/g, "");
				if(data == "1"){
					alert("上传图片大小不得超过500M！");
				}else{
					var oldFile = data.substring(0,data.indexOf("[[[]]]"));
					var newFile = data.substring(data.indexOf("[[[]]]")+6,data.indexOf("size:"));
					var fileSize = data.substring(data.indexOf("size:")+5);
					$("#uploadfilenamehideM").val(oldFile);
					$("#uploadfilepathhideM").val(newFile);
					$("#fileSizehideM").val(fileSize);
					$("#fileareaM").css("color","green");
					$("#fileareaM").html("上传文件：" + oldFile + " 大小："+(fileSize/1000)+"KB <a style=\"color:red;\" href=\"javascript:delFileM();\">X</a>");
					$("#informationuploadMbtn").hide();
					 $("#uploadInformationFileM").change(function(){
	        			   var fn = $("#uploadInformationFileM").val(); 
	        			   if($.browser.msie){
	        				   fn = fn.substring(fn.lastIndexOf("\\")+1);
	        			   }
	        			   $("#uniform-uploadInformationFileM span:first").html(fn);
	        		 });
				}
			},  
			error: function() {  
				alert("上传失败！");
			} 
		});
	}
}


$(".viewinformation").click(function(e){
	id = $(this).attr("id");
	title = $(this).attr("title");
	$.ajax({
		url: '/informanage/viewInfo.html',
		type: 'POST',
		data:{id:id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("获取"+title+"失败！请重试。");
		},
		success: function(result){
			if(result != ""){
				
				jsonStrInfo = eval("("+result+")");
				state = "未发布";
				if(jsonStrInfo.state == 1){
					state = "发布";
				}
				$("#viewContent").html("");
				
				$("#viewContent").append("<li>标题：<input type=\"text\" style=\"border:0px;\" disabled=\"disabled\" value=\""+jsonStrInfo.title+"\"/></li>");
				$("#viewContent").append("<li>发布状态："+state+"</li>");
				$("#viewContent").append("<li>发布人："+jsonStrInfo.publisher+"</li>");
				$("#viewContent").append("<li>发布时间："+jsonStrInfo.publishTime+"</li>");
				if(jsonStrInfo.fileName != null && jsonStrInfo.fileName != "" && jsonStrInfo.filePath != null && jsonStrInfo.filePath != ""){
					$("#viewContent").append("<li>附件类型："+jsonStrInfo.typeName+"</li>");
					$("#viewContent").append("<li>附件名称："+jsonStrInfo.fileName+"</li>");
					$("#viewContent").append("<li>附件存放路径：<a href='"+jsonStrInfo.filePath+"'>下载(右键另存为...)</a></li>");
					$("#viewContent").append("<li>附件大小："+(jsonStrInfo.fileSize/1000)+"KB</li>");
				}else{
					$("#viewContent").append("<li>附件：暂无</li>");
				}
				$("#viewContent").append("<li>上传时间："+jsonStrInfo.uploadTime+"</li>");
				$("#viewContent").append("<li>资讯内容：<div>"+jsonStrInfo.content+"</div></li>");
				e.preventDefault();
				$('#viewInfoDiv').modal('show');
				
			}else if("failed" == result){
				alert("获取"+title+"失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
});

$(".modifyinformation").click(function(e){
	id = $(this).attr("id");
	$.ajax({
		url: '/informanage/viewInfo.html',
		type: 'POST',
		data:{id:id},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("获取"+title+"失败！请重试。");
		},
		success: function(result){
			if(result != ""){
				
				jsonStrInfo = eval("("+result+")");
				//id
				$("#infoIdModify").val(jsonStrInfo.id);
				$("#informationTitleModify").val(jsonStrInfo.title);
				//typeID typeName
				$("#typeNamehideM").val(jsonStrInfo.typeName);
				$("#docTypeModity").val(jsonStrInfo.typeId);
				//fileName
				$("#uploadfilenamehideM").val(jsonStrInfo.fileName);
				//filePath
				$("#uploadfilepathhideM").val(jsonStrInfo.filePath);
				//fileSize
				$("#fileSizehideM").val(jsonStrInfo.fileSize);
				//set select
				$("#docTypeModity").html("");
				for(var i=0;i<dicJson.length-1;i++){
					if(dicJson[i].valueId == jsonStrInfo.typeId){
						$("#docTypeModity").append("<option value=\""+dicJson[i].valueId+"\" selected=\"selected\">"+dicJson[i].valueName+"</option>");
					}else{
						$("#docTypeModity").append("<option value=\""+dicJson[i].valueId+"\">"+dicJson[i].valueName+"</option>");
					}
				}
				if(jsonStrInfo.fileName != null && jsonStrInfo.fileName != ""){
					$("#fileareaM").css("color","green");
	     		    $("#fileareaM").html("上传文件：" + jsonStrInfo.fileName + " 大小："+(jsonStrInfo.fileSize/1000)+"KB <a style=\"color:red;\" href=\"javascript:delFileM();\">X</a>");
				}else{
					$("#fileareaM").css("color","red");
	     		    $("#fileareaM").html("上传文件：暂无");
	     		    $("#informationuploadMbtn").show();
				}
				$("#modifyinformationli").html("");
     		    $("#modifyinformationli").append("<span>资讯内容：</span> <br/><textarea id=\"infoContentModifyContent\" name=\"content\" rows=\"3\">"+jsonStrInfo.content+"</textarea>");
     		    $('#infoContentModifyContent').cleditor();
				
				/*
				$("#viewContent").html("");
				$("#viewContent").append("<li>标题："+jsonStrInfo.title+"</li>");
				$("#viewContent").append("<li>发布状态："+state+"</li>");
				$("#viewContent").append("<li>发布人："+jsonStrInfo.publisher+"</li>");
				$("#viewContent").append("<li>发布时间："+jsonStrInfo.publishTime+"</li>");
				$("#viewContent").append("<li>附件类型："+jsonStrInfo.typeName+"</li>");
				$("#viewContent").append("<li>附件名称："+jsonStrInfo.fileName+"</li>");
				$("#viewContent").append("<li>附件存放路径：<a href='"+jsonStrInfo.filePath+"'>下载(右键另存为...)</a></li>");
				$("#viewContent").append("<li>附件大小："+(jsonStrInfo.fileSize/1000)+"KB</li>");
				$("#viewContent").append("<li>上传时间："+jsonStrInfo.uploadTime+"</li>");
				$("#viewContent").append("<li>资讯内容：<div>"+jsonStrInfo.content+"</div></li>");
				*/
				e.preventDefault();
				$('#modifyInfoDiv').modal('show');
				
			}else if("failed" == result){
				alert("获取"+title+"失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
		});
});

function CoverFormXmlTag(value){
	value = value.replace("&","&amp;");
	value = value.replace("<","&lt;");
	value = value.replace(">","&gt;");
	value = value.replace("'\'","&quot;");
	value = value.replace("\r\n","<br>");
	value = value.replace("","");
	return value;
}

function addInfoFunction(){
	infoTitle = $("#informationTitle");
	docType = $("#docType");
	filePath = $("#uploadfilepathhide");
	content = $("#informationContent");
	add_formtip = $("#add_formtip");
	
	//infoTitle.val(CoverFormXmlTag(infoTitle.val()));
	
	if( $.trim(infoTitle.val()) == "" || infoTitle.val() == null){
		add_formtip.css("color","red");
		add_formtip.html("对不起，资讯标题不能为空。");
		infoTitle.focus();
		return false;
	}else if( docType.val() == ""){
		add_formtip.css("color","red");
		add_formtip.html("对不起，资讯类型不能为空。");
		docType.focus();
		return false;
	}
//	else if( filePath.val() == ""){
//		add_formtip.css("color","red");
//		add_formtip.html("对不起，您还没有上传文件。");
//		return false;}
	else if( content.val() == "" ||  content.val() == "<br>"){
		add_formtip.css("color","red");
		add_formtip.html("对不起，资讯内容不能为空。");
		return false;
	}else{
		return true;
	}
}
function modifyInfoFunction(){
	infoTitle = $("#informationTitleModify");
	docType = $("#docTypeModity");
	filePath = $("#uploadfilepathhideM");
	content = $("#infoContentModifyContent");
	modify_formtip = $("#modify_formtip");
	infoid = $("#infoIdModify");
	//alert(infoid.val());
	//return false;
	//infoTitle.val(CoverFormXmlTag(infoTitle.val()));
	if( $.trim(infoTitle.val()) == "" || infoTitle.val() == null){
		modify_formtip.css("color","red");
		modify_formtip.html("对不起，资讯标题不能为空。");
		infoTitle.focus();
		return false;
	}else if( docType.val() == ""){
		modify_formtip.css("color","red");
		modify_formtip.html("对不起，资讯类型不能为空。");
		docType.focus();
		return false;
	}
//	else if( filePath.val() == ""){
//		modify_formtip.css("color","red");
//		modify_formtip.html("对不起，您还没有上传文件。");
//		return false;}
	else if( content.val() == "" ||  content.val() == "<br>"){
		modify_formtip.css("color","red");
		modify_formtip.html("对不起，资讯内容不能为空。");
		return false;
	}else{
		return true;
	}
}

$(".delinformation").click(function(){
	id = $(this).attr("id");
	title = $(this).attr("title");
	if(confirm("您确定要删除" + title + "吗？" )){
		$.ajax({
			url: '/informanage/delInfo.html',
			type: 'POST',
			data:{id:id},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("删除"+title+"失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					window.location.href="/informanage/information.html";
				}else if("failed" == result){
					alert("删除"+title+"失败！请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
});




$(".modifyInformationState").click(function(){
	modify = $(this);
	id= modify.attr("inforid");
	state= modify.attr("inforstate");
	infoState = new Object();
	infoState.id = id;
	if(state == "1"){
		infoState.state = 0;
		modify.attr("inforstate",0);
	}
	else{
		infoState.state = 1;
		modify.attr("inforstate",1);
	}
	
	$.ajax({
		url: '/informanage/modifyInfoState.html',
		type: 'POST',
		data:{inforState:JSON.stringify(infoState)},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("开启或关闭发布状态操作时失败！请重试。");
		},
		success: function(result){
			if(result != "" && "success" == result){
				if(isstart == "1")
					modify.attr("isstart",0);
				else
					modify.attr("isstart",1);
			}else if("failed" == result){
				alert("开启或关闭发布状态操作时失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
	});
});