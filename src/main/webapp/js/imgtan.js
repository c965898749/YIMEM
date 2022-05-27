
$(document).on("click","img",function(){
    $(".seeImg_list li").remove();
    var seeImglistLiLeng = $(this).length;
    $(".seeImg_popup").attr("data",seeImglistLiLeng)
    var arrtimg = [];
    $(this).each(function(i){
        var imgse = $(this).attr("src");
        arrtimg.push(imgse)
    });
    for( var i in arrtimg){
        var html ="";
        html +="<li><img src=" +arrtimg[i]+ " class='enlarge' draggable='false' onmousewheel='return bbimg(this)'  /></li>";
        $(".seeImg_list").append(html)
    }
    var seeImgswriebWight = $(".seeImg_swrieb").width();
    var seeImglistWigth = seeImgswriebWight * seeImglistLiLeng;
    console.log(seeImglistWigth)
    $(".seeImg_mask").show();
    $(".seeImg_popup").css("bottom","10%");
    $(".seeImg_list").width(seeImglistWigth)
    $(".seeImg_list").css("margin-left","0px")
    $(".seeImg_list li").width(seeImgswriebWight)
    $(".seeImg_list").children("li").each(function(i){
        $(this).children("img").attr("data",i)
        $(this).children("img").addClass("maxdom"+i)
    })
})
$(document).on("click",".seeImg_close",function(){
    $(".seeImg_mask").hide();
    $(".seeImg_popup").css("bottom","200%");
})
var UlMarRig = 0;
$(document).on("click",".labelRight",function(){
    var roue = $(".seeImg_swrieb").width();
    var mes = $(".seeImg_popup").attr("data");
    var csdx = roue * mes;
    console.log(csdx)
    UlMarRig = UlMarRig - roue;
    if( UlMarRig <= -csdx){
        UlMarRig = 0;
    }
    $(".seeImg_list").css("margin-left", UlMarRig)
})
$(document).on("click",".labelLeft",function(){
    var emgd = $(".seeImg_swrieb").width();
    var aas = $(".seeImg_popup").attr("data");
    var ddx = emgd * aas;
    if( UlMarRig == 0){
        UlMarRig = -ddx;
    }
    UlMarRig = UlMarRig + emgd;
    $(".seeImg_list").css("margin-left", UlMarRig)
})
var ment = 90;
function bbimg(o){
    var zoom=parseInt(o.style.zoom, 10)||100;
    zoom+=event.wheelDelta/12;
    console.log(zoom)
    if(zoom>=110){
        ment = ment +10;
        o.style.height = ment +'%';
    }
    if(zoom<110){
        ment = ment -10;
        o.style.height = ment +'%';
    }
    // console.log(ment)
}

$(document).on("mousedown",".enlarge",function(e){
    var u = $(this).attr("data");
    // e.pageX
    var imtLeft = $(this).position().left;
    var imtTop = $(this).position().top;
    var distenceX = e.pageX - imtLeft;     //记录鼠标点击的位置与div左上角水平方向的距离
    var distenceY = e.pageY - imtTop;     //记录鼠标点击的位置与div左上角数值方向的距离
    $(document).mousemove(function(e){
        var x = e.pageX - distenceX;
        var y = e.pageY - distenceY;
        if(x<0){
            x= x ;
        }else if(x>$(document).width()-$('.maxdom'+u).outerWidth(true)){
            x = $(document).width()-$('.maxdom'+u).outerWidth(true);
        }
        if(y<0){
            y= y;
        }else if(y>$(document).height()-$('.maxdom'+u).outerHeight(true)){
            y = $(document).height()-$('.maxdom'+u).outerHeight(true);
        }
        $('.maxdom'+u).css({'left':x+'px','top':y+'px'});
    });

    $(document).mouseup(function(){
        $(document).off('mousemove'); //移除鼠标移动事件
    });

})


