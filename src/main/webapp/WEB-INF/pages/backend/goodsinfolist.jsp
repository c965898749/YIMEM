<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/pages/common/head.jsp"%>
<div>
	<ul class="breadcrumb">
		<li><a href="#">后台管理</a> <span class="divider">/</span></li>
		<li><a href="/backend/goodsinfolist.html">商品管理</a></li>
	</ul>
</div>

			<div class="row-fluid sortable">		
				<div class="box span12">
					<div class="box-header well" data-original-title>
						<h2><i class="icon-user"></i> 商品列表</h2>
						<div class="box-icon">
							<span class="icon32 icon-color icon-add custom-setting addGoodsInfo"/>
						</div>
					</div>
					
					<div class="box-content">
						<form action="/backend/goodsinfolist.html" method="post">
							<div class="searcharea">
							商品名称:
							<input type="text" name="s_goodsName" value="${s_goodsName}" />
							状态：
							 <select name="s_state" style="width:100px;">
								<option value="" selected="selected">--请选择--</option>
								　　 <c:if test="${s_state == 1}">  
									　　<option value="1" selected="selected">上架</option>
										<option value="2">下架</option>
								　　 </c:if>  
								　　 <c:if test="${s_state == 2}">  
									　    <option value="2" selected="selected">下架</option>
										<option value="1">上架</option>
								 	 </c:if>
								　　  <c:if test="${s_state == null || s_state == ''}">  
									　    <option value="2">下架</option>
										<option value="1">上架</option>
								 	</c:if>
						 	</select>
							<button type="submit" class="btn btn-primary"><i class="icon-search icon-white"></i> 查询 </button>
						</div>
						</form>
					
						<table class="table table-striped table-bordered bootstrap-datatable datatable">
						  <thead>
							  <tr>
								  <th>商品名称</th>
								  <th>市场价(元)</th>
								  <th>优惠价(元)</th>
								  <th>库存量</th>
								  <th>状态(上架/下架)</th>
								  <th>最后更新时间</th>
								  <th>操作</th>
							  </tr>
						  </thead>   
						  <tbody>
						  <c:if test="${page.items != null}">
						  <c:forEach items="${page.items}" var="goodsInfo">
							<tr>
							
								<td class="center">${goodsInfo.goodsName}</td>
								<td class="center">${goodsInfo.marketPrice}</td>
								<td class="center">${goodsInfo.realPrice}</td>
								<td class="center">${goodsInfo.num}</td>
								<td class="center">
								<input type="checkbox" title="直接勾选修改状态，立即生效" data-rel="tooltip" class="modifystate" state="${goodsInfo.state}" goodsinfoid="${goodsInfo.id}" <c:if test="${goodsInfo.state == 1}">checked="true"</c:if>/>
								</td>
								<td class="center">
								<fmt:formatDate value="${goodsInfo.lastUpdateTime}" pattern="yyyy-MM-dd"/>
								</td>
								<td class="center">
									<a class="btn btn-success viewgoodsinfo" href="#" id="${goodsInfo.id}">
										<i class="icon-zoom-in icon-white"></i>  
										查看                                           
									</a>
									<a class="btn btn-info modifygoodsinfo" href="#" id="${goodsInfo.id}">
										<i class="icon-edit icon-white"></i>  
										修改                                            
									</a>
									<a class="btn btn-danger delgoodsinfo" href="#" id="${goodsInfo.id}" goodsName="${goodsInfo.goodsName}">
										<i class="icon-trash icon-white"></i> 
										删除
									</a>
								</td>
							</tr>
						  </c:forEach>
						 </c:if>
						  </tbody>
					  </table>   
					<div class="pagination pagination-centered">
					  <ul>
					   <c:choose>
					  	<c:when test="${page.page == 1}">
					  	<li class="active"><a href="javascript:void();" title="首页">首页</a></li>
					  	</c:when>
					  	<c:otherwise>
					  	<li><a href="/backend/goodsinfolist.html?currentpage=1&s_goodsName=${s_goodsName}&s_state=${s_state}" title="首页">首页</a></li>
					  	</c:otherwise>
					  </c:choose>
						
						<c:if test="${page.prevPages!=null}">
							<c:forEach items="${page.prevPages}" var="num">
								<li><a href="/backend/goodsinfolist.html?currentpage=${num}&s_goodsName=${s_goodsName}&s_state=${s_state}"
									class="number" title="${num}">${num}</a></li>
							</c:forEach>
						</c:if>
						<li class="active">
						  <a href="#" title="${page.page}">${page.page}</a>
						</li>
						<c:if test="${page.nextPages!=null}">
							<c:forEach items="${page.nextPages}" var="num">
								<li><a href="/backend/goodsinfolist.html?currentpage=${num}&s_goodsName=${s_goodsName}&s_state=${s_state}" title="${num}">
								${num} </a></li>
							</c:forEach>
						</c:if>
						<c:if test="${page.pageCount !=null}">
							<c:choose>
						  	<c:when test="${page.page == page.pageCount}">
						  	<li class="active"><a href="javascript:void();" title="尾页">尾页</a></li>
						  	</c:when>
						  	<c:otherwise>
						  	<li><a href="/backend/goodsinfolist.html?currentpage=${page.pageCount}&s_goodsName=${s_goodsName}&s_state=${s_state}" title="尾页">尾页</a></li>
						  	</c:otherwise>
						    </c:choose>
					    </c:if>
						<c:if test="${page.pageCount == null}">
						<li class="active"><a href="javascript:void();" title="尾页">尾页</a></li>
					  	</c:if>
					  </ul>
				  </div>
				</div>
			</div><!--/span-->
		</div><!--/row-->
		
		
		<div class="modal hide fade" id="addGoodsInfoDiv">
		<form action="/backend/addgoodsinfo.html" enctype="multipart/form-data" method="post" onsubmit="return addGoodsInfoFunction();">
			<div class="modal-header">
				<button type="button" class="close addgoodsinfocancel" data-dismiss="modal">×</button>
				<h3>添加商品信息</h3>
			</div>
			<div class="modal-body">
				<ul id="add_formtip"></ul>
				<ul class="topul">
					<li>
					  <label>商品名称：</label><input type="text" id="a_goodsName" name="goodsName" />
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>商品编号：</label><input type="text" id="a_goodsSN" name="goodsSN" />
					  <span style="color:red;font-weight: bold;">*</span>
					</li> 
					<li>
					  <label>市场价：</label>
					  <input type="text" id="a_marketPrice" name="marketPrice" onkeyup="if(this.value==this.value2)return;if(this.value.search(/^\d*(?:\.\d{0,2})?$/)==-1)this.value=(this.value2)?this.value2:'';else this.value2=this.value;"/>
		 			  <span style="color:red;font-weight: bold;">*</span>
					</li> 
					<li>
					  <label>优惠价：</label>
					  <input type="text" id="a_realPrice" name="realPrice" onkeyup="if(this.value==this.value2)return;if(this.value.search(/^\d*(?:\.\d{0,2})?$/)==-1)this.value=(this.value2)?this.value2:'';else this.value2=this.value;"/>
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>库存量：</label><input type="text" id="a_num" name="num" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"/>
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>单位：</label>
					   <input type="text" id="a_unit" name="unit"/><span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>状态：</label>
					  <input type="radio" id="a_state" name="state" checked="checked" value="1"/>上架
					  <input type="radio" id="a_state" name="state" value="2"/>下架
					</li>
					</ul>
					<div class="clear"></div>
					
					<ul class="downul">
					<li>
					<span>商品规格：</span> <br/>
					<textarea class="cleditor" id="a_goodsFormat" name="goodsFormat" rows="3"></textarea>
					</li>
					</ul>
					<ul class="downul">
					<li>
					<span>商品说明：</span> <br/>
					<textarea class="cleditor" id="a_note" name="note" rows="3"></textarea>
					</li>
					</ul>
			</div>
			<div class="modal-footer">
				<a href="#" class="btn addgoodsinfocancel" data-dismiss="modal">取消</a>
				<input type="submit"  class="btn btn-primary" value="保存" />
			</div>
		</form>
	 </div>
		
	<div class="modal hide fade" id="modifyGoodsInfoDiv">
		<form action="/backend/modifygoodsinfo.html" enctype="multipart/form-data" method="post" onsubmit="return modifyGoodsInfoFunction();">
			<div class="modal-header">
				<button type="button" class="close modifygoodsinfocancel" data-dismiss="modal">×</button>
				<h3>修改商品信息</h3>
			</div>
			<div class="modal-body">
				<ul id="modify_formtip"></ul>
				<input id="m_id" type="hidden" name="id"/>
				<ul class="topul">
					<li>
					  <label>商品名称：</label><input type="text" id="m_goodsName" name="goodsName" />
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>商品编号：</label><input type="text" id="m_goodsSN" name="goodsSN" />
					  <span style="color:red;font-weight: bold;">*</span>
					</li> 
					<li>
					  <label>市场价：</label><input type="text" id="m_marketPrice" name="marketPrice" onkeyup="if(this.value==this.value2)return;if(this.value.search(/^\d*(?:\.\d{0,2})?$/)==-1)this.value=(this.value2)?this.value2:'';else this.value2=this.value;"/>
		 			  <span style="color:red;font-weight: bold;">*</span>
					</li> 
					<li>
					  <label>优惠价：</label>
					  <input type="text" id="m_realPrice" name="realPrice" onkeyup="if(this.value==this.value2)return;if(this.value.search(/^\d*(?:\.\d{0,2})?$/)==-1)this.value=(this.value2)?this.value2:'';else this.value2=this.value;"/>
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>库存量：</label><input type="text" id="m_num" name="num" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"/>
					  <span style="color:red;font-weight: bold;">*</span>
					</li>
					<li>
					  <label>单位：</label>
					   <input type="text" id="m_unit" name="unit"/><span style="color:red;font-weight: bold;">*</span>
					</li>
					</ul>
					<div class="clear"></div>
					<ul class="downul">
					<li id="m_state"></li>
					<li id="m_goodsFormatli">
					</li>
					</ul>
					<ul class="downul">
					<li id="m_noteli">
					</li>
					</ul>
			</div>
			
			<div class="modal-footer">
				<a href="#" class="btn modifygoodsinfocancel" data-dismiss="modal">取消</a>
				<input type="submit"  class="btn btn-primary" value="保存" />
			</div>
		</form>
	 </div>
		
	<div class="modal hide fade" id="viewGoodsInfoDiv">
			<div class="modal-header">
				<button type="button" class="close viewgoodsinfocancel" data-dismiss="modal">×</button>
				<h3>查看商品信息</h3>
			</div>
			<div class="modal-body">
				<ul class="topul">
					<li>
					  <label>商品名称：</label><input type="text" id="v_goodsName" name="goodsName" readonly="readonly"/>
					</li>
					<li>
					  <label>商品编号：</label><input type="text" id="v_goodsSN" name="goodsSN" readonly="readonly"/>
					</li> 
					<li>
					  <label>市场价：</label><input type="text" id="v_marketPrice" name="marketPrice" readonly="readonly"/>
					</li> 
					<li>
					  <label>优惠价：</label>
					  <input type="text" id="v_realPrice" name="realPrice" readonly="readonly"/>
					</li>
					<li>
					  <label>库存量：</label><input type="text" id="v_num" name="num" readonly="readonly"/>
					</li>
					<li>
					  <label>单位：</label>
					   <input type="text" id="v_unit" name="unit" readonly="readonly"/>
					</li>
					<li>
					  <label>状态：</label>
					  <span id="v_state"></span>
					</li>
				</ul>
				<div class="clear"></div>
					<ul class="downul">
					<li>
					<span>商品规格：</span> <br/><div id="v_goodsFormat" readonly="readonly" rows="3"></div>
					</li>
				    </ul>
					<ul class="downul">
					<li>
					<span>商品说明：</span> <br/><div id="v_note" readonly="readonly" rows="3"></div>
					</li>
				    </ul>
			</div>
			<div class="modal-footer">
				<a href="#" class="btn viewgoodsinfocancel" data-dismiss="modal">关闭</a>
			</div>
	 </div>
	
<%@include file="/WEB-INF/pages/common/foot.jsp"%>
<script type="text/javascript" src="/statics/localjs/goodsinfolist.js"></script> 
