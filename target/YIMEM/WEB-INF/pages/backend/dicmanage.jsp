<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/pages/common/head.jsp"%>

<div>
	<ul class="breadcrumb">
		<li><a href="#">后台管理</a> <span class="divider">/</span></li>
		<li><a href="/backend/dicmanage.html">数据字典管理</a></li>
	</ul>
</div>


			<div class="row-fluid sortable">		
				<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-user"></i> 数据字典管理 </h2>
					</div>
					
					<div class="box-content">
						<table class="table table-striped table-bordered bootstrap-datatable datatable">
						  <tbody>
						  <tr>
						  <td width="160px;">
						  <ul class="dllist">
						  <li>
						  		<h3>字典类型</h3>
						  </li>
						  <li class="addDicBtn">
						  		<img src="/statics/img/ico7.png"/>
						  </li>
						  <c:forEach items="${dataList}" var="dl">
						  	<li class="maintitle">
						  	
						  		<a class="typecodelist" typename="${dl.typeName}" typecode="${dl.typeCode}" dlid="${dl.id}">
						  		${dl.typeName}
						  		</a>
						  		<span class="mainset">
									<img class="modifyMainDic" dictypename="${dl.typeName}" dicid="${dl.id}" dictypecode="${dl.typeCode}" src="/statics/img/ico10.png"> <img class="delMainDic" dictypecode="${dl.typeCode}" dictypename="${dl.typeName}" src="/statics/img/linkdel.png">
								</span>
						  	</li>
						  </c:forEach>
						  <li class="addDicBtn">
						  		<img src="/statics/img/ico7.png"/>
						  </li>
						  </ul>
						  </td>
						  <td>
						  
						   <h3 id="optitle"></h3>
						  <div class="dicListContent">
						  	<ul id="dicListUL">
						  	<!-- 
						  		<li>
									<div>类型代码:</div>
									<div>类型名称:</div>
									<div>数据数值:<input type="text" id=""/></div>
									<div>数值名称:<input type="text" id=""/></div>
									<div class="editdiv">
										<img src="/statics/img/ico10.png"> <img src="/statics/img/linkdel.png">
									</div>
								</li>
							-->
						  	</ul>
						  	<ul id="addsubdicul">
						  		<li id="addDicLiBtn" class="addDicLiBtn"><img src="/statics/img/winapp_add.png"/></li>
						  	</ul>
						  </div>
						  
						  
						  </td>
						  <tr>
						  </tbody>
					  </table>   
				</div>
			</div><!--/span-->
		</div><!--/row-->

			<div class="modal hide fade" id="addDicModel">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">×</button>
						<h3>添加数据字典</h3>
					</div>
					<div class="modal-body">
						<p>
								<label>类型代码：</label>
								  <input id="typeCode"  type="text">
								<label>类型名称：</label>
								  <input id="typeName"  type="text">
								  <!-- 
								<label>数据名称：</label>
								  <input id="valueName"  type="text">
								   -->
						</p>
						<p id="addDictip">
						</p>
					</div>
					<div class="modal-footer">
						<a href="#" class="btn" data-dismiss="modal">取消</a>
						<a href="#" id="addDicExeBtn" class="btn btn-primary">添加</a>
					</div>
				</div>
			<div class="modal hide fade" id="addDicSubModel">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">×</button>
						<h3>添加数据字典</h3>
					</div>
					<div class="modal-body">
						<p>
								<label>类型代码：</label>
								  <input id="typeDicSubCode" disabled="disabled"  type="text">
								<label>类型名称：</label>
								  <input id="typeDicSubName" disabled="disabled"  type="text">
								<label>数据名称：</label>
								  <input id="valueDicSubName"  type="text">
						</p>
						<p id="addDicSubtip"></p>
					</div>
					<div class="modal-footer">
						<a href="#" class="btn" data-dismiss="modal">取消</a>
						<a href="#" id="addDicsubExeBtn" class="btn btn-primary">添加</a>
					</div>
				</div>
			<div class="modal hide fade" id="modifyDicModel">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">×</button>
						<h3>修改数据字典</h3>
					</div>
					<div class="modal-body">
						<p>
								<label>类型代码：</label>
								  <input id="modifytypeCode"  type="text">
								<label>类型名称：</label>
								  <input id="modifytypeName"  type="text">
								  <input id="modifydicid"  type="hidden">
								  <input id="modifydictypecode"  type="hidden">
								  <input id="modifydictypename"  type="hidden">
						</p>
						<p id="modifyDictip"></p>
					</div>
					<div class="modal-footer">
						<a href="#" class="btn" data-dismiss="modal">取消</a>
						<a href="#" id="modifyDicExeBtn" class="btn btn-primary">修改</a>
					</div>
				</div>


<%@include file="/WEB-INF/pages/common/foot.jsp"%>
<script type="text/javascript" src="/statics/localjs/dicmanage.js"></script>