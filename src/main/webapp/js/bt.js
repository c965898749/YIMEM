$(function() {
	$(".newManuLis").hide()
	$(".newState").css("color", "#94D6EF")
	$(".originalCartoonlis").hide()
	$(".allCartoon").css("color", "#94D6EF")
	var ConLis = ["A", "B", "C", "D", "E", "F"];
	$.each(ConLis, function(i, v) {
		$(".Con" + v).on("click", ".newState", function() {
			console.log(this)
			$(".Con" + v).find(".newStateLis").show()
			$(".Con" + v).find(".newManuLis").hide()
			$(".Con" + v).find(".newState").css("color", "#94D6EF")
			$(".Con" + v).find(".newManu").css("color", "#6D757A")
		})

		$(".Con" + v).on("click", ".newManu", function() {
			console.log(v)
			$(".Con" + v).find(".newStateLis").hide()
			$(".Con" + v).find(".newManuLis").show()
			$(".Con" + v).find(".newState").css("color", "#6D757A")
			$(".Con" + v).find(".newManu").css("color", "#94D6EF")
		})

		$(".Con" + v).on("click", ".allCartoon", function() {
			$(".Con" + v).find(".allCartoonlis").show()
			$(".Con" + v).find(".originalCartoonlis").hide()
			$(".Con" + v).find(".allCartoon").css("color", "#94D6EF")
			$(".Con" + v).find(".originalCartoon").css("color", "#6D757A")
		})

		$(".Con" + v).on("click", ".originalCartoon", function() {
			$(".Con" + v).find(".allCartoonlis").hide()
			$(".Con" + v).find(".originalCartoonlis").show()
			$(".Con" + v).find(".allCartoon").css("color", "#6D757A")
			$(".Con" + v).find(".originalCartoon").css("color", "#94D6EF")
		})

	});

	$(window).scroll(function() {
		//为页面添加页面滚动监听事件
		var wst = $(window).scrollTop() + 100 //滚动条距离顶端值
		for(i = 1; i < 7; i++) { //加循环
			if($("#a" + i).offset().top <= wst) { //判断滚动条位置
				$('#nav a').removeClass("c"); //清除c类
				$("#a" + i + i).addClass("c"); //给当前导航加c类
			}
		}

	})
	$('#nav a').click(function() {
		$('#nav a').removeClass("c");
		$(this).addClass("c");
	});
	//稍后再看main-lf
	$(".left-main").on("click", ".laterBt", function() {
		var bp = $(this).css("background-position");
		console.log(111)
		if(bp == "-1365px -880px") {
			$(this).css("background-position", "-1435px -880px")
		} else {
			$(this).css("background-position", "-1365px -880px")
		}
	})
	$(".main").on("click", ".laterBt", function() {
		var bp = $(this).css("background-position");
		console.log(111)
		if(bp == "-1365px -880px") {
			$(this).css("background-position", "-1435px -880px")
		} else {
			$(this).css("background-position", "-1365px -880px")
		}
	})
	$(".home").on("click", ".laterBt", function() {
		var bp = $(this).css("background-position");
		console.log(111)
		if(bp == "-1365px -880px") {
			$(this).css("background-position", "-1435px -880px")
		} else {
			$(this).css("background-position", "-1365px -880px")
		}
	})
	var week = ["一", "二", "三", "四", "五", "六", "日"]
	$(".btft").find(".btft-p").text(week[6])
	$(".btrt").find(".btrt-p").text(week[0])
	$(".btft").on("click", function() {
		var bf = $(".btft").find(".btft-p").text()
		console.log(bf)
		$.each(week, function(i, v) {

			if(bf == v) {

				if(bf == "一") {
					$(".btft").find(".btft-p").text(week[6])
					$(".btrt").find(".btrt-p").text(week[0])
				} else {
					$(".btft").find(".btft-p").text(week[i - 1])
					$(".btrt").find(".btrt-p").text(week[i])
				}
			}
		})
	})

	$(".btrt").on("click", function() {
		var bf = $(".btrt").find(".btrt-p").text()
		console.log(bf)
		$.each(week, function(i, v) {

			if(bf == v) {

				if(bf == "日") {
					$(".btft").find(".btft-p").text(week[6])
					$(".btrt").find(".btrt-p").text(week[0])
				} else {
					$(".btft").find(".btft-p").text(week[i])
					$(".btrt").find(".btrt-p").text(week[i + 1])
				}
			}
		})
	})
})