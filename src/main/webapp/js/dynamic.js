$(function() {
	//地址点击添加弹出层
	$(".dynamic").mouseenter(function() {
		layer.open({
			type: 2,
			shadeClose: true,
			shade: 0,
			resize: false,
			scrollbar: false,
			offset: ['40px', '1000px'],
			area: ['400px', '400px'],
			content: 'dynamic.html',
//			btn: ['关闭'],
			yes: function() {
				layer.closeAll();
				// $(that).click();
			},
			zIndex: layer.zIndex //重点1
				,
			success: function(layero) {
				layer.setTop(layero); //重点2
			},
			end: function() {
				window.location.reload();
			}

		});
	})
	
//	$(".dynamic").mouseleave(function(){
//		layer.closeAll(); 
//	})
})