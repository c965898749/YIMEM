var maxValue = 0;
//类型代码不能重复验证(修改)
$("#modifytypeCode").blur(function(){
	var mlc = $("#modifytypeCode").val();
	if(mlc != ""){
		$.post("/backend/typecodeisexit.html",{'typeCode':mlc,'id':$("#modifydicid").val()},function(result){
			if(result == "repeat"){
				$("#modifyDictip").css("color","red");
				$("#modifyDictip").html("对不起，该类型代码已存在。");
				result = false;
			}else if(result == "failed"){
				alert("操作超时!");
			}else if(result == "only"){
				$("#modifyDictip").css("color","green");
				$("#modifyDictip").html("该类型代码可以正常使用。");
			}
		},'html');
	}
});

$(".addDicBtn").click(function(e){
	e.preventDefault();
	$("#addDicModel").modal('show');
	
});
$(".maintitle").mouseenter(function(){
	$(this).children(".mainset").show();
});
$(".maintitle").mouseleave(function(){
	$(this).children(".mainset").hide();
});

$(".addDicLiBtn").click(function(e){
	e.preventDefault();
	$("#addDicSubModel").modal('show');
	
});


$(".typecodelist").click(function(){
	taga = $(this);
	typecode = taga.attr("typecode");
	typename = taga.attr("typename");
	$("#typeDicSubCode").val(typecode);
	$("#typeDicSubName").val(typename);
	$("#valueDicSubName").val("");
	$("#addDicSubtip").html("");
	$("#optitle").html("当前操作：" + typecode + " - " + typename);
	//alert(id + "," + typecode + "," + typename);
	//getJsonDic tcode
	$.ajax({
		url: '/backend/getJsonDic.html',
		type: 'POST',
		data:{tcode:typecode},
		dataType: 'html',
		timeout: 1000,
		error: function(){
			alert("查询"+typename+"失败！请重试。");
		},
		success: function(result){
			if("failed" == result){
				alert("查询"+typename+"失败！请重试。");
			}else if("nodata" == result){
				alert("查询"+typename+"失败！请重试。");
			}else{
				//alert(result);
				jsonObj = eval('('+result+')');
				//alert(jsonObj.length);
				dicListUL = $("#dicListUL");
				/*
				 <li>
					<div>类型代码:</div>
					<div>类型名称:</div>
					<div>数据数值:<input type="text" id=""/></div>
					<div>数值名称:<input type="text" id=""/></div>
					<div class="editdiv">
						<img src="/statics/img/ico10.png"> <img src="/statics/img/linkdel.png">
					</div>
				</li>
				 */
				str = "";
				dicListUL.html("");
				for(var i=0;i<jsonObj.length;i++){
					str += "<li id=\"li"+jsonObj[i].id+"\">";
					str += "<div>类型代码:"+jsonObj[i].typeCode+"</div>";
					str += "<div>类型名称:"+jsonObj[i].typeName+"</div>";
					
					str += "<div>数据数值:<input type=\"text\" disabled=\"disabled\" onkeyup=\"this.value=this.value.replace(\/\\D\/g,'')\" onafterpaste=\"this.value=this.value.replace(\/\\D\/g,'')\" id=\"valueIdText"+jsonObj[i].id+"\" value=\""+jsonObj[i].valueId+"\"/></div>";
					str += "<div>数值名称:<input type=\"text\" id=\"valueNameText"+jsonObj[i].id+"\" value=\""+jsonObj[i].valueName+"\"/></div>";
					str += "<div class=\"editdiv\">";
					str += "<img class=\"modifyDicValue\" modifytypecode="+jsonObj[i].typeCode+" modifytypename="+jsonObj[i].typeName+" id=\""+jsonObj[i].id+"\" valueid=\""+jsonObj[i].valueId+"\" valuename=\""+jsonObj[i].valueName+"\" src=\"/statics/img/ico10.png\"> <img class=\"delDicValue\" deltypename="+jsonObj[i].typeName+" id=\""+jsonObj[i].id+"\" valueid=\""+jsonObj[i].valueId+"\" valuename=\""+jsonObj[i].valueName+"\"  src=\"/statics/img/linkdel.png\">";
					str += "</div>";
					str += "<div id=\"dicTip"+jsonObj[i].id+"\"></div>";
					str += "</li>";
					maxValue = jsonObj[i].valueId;
				}
				//str += "<li id=\"addDicLiBtn\" class=\"addDicLiBtn\"><img src=\"/statics/img/winapp_add.png\"/></li>";
				dicListUL.append(str);
				
				$("#addsubdicul").show();
				
				$(".modifyDicValue").click(function(){
					modify = $(this);
					dic = new Object();
					dic.id = modify.attr("id");
					dic.valueId = modify.attr("valueid");
					dic.valueName = modify.attr("valuename");
					dic.typeCode = modify.attr("modifytypecode");
					
					tip = $("#dicTip"+dic.id);
					//modifytypename
					//alert(dic.id + "," + dic.valueId + "," + dic.valueName);
					if(confirm("您确定要修改【"+modify.attr("modifytypename")+"】- 【"+dic.valueName+"】- 【"+dic.valueId+"】的数据配置吗？")){
						//modifyDic dicJson
						dic.valueId = $.trim($("#valueIdText"+dic.id).val());
						dic.valueName = $.trim($("#valueNameText"+dic.id).val());
						if(dic.valueId == ""){
							$("#valueIdText"+dic.id).focus();
							tip.css("color","red");
							tip.html("数据数值 不能为空。");
						}else if(dic.valueName == ""){
							$("#valueNameText"+dic.id).focus();
							tip.css("color","red");
							tip.html("数据名称 不能为空。");
						}else{
						$.ajax({
							url: '/backend/modifyDic.html',
							type: 'POST',
							data:{dicJson:JSON.stringify(dic)},
							dataType: 'html',
							timeout: 1000,
							error: function(){
								tip.css("color","red");
								tip.html("数据字典修改失败！请重试。");
							},
							success: function(result){
								if(result != "" && "success" == result){
									tip.css("color","green");
									tip.html("恭喜您，修改成功。^_^");
								}else if("failed" == result){
									tip.css("color","red");
									tip.html("数据字典修改失败！请重试。");
								}else if("nodata" == result){
									tip.css("color","red");
									tip.html("对不起，没有任何数据需要处理！请重试。");
								}else if("rename" == result){
									tip.css("color","red");
									tip.html("添加失败！该类型代码下的数据ID不能重复，请重试。");
								}
							}
							});
						}
					}
				});
				$(".delDicValue").click(function(){
					del = $(this);
					dic = new Object();
					dic.id = del.attr("id");
					dic.valueId = del.attr("valueid");
					dic.valueName = del.attr("valuename");
					tip = $("#dicTip"+dic.id);
					//alert(dic.id + "," + dic.valueId + "," + dic.valueName);
					if(confirm("您确定要删除【"+del.attr("deltypename")+"】- 【"+dic.valueName+"】- 【"+dic.valueId+"】的数据配置吗？")){
						//delDic id
						$.ajax({
							url: '/backend/delDic.html',
							type: 'POST',
							data:{id:dic.id},
							dataType: 'html',
							timeout: 1000,
							error: function(){
								tip.css("color","red");
								tip.html("数据字典删除失败！请重试。");
							},
							success: function(result){
								if(result != "" && "success" == result){
									//alert(del.parents("li"+dic.id));
									del.parents("#li"+dic.id).remove();
								}else if("failed" == result){
									tip.css("color","red");
									tip.html("数据字典删除失败！请重试。");
								}else if("nodata" == result){
									tip.css("color","red");
									tip.html("对不起，没有任何数据需要处理！请重试。");
								}
							}
							});
					}
				});
				
				
				
			}
		}
		});
});



