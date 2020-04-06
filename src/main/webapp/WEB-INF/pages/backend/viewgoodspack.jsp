<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/pages/common/head.jsp"%>
<div>
	<ul class="breadcrumb">
		<li><a href="#">后台管理</a> <span class="divider">/</span></li>
		<li><a href="/backend/viewgoodspack.html">查看商品套餐</a></li>
	</ul>
</div>
			<div class="row-fluid sortable">		
				<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-user"></i>查看商品套餐</h2>
					</div>
			<div class="box-content">
			<legend>查看商品套餐</legend>
			<form class="form-horizontal">
					  <div class="control-group">
					  <label class="control-label" for="typeahead">套餐名称: </label>
					  <div class="controls">
						<input type="text" id="v_goodsPackName" name="goodsPackName" value="${goodsPack.goodsPackName}" readonly="readonly"/>
					  </div>
					  </div>
					  <div class="control-group">
					  <label class="control-label" for="typeahead">套餐编号: </label>
					  <div class="controls">
						<input type="text" id="v_goodsPackCode" name="goodsPackCode" value="${goodsPack.goodsPackCode}" readonly="readonly"/>
					  </div>
					</div>
					<div class="control-group">
					  <label class="control-label" for="typeahead">套餐类型: </label>
					  <div class="controls">
					  <input id="v_typeName" type="text" value="${goodsPack.typeName}" name="typeName" readonly="readonly"/>
					  </div>
					 </div>
					 
					<div class="control-group">
						<label class="control-label" for="focusedInput">库存量：</label>
						<div class="controls">
						<input type="text" id="v_num" value="${goodsPack.num}" readonly="readonly"/>
						</div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">套餐总价: </label>
					  <div class="controls">
						<input type="text" id="v_totalPrice" name="totalPrice" value="${goodsPack.totalPrice}" readonly="readonly"/>
					  </div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">状态: </label>
					  <div class="controls">
					  <c:if test="${goodsPack.state == '1'}">
					 	上架
					  </c:if>
					  <c:if test="${goodsPack.state == '0'}">
					  	下架
					  </c:if>
					  </div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">相关商品: </label>
					  <div class="controls">
					 	 <c:if test="${goodsList != null}">
						  <c:forEach items="${goodsList}" var="goods">
							<ul>
								${goods.goodsName}&nbsp;&nbsp;&nbsp;&nbsp;
								${goods.goodsNum}&nbsp;
								${goods.unit}
							</ul>
						  </c:forEach>
						 </c:if>
						 <c:if test="${goodsList == null}">
						 暂无
						 </c:if>
					  </div>
					</div>
					        
					<div class="control-group">
					  <label class="control-label" for="textarea2">套餐说明:</label>
					  <div class="controls">
					    <div id="v_note" readonly="readonly" rows="3">${goodsPack.note}</div>
					  </div>
					</div>
					<div class="form-actions">
					  <button type="button" class="btn backbtn">返回</button>
					</div>
			</div>
		</div><!--/span-->
		</div><!--/row-->
                

	
<%@include file="/WEB-INF/pages/common/foot.jsp"%>
<script type="text/javascript" src="/statics/localjs/viewgoodspack.js"></script> 
