jQuery.fn.FileInput = function(){
	$(this).each(function(){
		var input = $(this),
			html = "";
		if(input.is('[type=file]')){
			html += '<input type="text" class="filename" value="未选择文件" readonly/>';
			html += '<input type="button" name="file" class="button" value="浏览"/>';
			input.wrap('<div class="uploader blue"></div>').before(html);
			input.change(function(){
				var filename = $(this).val();
				if(filename != ""){
					$(this).parent('.uploader').find('.filename').val(filename);
				}else{
					$(this).parent('.uploader').find('.filename').val('未选择文件');
				}				
			});
		}
	});
};

jQuery.fn.placeholder = function(){
	//判断浏览器是否支持placeholder属性
	var supportPlaceholder = 'placeholder' in document.createElement('input');
	if(!supportPlaceholder){
		$(this).each(function(){
			var input = $(this);
			if(input.is('[type=text]')){
				var defaultText = input.attr("placeholder"),
					text = input.val();
				
				if(defaultText){					
					input.focus(function(){
						if($(this).val() == defaultText){ 
							$(this).val("").css("color","");
						}
					})
					.blur(function(){
						if(input.val() == ""){
							$(this).val(defaultText).css("color","#999");
						}
					});
					if(!text){
						input.val(defaultText).css("color","#999");
					}
				}
				
			}
		});
	}	
};

//显示或隐藏密码框
jQuery.fn.showpassword = function(){
	$(this).each(function(){
		var password = $(this),
			password_show = $(this).clone().attr("type","text");
			eye_box = "<div class='eye-open eye-box' type='button'  data-show='false'></div>";
		password.after(password_show);
		password_show.after(eye_box);
		password_show.next(".eye-box").bind("showAndHide",function(){
			if($(this).attr("data-show") == "true"){
				password.hide();
				$(this).removeClass("eye-open");
				$(this).addClass("eye-close");
				password_show.show();
			}else{
				password.show();
				$(this).removeClass("eye-close");
				$(this).addClass("eye-open");
				password_show.hide();
			}
		})
		.trigger("showAndHide")
		.click(function(){
			if($(this).attr("data-show") == "true"){
				$(this).attr("data-show","false");
				password.val(password_show.val());
				
			}else{
				$(this).attr("data-show","true");
				password_show.val(password.val());
			}
			$(this).trigger("showAndHide");
		});
		password_show.on('input propertychange',function(){
			password.val(password_show.val());
		});
	});	
};