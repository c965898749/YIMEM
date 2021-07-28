$('.addrole').click(function(e){
		e.preventDefault();
		$('#addRoleDiv').modal('show');
  });

$("#addRoleCancel").click(loadingRoleList);
$("#addRoleClose").click(loadingRoleList);

function loadingRoleList(){
	window.location.href = "/backend/rolelist.html";
}
$("#addRoleBtn").click(function(){
	var role = new Object();
	role.roleCode = $.trim($("#roleCode").val());
	role.roleName = $.trim($("#roleName").val());
	
	if(role.roleCode == "" || role.roleCode == null){
		 $("#roleCode").focus();
		 $("#formtip").css("color","red");
		 $("#formtip").html("对不起，角色代码不能为空。");
	}else if(role.roleName == "" || role.roleName == null){
		$("#roleName").focus();
		$("#formtip").css("color","red");
		$("#formtip").html("对不起，角色名称不能为空。");
	}else{
		$("#formtip").html("");
		
		$.ajax({
			url: '/backend/addRole.html',
			type: 'POST',
			data:{role:JSON.stringify(role)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				$("#formtip").css("color","red");
				$("#formtip").html("角色添加失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					$("#formtip").css("color","green");
					$("#formtip").html("角色添加成功 ^_^ 继续添加请填写。");
					$("#roleCode").val('');
					$("#roleName").val('');
				}else if("failed" == result){
					$("#formtip").css("color","red");
					$("#formtip").html("角色添加失败！请重试。");
				}
				else if("rename" == result){
					$("#formtip").css("color","red");
					$("#formtip").html("角色添加失败！角色代码和角色名称不能重复，请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
});





$(".modifyrole").click(function(){
	var modify = $(this);
	var id= modify.attr("roleid");
	var oldCode= modify.attr("rolecode");
	var oldName= modify.attr("rolename");
	var roleCode = $.trim($("#roleCode"+id).val());
	var roleName = $.trim($("#roleName"+id).val());
	if(roleCode == "" || roleCode == null){
		 alert("对不起，角色代码不能为空。");
	}else if(roleName == "" || roleName == null){
		alert("对不起，角色名称不能为空。");
	}else{
		var tip = "您确定要将原来的\n角色代码："+oldCode + "和角色名称："+oldName + "\n,修改为\n角色代码：" + roleCode + "和角色名称：" + roleName + "\n吗？";
		if(confirm(tip)){
			var role = new Object();
			role.id = id;
			role.roleCode = roleCode;
			role.roleName = roleName;
			$.ajax({
				url: '/backend/modifyRole.html',
				type: 'POST',
				data:{role:JSON.stringify(role)},
				dataType: 'html',
				timeout: 1000,
				error: function(){
					alert("角色修改失败！请重试。");
				},
				success: function(result){
					if(result != "" && "success" == result){
						alert("角色修改成功 ^_^");
					}else if("failed" == result){
						alert("角色修改失败！请重试。");
					}else if("nodata" == result){
						alert("对不起，没有任何数据需要处理！请重试。");
					}
				}
				});
		}
	}
});
$(".modifyIsStart").click(function(){
	modify = $(this);
	id= modify.attr("roleid");
	isstart= modify.attr("isstart");
	roleIstart = new Object();
	roleIstart.id = id;
	roleIstart.roleName = null;
	if(isstart == "1"){
		roleIstart.isStart = 2;
	}
	else{
		roleIstart.isStart = 1;
	}
	
	$.ajax({
		url: '/backend/modifyRole.html',
		type: 'POST',
		data:{role:JSON.stringify(roleIstart)},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("开启或关闭角色操作时失败！请重试。");
		},
		success: function(result){
			if(result != "" && "success" == result){
				if(isstart == "1")
					modify.attr("isstart",0);
				else
					modify.attr("isstart",1);
			}else if("failed" == result){
				alert("开启或关闭角色操作时失败！请重试。");
			}else if("nodata" == result){
				alert("对不起，没有任何数据需要处理！请重试。");
			}
		}
	});
});

$(".delrole").click(function(){
	var modify = $(this);
	var id= modify.attr("roleid");
	var roleName= modify.attr("rolename");
	var tip = "您确定要删除角色："+roleName+"吗？";
	if(confirm(tip)){
		var role = new Object();
		role.id = id;
		$.ajax({
			url: '/backend/delRole.html',
			type: 'POST',
			data:{role:JSON.stringify(role)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("删除角色失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					alert("删除角色成功 ^_^");
					loadingRoleList();
				}else if("failed" == result){
					alert("删除角色失败！请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}else{
					if(result != null && result != ""){
						result = result.substring(0,result.length-1);
						alert("系统中有用户被授权该角色，不能被删除！用户账号：【"+result+"】");
					}
				}
			}
			});
	}
});