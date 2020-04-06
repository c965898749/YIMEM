<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/pages/common/head.jsp"%>

<div>
	<ul class="breadcrumb">
		<li><a href="#">后台管理</a> <span class="divider">/</span></li>
		<li><a href="/backend/authoritymanage.html">权限管理</a></li>
	</ul>
</div>

			<div class="row-fluid sortable">		
				<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-user"></i> 权限管理 </h2>
					</div>
					
					<div class="box-content">
						<table class="table table-striped table-bordered bootstrap-datatable datatable">
						  <tbody>
						  <tr>
						  <td width="200px;">
						  <ul class="rolelistul">
						  <c:forEach items="${roleList}" var="role">
						  	<li>
						  		<a class="roleNameAuthority" rolename="${role.roleName}" roleid="${role.id}">${role.roleName}</a>
						  	</li>
						  </c:forEach>
						  </ul>
						  </td>
						  <td>
						  <h3 id="selectrole"></h3>
						  <input type="hidden" id="roleidhide" value=""/>
						    <p class="btn-group">
						    	<button class="btn" id="selectAll">全选</button>
							  	<button class="btn" id="unSelect">全不选</button>
							  	<button class="btn" id="reverse">反选</button>
							 </p>
						  	<ul id="functionList"></ul>
						  	<div class="clear"></div>
						  	<p class="center">
								<a id="confirmsave" class="btn btn-large btn-primary"><i class="icon-chevron-left icon-white"></i> 确定赋予权限</a> 
							</p>
						  </td>
						  <tr>
						  </tbody>
					  </table>   
				</div>
			</div><!--/span-->
		</div><!--/row-->




	<div class="modal hide fade" id="addRoleDiv">
			<div class="modal-header">
				<button type="button" id="addRoleClose" class="close" data-dismiss="modal">×</button>
				<h3>添加角色信息</h3>
			</div>
			<div class="modal-body">
				
				<ul class="topul">
					<li>
					  <label>角色代码：</label><input type="text" id="roleCode" />
					</li>
					<li>
					  <label>角色名称：</label><input type="text" id="roleName" />
					</li>
					<li id="formtip"></li>
				</ul>
				<div class="clear"></div>
			</div>
			
			<div class="modal-footer">
				<a href="#" class="btn" id="addRoleCancel" data-dismiss="modal">取消</a>
				<input type="button" id="addRoleBtn" class="btn btn-primary" value="保存" />
			</div>
		</form>
	 </div>
<%@include file="/WEB-INF/pages/common/foot.jsp"%>

<script type="text/javascript" src="/statics/localjs/authoritymanage.js"></script>