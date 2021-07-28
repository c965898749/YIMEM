<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/pages/common/head.jsp"%>
<div>
	<ul class="breadcrumb">
		<li><a href="#">后台管理</a> <span class="divider">/</span></li>
		<li><a href="/backend/modifygoodspack.html">修改商品套餐</a></li>
	</ul>
</div>
			<div class="row-fluid sortable">		
				<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-user"></i>修改商品套餐</h2>
					</div>
			<div class="box-content">
			<ul id="modify_formtip"></ul>
			<legend>修改商品套餐</legend>
                 <form action="/backend/savemodifygoodspack.html" class="form-horizontal" enctype="multipart/form-data" method="post" onsubmit="return modifyGoodsPackFunc();">
					  <div class="control-group">
					  <input id="m_id" type="hidden" name="id" value="${goodsPack.id}"/>
					  <label class="control-label" for="typeahead">套餐名称: </label>
					  <div class="controls">
						<input type="text" id="m_goodsPackName" name="goodsPackName" value="${goodsPack.goodsPackName}" />
						 <span style="color:red;font-weight: bold;">*</span>
					  </div>
					  </div>
					  <div class="control-group">
					  <label class="control-label" for="typeahead">套餐编号: </label>
					  <div class="controls">
						<input type="text" id="m_goodsPackCode" name="goodsPackCode" value="${goodsPack.goodsPackCode}" />
						 <span style="color:red;font-weight: bold;">*</span>
					  </div>
					</div>
					<div class="control-group">
					  <label class="control-label" for="typeahead">套餐类型: </label>
					  <div class="controls">
					  <input id="m_typeName" type="hidden" value="${goodsPack.typeName}" name="typeName"/>
					  <select id="m_typeId" name="typeId" style="width:100px;">
			 			<option value="" selected="selected">--请选择--</option>
			 			<c:if test="${packTypeList != null}">
					 				<c:forEach items="${packTypeList}" var="packType">
					 					<option <c:if test="${goodsPack.typeId == packType.valueId}">selected = "selected"</c:if>
					 					value="${packType.valueId}">${packType.valueName}</option>
					 				</c:forEach>
					 	</c:if>
			 		  </select>
			 		   <span style="color:red;font-weight: bold;">*</span>
					  </div>
					 </div>
					 
					<div class="control-group">
						<label class="control-label" for="focusedInput">库存量：</label>
						<div class="controls">
						<input type="text" id="m_num" name="num" value="${goodsPack.num}" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"/>
						<span style="color:red;font-weight: bold;">*</span>
						</div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">套餐总价: </label>
					  <div class="controls">
						<input type="text" id="m_totalPrice" name="totalPrice" value="${goodsPack.totalPrice}" onkeyup="if(this.value==this.value2)return;if(this.value.search(/^\d*(?:\.\d{0,2})?$/)==-1)this.value=(this.value2)?this.value2:'';else this.value2=this.value;"/>
						<span style="color:red;font-weight: bold;">*</span>
					  </div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">状态: </label>
					  <div class="controls">
					  <c:if test="${goodsPack.state == '1'}">
					 	<input type="radio" name="state" checked="checked" value="1"/>上架
					  	<input type="radio" name="state" value="0"/>下架
					  </c:if>
					  <c:if test="${goodsPack.state == '0'}">
					 	<input type="radio" name="state" value="1"/>上架
					  	<input type="radio" name="state" checked="checked" value="0"/>下架
					  </c:if>
					  </div>
					</div>
					
					<div class="control-group">
					  <label class="control-label" for="typeahead">相关商品: </label>
					  <div class="controls">
						<ul class="aboutproductsList">
						  	<li><iframe id="goodsListFrame" class="goodsListFrame" src="/backend/goodslist.html"></iframe></li>
						  	<li id="selectgoodslist">
						  		<c:if test="${goodsList != null}">
					 				<c:forEach items="${goodsList}" var="goods">
					 				<div id="selectdiv">
					 				<label class="goodsname">${goods.goodsName}</label>
									<label class="goodscount"><input class="finalresult" goodsid="${goods.goodsInfoId}" rprice="${goods.realPrice}" type="text" value="${goods.goodsNum}"/></label>
									<label class="del" rprice="${goods.realPrice}"><img src="/statics/img/cancel-on.png"/></label>
									<label class="clear"></label>
									</div>
					 				</c:forEach>
					 	    	</c:if>
						  	</li>
					  	</ul>
					  	<input id="goodsJson" type="hidden" name="goodsJson"/>
					  </div>
					</div>
					        
					<div class="control-group">
					  <label class="control-label" for="textarea2">套餐说明:</label>
					  <div class="controls">
					    <textarea class="cleditor" id="m_note" name="note" rows="3">${goodsPack.note}</textarea>
					  </div>
					</div>
					<div class="form-actions">
					  <button type="submit" class="btn btn-primary">保存</button>
					  <button type="button" class="btn backbtn">返回</button>
					</div>
				</form>
			</div>
		</div><!--/span-->
		</div><!--/row-->
                
<%@include file="/WEB-INF/pages/common/foot.jsp"%>
<script type="text/javascript" src="/statics/localjs/modifygoodspack.js"></script> 
