<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style type="text/css">
body {
	font-size: 12px;
	padding: 0px;
	margin: 0px;
}

h2 {
	font-size: 18px;
	font-weight: bold;
	color: #006699;
	border-bottom: 2px solid #006699;
	padding: 3px;
}

.searcharea {
	padding: 3px;
}

ul {
	list-style: none;
	margin-left: -30px;
}

ul li {
	padding: 5px;
	height:30px;
	cursor: pointer;
}


ul li:hover{background: #efefef;border:1px dashed #ccc;}
label{float: left;}
.goodsname{display:block; width:180px;height:30px;overflow: hidden;}
.kucun{display:block;width:80px;height:30px;overflow: hidden;}
.add{display:block;width:30px;height:30px;vertical-align:middle;overflow: hidden;line-height: 30px;text-align: center;cursor: pointer;}
.clear{clear:both;}
h2 a{font-size:12px;color:green;}
</style>
<!-- jQuery -->
<script src="/statics/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(".add").click(function(){
		gid = $(this).attr("gid");
		gname = $(this).attr("gname");
		rprice = $(this).attr("rprice");
		window.parent.addGoods(gid,gname,rprice);
	});
});
</script>
</head>
<body>
	<h2>商品列表</h2>

	<form action="/backend/goodslist.html" method="post">
		<div class="searcharea">
			商品名称: <input type="text" name="s_goodsName" value="${s_goodsName}" />
			<input type="submit" value="查询" />
		</div>
	</form>


	<c:if test="${goodsInfoList != null}">
		<ul>
			<c:forEach items="${goodsInfoList}" var="goodsInfo">
				<li>
					<label class="goodsname">${goodsInfo.goodsName}</label>
					<label class="kucun">库存：${goodsInfo.num}</label>
					<label class="add" gid="${goodsInfo.id}" gname="${goodsInfo.goodsName}" rprice="${goodsInfo.realPrice}"><img src="/statics/img/+.png" width="24px;"/></label>
					<label class="clear"></label>
				</li>
			</c:forEach>
		</ul>
	</c:if>
</html>
