$('#m_note').cleditor({ width:730,height:300});

$('.backbtn').click(function(e){
	window.location.href="/backend/goodspacklist.html";
});

$("#m_typeId").change(function(){
	$("#m_typeName").val($("#m_typeId").find("option:selected").text()) ;
});

function addGoods(id,goodsname,rprice){
//	alert(id+"_"+ goodsname +"_"+ rprice+" _");
	var ok = true;
	$(".goodsname").each(function () {  
		title = $(this).html();
        if(goodsname == title){
        	ok = false;
        	return false;
        }
    });  
	if(ok){
		str = "<div id=\"selectdiv\"><label class=\"goodsname\">"+goodsname+"</label>"+
				"<label class=\"goodscount\"><input class=\"finalresult\" goodsid=\""+id+"\" rprice=\""+rprice+"\" type=\"text\" value=\"1\"/></label>"+
						"<label class=\"del\" rprice=\""+rprice+"\"><img src=\"/statics/img/cancel-on.png\"/></label>"+
						"<label class=\"clear\"></label></div>";
		$("#selectgoodslist").append(str);
		var gcount = $(".finalresult").val();
		$(".del").val(gcount);
		var tprice = rprice*gcount;
		var tempprice = $("#m_totalPrice").val();
		$("#m_totalPrice").val(tprice);
		if(tempprice == "" || tempprice == null) tempprice =0;
		tprice = parseInt(tprice) + parseInt(tempprice);
		console.log("tprice------ "+tprice);
		$("#m_totalPrice").val(tprice);
		$('.del').click(function(e){
			$(this).parents("#selectdiv").remove();
			$("#selectgoodslist").change();
		});
	}
}

$('.del').click(function(e){
	$(this).parents("#selectdiv").remove();
	$("#selectgoodslist").change();
});

$("#selectgoodslist").change(function(){
	var totleprice = 0;
	$(".finalresult").each(function () {  
		id = $(this).attr("goodsid");
		rprice = $(this).attr("rprice");
		gcount = $(this).val();
		totleprice = parseInt(totleprice) + parseInt(rprice*gcount);
    });  
	$("#m_totalPrice").val(totleprice);
});

$("#m_goodsPackCode").blur(function(){
	var mgpc = $.trim($("#m_goodsPackCode").val());
	if(mgpc != ""){
		$.post("/backend/goodspackcodeisexit.html",{'goodsPackCode':mgpc,'id':$("#m_id").val()},function(result){
			if(result == "repeat"){
				$("#modify_formtip").css("color","red");
				$("#modify_formtip").html("<li>对不起，该套餐编码已存在。</li>");
				$("#modify_formtip").attr("key","1");;
				result = false;
			}else if(result == "failed"){
				alert("操作超时!");
			}else if(result == "only"){
				$("#modify_formtip").css("color","green");
				$("#modify_formtip").html("<li>该套餐编码可以正常使用。</li>");
				$("#modify_formtip").attr("key","0");
			}
		},'html');
	}
});

function modifyGoodsPackFunc(){
	$("#modify_formtip").html("");
	var result = true;
	json = "[";
	$(".finalresult").each(function () {  
		id = $(this).attr("goodsid");
		gcount = $(this).val();
		json = json+"{";
		json = json+"\"goodsInfoId\":\""+id+"\",\"goodsNum\":\""+gcount+"\"";
		json = json+"},";
    });  
	json = json+"{";
	json = json+"\"goodsInfoId\":\"0\",\"goodsNum\":\"0\"";
	json = json+"}";
	json = json + "]";
	$("#goodsJson").val(json);
	
	if( $.trim($("#m_goodsPackName").val()) == "" || $("#m_goodsPackName").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，套餐名称不能为空。</li>");
		result = false;
	}
	if( $.trim($("#m_goodsPackCode").val()) == "" || $("#m_goodsPackCode").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，套餐编码不能为空。</li>");
		result = false;
	}else{
		if($("#modify_formtip").attr("key") == "1"){
			$("#modify_formtip").append("<li>对不起，该套餐编码已存在。</li>");
			result = false;
		}
	}
	if($("#m_typeId").val() == ""){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，套餐类型不能为空。</li>");
		result = false;
	}
	if( $.trim($("#m_num").val()) == "" || $("#m_num").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，套餐库存量不能为空。</li>");
		result = false;
	}
	if( $.trim($("#m_totalPrice").val()) == "" || $("#m_totalPrice").val() == null){
		$("#modify_formtip").css("color","red");
		$("#modify_formtip").append("<li>对不起，套餐总价不能为空。</li>");
		result = false;
	}
	
	return result;
}