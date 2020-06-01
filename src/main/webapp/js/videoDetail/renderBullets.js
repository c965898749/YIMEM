//在右侧渲染弹幕
function renderBulletsList(){
    $(".danmuList").empty();
    $("#bulletCount").text(danmakuList.length);
    $.each(danmakuList,function (index,data) {
        let itemHtml = `
                   <li>
                        <div class="currentTime">`+data.currentTime+`</div>
                        <div class="msg">`+data.msg+`</div>
                        <div class="createTime" >`+data.createTime.slice(5,-3)+`</div>
                   </li>                    
        `;
        $(".danmuList").append(itemHtml);
        // console.log(itemHtml);
    })
}