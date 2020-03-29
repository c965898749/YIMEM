

var ur=location.href;
var type=ur.split('?')[1];
$(function() {
	$(".left-nav").hide()
	$(".shousuo").css("left","0px");
	$("#homeDetails").click(function() {
		location.href = "forum.html"
		$(".NAV-myresouces").hide();
		$(".NAV-upload").hide()
		$(".NAV").show()
		$("#home").css("border-bottom", "2px red solid")
		$("#myresources").css("border-bottom", "none")
		$("#upload").css("border-bottom", "none")
	})
	$("#uploadDetails").click(function() {
		location.href = "forum.html"
		$(".NAV-upload").show();
		$(".NAV-myresouces").hide();
		$(".NAV").hide()
		$("#home").css("border-bottom", "none")
		$("#myresources").css("border-bottom", "none")
		$("#upload").css("border-bottom", "2px red solid")
	})

	$(".uploadbtn").click(function() {
		$(".NAV-upload").show();
		$(".NAV-myresouces").hide();
		$(".NAV").hide()
		$("#home").css("border-bottom", "none")
		$("#myresources").css("border-bottom", "none")
		$("#upload").css("border-bottom", "2px red solid")
	})
	$("#uploadcld").click(function() {
		$("#uploadcld").css("background-color", "white")
		$("#uploadcld").siblings().css("background-color", "")
		$(".myresouces").show()
		$(".myintegrals").hide()
	})

	$("#integralcld").click(function() {
		$("#integralcld").css("background-color", "white")
		$("#integralcld").siblings().css("background-color", "")
		$(".myintegrals").show()
		$(".myresouces").hide()
	})
	$("#downcld").click(function() {
		$("#downcld").css("background-color", "white")
		$("#downcld").siblings().css("background-color", "")
		$(".myresouces").show()
		$(".myintegrals").hide()
	})
	$("#collentcld").click(function() {
		$("#collentcld").css("background-color", "white")
		$("#collentcld").siblings().css("background-color", "")
		$(".myresouces").show()
		$(".myintegrals").hide()
	})
	$("#allupload").click(function() {
		$("#allupload").css("background-color", "red")
		$("#allupload").css("color", "white")
		$("#allupload").siblings().css("background-color", "")
		$("#allupload").siblings().css("color", "black")
	})
	$("#waitpassupload").click(function() {
		$("#waitpassupload").css("background-color", "red")
		$("#waitpassupload").css("color", "white")
		$("#waitpassupload").siblings().css("background-color", "")
		$("#waitpassupload").siblings().css("color", "black")
	})
	$("#passupload").click(function() {
		$("#passupload").css("background-color", "red")
		$("#passupload").css("color", "white")
		$("#passupload").siblings().css("background-color", "")
		$("#passupload").siblings().css("color", "black")
	})
	$("#nopassupload").click(function() {
		$("#nopassupload").css("background-color", "red")
		$("#nopassupload").css("color", "white")
		$("#nopassupload").siblings().css("background-color", "")
		$("#nopassupload").siblings().css("color", "black")
	})
	$(".memberbtn").click(function() {
		location.href = "vip.html"
	})
	$(".shousuo").click(function() {
		if($(".left-nav").is(':hidden')){
			$(".left-nav").show()
			$(".shousuo").css("left","200px");
		}else {
			$(".left-nav").hide()
			$(".shousuo").css("left","0px");
		}

	})


})
