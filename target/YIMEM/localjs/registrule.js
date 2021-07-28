/**
 * 
$(".agreerule").click(function(){
	if($(".agreerule").attr("checked") == 'checked')
		$(".nextbtn").removeAttr("disabled");
	else
		$(".nextbtn").attr("disabled","disabled");
});

$(".nextbtn").click(function(){
	window.location.href="/member/registmember.html";
}); 

 * */


$(".closebtn").click(function(){
	window.close();
});