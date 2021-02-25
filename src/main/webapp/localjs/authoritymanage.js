$('.roleNameAuthority').click(function(){
	var authority = $(this);
	var roleId = authority.attr("roleid");
	$("#selectrole").html("当前配置角色为：" + authority.attr("rolename"));
	$("#roleidhide").val(roleId);
	//functions
	$.ajax({
		url: '/backend/functions.html',
		type: 'POST',
		data:{fid:roleId},
		dataType: 'html',
		timeout: 1000,
		error: function(){
		},
		success: function(result){
			if(result == "nodata"){
				alert("对不起，功能列表获取失败，请重试。");
			}else{
				var json = eval('(' + result + ')');
				listr = "";
				
				for(var i=0;i<json.length;i++){
					listr += "<li>";
					listr += "<ul id=\"subfuncul"+json[i].mainFunction.id+"\" class=\"subfuncul\">";
					listr += "<li  class=\"functiontitle\" ><input id='functiontitle"+json[i].mainFunction.id+"' onchange='mainFunctionSelectChange(this,"+json[i].mainFunction.id+");' funcid=\""+json[i].mainFunction.id+"\" type='checkbox' />"+json[i].mainFunction.functionName+"</li>";
					for(j=0;j<json[i].subFunctions.length;j++){
						
						listr += "<li><input onchange='subFunctionSelectChange(this,"+json[i].mainFunction.id+");' funcid=\""+json[i].subFunctions[j].id+"\" type='checkbox' /> "+json[i].subFunctions[j].functionName+"</li>";
					}
					listr += "</ul></li>";
				}
				
				$("#functionList").html(listr);
				
				//get default value
				$("#functionList :checkbox").each(function () {  
					var checkbox = $(this);
					$.ajax({
						url: '/backend/getAuthorityDefault.html',
						type: 'POST',
						data:{rid:$("#roleidhide").val(),fid:$(this).attr("funcid")},
						dataType: 'html',
						timeout: 1000,
						error: function(){
						},
						success: function(result){
							if(result == "success"){
								//alert("ok");
								checkbox.attr("checked", true); 
							}else{
								//alert("no");
								checkbox.attr("checked", false);
							}
						}
						});
				});
				
				
			}
		}
		});
});

function subFunctionSelectChange(obj,id){
	if(obj.checked){
		$("#functiontitle"+id).attr("checked", true);  
	}
}

function mainFunctionSelectChange(obj,id){
	if(obj.checked){
		$("#subfuncul"+id+" :checkbox").attr("checked", true);  
	}else{
		$("#subfuncul"+id+" :checkbox").attr("checked", false);  
	}
	
	//alert($(this) +　id);
}


$("#selectAll").click(function () {//全选  
    $("#functionList :checkbox").attr("checked", true);  
});  

$("#unSelect").click(function () {//全不选  
    $("#functionList :checkbox").attr("checked", false);  
});  

$("#reverse").click(function () {//反选  
    $("#functionList :checkbox").each(function () {  
        $(this).attr("checked", !$(this).attr("checked"));  
    });  
});  


  

$("#confirmsave").click(function(){
	
	if(confirm("您确定要修改当前角色的权限吗？")){
	
	ids = $("#roleidhide").val()+"-";
	$("#functionList :checkbox").each(function () {
		if($(this).attr("checked") == 'checked'){
			ids += $(this).attr("funcid") + "-" ;
		}
    }); 
	$.ajax({
		url: '/backend/modifyAuthority.html',
		type: 'POST',
		data:{ids:ids},
		dataType: 'html',
		timeout: 1000,
		error: function(){
		},
		success: function(result){
			if(result == "nodata"){
				alert("对不起，功能列表获取失败，请重试。");
			}else{
				alert("恭喜您，权限修改成功。");
			}
		}
		});
	}
});