$("#modifyDicExeBtn").click(function(){
	dic = new Object();
	dic.typeCode = $.trim($("#modifytypeCode").val());
	dic.typeName = $.trim($("#modifytypeName").val());
	dic.id = $("#modifydicid").val();
	
	olddic = new Object();
	olddic.typeCode = $("#modifydictypecode").val();
	olddic.typeName = $("#modifydictypename").val();
	olddic.id = $("#modifydicid").val();
	
	//alert(dic.typeCode+","+olddic.typeCode);
	
	tip = $("#modifyDictip");
	if(dic.typeCode == ""){
		$("#modifytypeCode").focus();
		tip.css("color","red");
		tip.html("类型代码不能为空！");
	}else if(dic.typeName == ""){
		$("#modifytypeName").focus();
		tip.css("color","red");
		tip.html("类型名称不能为空！");
	}else{
		//alert(dic.typeCode + "," +dic.typeName + "," + dic.id);
		$.ajax({
			url: '/backend/modifylDic.html',
			type: 'POST',
			data:{olddic:JSON.stringify(olddic),newdic:JSON.stringify(dic)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("数据字典修改失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					window.location.href = "/backend/dicmanage.html";
				}else if("failed" == result){
					tip.css("color","red");
					tip.html("数据字典修改失败！请重试。");
				}else if("nodata" == result){
					tip.css("color","red");
					tip.html("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
});
$(".modifyMainDic").click(function(e){
	dictypecode = $(this).attr("dictypecode");
	dictypename = $(this).attr("dictypename");
	dicid = $(this).attr("dicid");
	
	$("#modifytypeCode").val(dictypecode);
	$("#modifytypeName").val(dictypename);
	$("#modifydicid").val(dicid);
	$("#modifydictypecode").val(dictypecode);
	$("#modifydictypename").val(dictypename);
	
	e.preventDefault();
	$("#modifyDicModel").modal('show');
});
$(".delMainDic").click(function(){
	dictypecode = $(this).attr("dictypecode");
	dictypename = $(this).attr("dictypename");
	dic = new Object();
	if(confirm("删除操作会删除\""+dictypename+"\"下所有的字典值，您确定要删除\""+dictypename+"\"吗？")){
		dic.typeCode = dictypecode;
		
		$.ajax({
			url: '/backend/delMainDic.html',
			type: 'POST',
			data:{dic:JSON.stringify(dic)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				alert("数据字典删除失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					window.location.href = "/backend/dicmanage.html";
				}else if("failed" == result){
					alert("数据字典删除失败！请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
});
$("#addDicExeBtn").click(function(){
	dic = new Object();
	dic.typeCode = $.trim($("#typeCode").val());
	dic.typeName = $.trim($("#typeName").val());
	dic.valueId = 1;
	dic.valueName = $("#valueName").val();
	tip = $("#addDictip");
	if(dic.typeCode == ""){
		$("#typeCode").focus();
		tip.css("color","red");
		tip.html("类型代码不能为空！");
	}else if(dic.typeName == ""){
		$("#typeName").focus();
		tip.css("color","red");
		tip.html("类型名称不能为空！");
	}else if(dic.valueName == ""){
		$("#valueName").focus();
		tip.css("color","red");
		tip.html("值名称不能为空！");
	}else{
		//alert(dic.typeCode + "," +dic.typeName + "," + dic.valueName);
		$.ajax({
			url: '/backend/addDic.html',
			type: 'POST',
			data:{dic:JSON.stringify(dic)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				tip.css("color","red");
				tip.html("数据字典添加失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					window.location.href = "/backend/dicmanage.html";
				}else if("failed" == result){
					tip.css("color","red");
					tip.html("数据字典添加失败！请重试。");
				}
				else if("rename" == result){
					tip.css("color","red");
					tip.html("数据字典添加失败！类型代码不能重复，请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
			});
	}
	
	
	
});
$("#addDicsubExeBtn").click(function(){
	dic = new Object();
	dic.typeCode = $.trim($("#typeDicSubCode").val());
	dic.typeName = $.trim($("#typeDicSubName").val());
	dic.valueId = 0;
	dic.valueName = $.trim($("#valueDicSubName").val());
	tip = $("#addDicSubtip");
	if(dic.typeCode == ""){
		$("#typeDicSubCode").focus();
		tip.css("color","red");
		tip.html("类型代码不能为空！");
	}else if(dic.typeName == ""){
		$("#typeDicSubName").focus();
		tip.css("color","red");
		tip.html("类型名称不能为空！");
	}else if(dic.valueName == ""){
		$("#valueDicSubName").focus();
		tip.css("color","red");
		tip.html("数据名称不能为空！");
	}else{
		//alert(dic.typeCode + "," +dic.typeName + "," + dic.valueName + "," + dic.valueId);
		$.ajax({
			url: '/backend/addDicSub.html',
			type: 'POST',
			data:{dic:JSON.stringify(dic)},
			dataType: 'html',
			timeout: 1000,
			error: function(){
				tip.css("color","red");
				tip.html("数据字典添加失败！请重试。");
			},
			success: function(result){
				if(result != "" && "success" == result){
					
					var str = "";
					str += "<li id=\"li999\">";
					str += "<div>类型代码:"+dic.typeCode+"</div>";
					str += "<div>类型名称:"+dic.typeName+"</div>";
					str += "<div>数据数值:<input type=\"text\"  disabled=\"disabled\"  onkeyup=\"this.value=this.value.replace(\/\\D\/g,'')\" onafterpaste=\"this.value=this.value.replace(\/\\D\/g,'')\" id=\"valueIdText"+dic.id+"\" value=\""+(maxValue+1)+"\"/></div>";
					str += "<div>数值名称:<input type=\"text\" id=\"valueNameText"+dic.id+"\" value=\""+dic.valueName+"\"/></div>";
					str += "<div class=\"editdiv\">";
					str += "新增数据成功。";
					str += "</div>";
					str += "<div id=\"dicTip"+dic.id+"\"></div>";
					str += "</li>";
					
					$("#dicListUL").append(str);
					tip.css("color","green");
					tip.html("数据字典添加成功。^_^。");
				}else if("failed" == result){
					tip.css("color","red");
					tip.html("数据字典添加失败！请重试。");
				}
				else if("rename" == result){
					tip.css("color","red");
					tip.html("数据字典添加失败！该类型代码下的数据ID不能重复，请重试。");
				}else if("nodata" == result){
					alert("对不起，没有任何数据需要处理！请重试。");
				}
			}
		});
	}
});