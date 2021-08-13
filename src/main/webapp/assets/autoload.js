/* 把 autoload.js 内容直接内置 */
$('body').append('<div class="waifu"><div class="waifu-tips"></div><canvas id="live2d" class="live2d"></canvas><div class="waifu-tool"><span class="fui-home"></span> <span class="fui-chat"></span> <span class="fui-eye"></span> <span class="fui-user"></span> <span class="fui-photo"></span> <span class="fui-info-circle"></span> <span class="fui-cross"></span></div></div>');
$.ajax({url: 'assets/waifu-tips.js?v=1.4.2',dataType:"script", cache: true, async: false});
$.ajax({url: 'assets/live2d.min.js?v=1.0.5',dataType:"script", cache: true, async: false});

/* 可直接修改部分参数 */
live2d_settings['hitokotoAPI'] = 'jinrishici.com'; // 一言 API
live2d_settings['modelId'] = 6;                    // 默认模型 ID
live2d_settings['modelTexturesId'] = 1;            // 默认材质 ID
live2d_settings['modelStorage'] = false;           // 不储存模型 ID
live2d_settings['canCloseLive2d'] = true;         // 隐藏 关闭看板娘 按钮
live2d_settings['canTurnToHomePage'] = true;      // 隐藏 返回首页 按钮
live2d_settings['waifuSize'] = '300x250';          // 看板娘大小
live2d_settings['waifuTipsSize'] = '300x70';      // 提示框大小
live2d_settings['waifuFontSize'] = '20px';         // 提示框字体
live2d_settings['waifuEdgeSide'] = 'right:100';     // 看板娘贴边方向
live2d_settings['waifuToolFont'] = '18px';         // 工具栏字体
live2d_settings['waifuToolLine'] = '30px';         // 工具栏行高
live2d_settings['waifuToolTop'] = '-20px';         // 工具栏顶部边距
live2d_settings['waifuDraggable'] = 'axis-x';      // 拖拽样式

/* 内置 waifu-tips.json */
initModel({
    "waifu": {
        "console_open_msg": ["哈哈，你打开了控制台，是想要看看我的秘密吗？"],
        "copy_message": ["你都复制了些什么呀，转载要记得加上出处哦"],
        "screenshot_message": ["照好了嘛，是不是很可爱呢？"],
        "hidden_message": ["我们还能再见面的吧…"],
        "load_rand_textures": ["我还没有其他衣服呢", "我的新衣服好看嘛"],
        "hour_tips": {
            "t5-7": ["早上好！一日之计在于晨，美好的一天就要开始了"],
            "t7-11": ["上午好！工作顺利嘛，不要久坐，多起来走动走动哦！"],
            "t11-14": ["中午了，工作了一个上午，现在是午餐时间！"],
            "t14-17": ["午后很容易犯困呢，今天的运动目标完成了吗？"],
            "t17-19": ["傍晚了！窗外夕阳的景色很美丽呢，最美不过夕阳红~"],
            "t19-21": ["晚上好，今天过得怎么样？"],
            "t21-23": ["已经这么晚了呀，早点休息吧，晚安~"],
            "t23-5": ["你是夜猫子呀？这么晚还不睡觉，明天起的来嘛"],
            "default": ["嗨~ 快来逗我玩吧！"]
        },
        "referrer_message": {
            "localhost": ["欢迎访问<span style=\"color:#0099cc;\">『", "』</span>", " - "],
            "baidu": ["Hello! 来自 百度搜索 的朋友<br>你是搜索 <span style=\"color:#0099cc;\">", "</span> 找到的我吗？"],
            "so": ["Hello! 来自 360搜索 的朋友<br>你是搜索 <span style=\"color:#0099cc;\">", "</span> 找到的我吗？"],
            "google": ["Hello! 来自 谷歌搜索 的朋友<br>欢迎阅读<span style=\"color:#0099cc;\">『", "』</span>", " - "],
            "default": ["Hello! 来自 <span style=\"color:#0099cc;\">", "</span> 的朋友"],
            "none": ["欢迎访问<span style=\"color:#0099cc;\">『", "』</span>", " - "]
        },
        "referrer_hostname": {
            "example.com": ["示例网站"],
            "www.fghrsh.net": ["FGHRSH 的博客"]
        },
        "model_message": {
            "1": ["来自 Potion Maker 的 Pio 酱 ~"],
            "2": ["来自 Potion Maker 的 Tia 酱 ~"]
        },
        "hitokoto_api_message": {
            "lwl12.com": ["这句一言来自 <span style=\"color:#0099cc;\">『{source}』</span>", "，是 <span style=\"color:#0099cc;\">{creator}</span> 投稿的", "。"],
            "fghrsh.net": ["这句一言出处是 <span style=\"color:#0099cc;\">『{source}』</span>，是 <span style=\"color:#0099cc;\">FGHRSH</span> 在 {date} 收藏的！"],
            "jinrishici.com": ["这句诗词出自 <span style=\"color:#0099cc;\">《{title}》</span>，是 {dynasty}诗人 {author} 创作的！"],
            "hitokoto.cn": ["这句一言来自 <span style=\"color:#0099cc;\">『{source}』</span>，是 <span style=\"color:#0099cc;\">{creator}</span> 在 hitokoto.cn 投稿的。"]
        }
    },
    "mouseover": [
        { "selector": ".container a[href^='http']", "text": ["要看看 <span style=\"color:#0099cc;\">{text}</span> 么？"] },
        { "selector": ".fui-home", "text": ["点击前往首页，想回到上一页可以使用浏览器的后退功能哦"] },
        { "selector": ".aplayer", "text": ["点播音乐放松心情吧"] },
        { "selector": "#searchtihing", "text": ["<(￣︶￣)>输入你想要查找的资源吧……哈哈哈……我可啥都懂"] },
        { "selector": "#TheadImg33", "text": ["你的头像好可爱呀……人家好喜欢呀"] },
        { "selector": ".TmsgContain", "text": ["看看谁给你来消息。"] },
        { "selector": ".TwriteBlogContain", "text": ["发表你感慨吧，我好想知道心里在想啥^_^"] },
        { "selector": ".recharge", "text": ["有了积分就能愉快的下资源了"] },
        { "selector": "#price", "text": ["积分可以到点击右上角头像，然后赞助我们快速获取"] },
        { "selector": "#blog_page", "text": ["去博客看看吧，说不定能遇上你的知音"] },
        { "selector": "#down_load", "text": ["去下载看看吧，说不定能有你需要的资源"] },
        { "selector": "#game_html", "text": ["我想玩，我想玩……ˋ( ° ▽、° )"] },
        { "selector": "#forum_html", "text": ["论坛那地方太冷了，咱们还是别去了（＞﹏＜）"] },
        { "selector": "#tool_html", "text": ["我家主人，写了好多好多工具。"] },
        { "selector": "#app_html", "text": ["抱歉，默默不能直接进去，你可以试试搜索"] },
        { "selector": ".fui-chat", "text": ["一言一语，一颦一笑。一字一句，一颗赛艇。"] },
        { "selector": ".fui-eye", "text": ["嗯··· 要切换 看板娘 吗？"] },
        { "selector": ".fui-user", "text": ["喜欢换装 Play 吗？"] },
        { "selector": ".fui-photo", "text": ["要拍张纪念照片吗？"] },
        { "selector": ".fui-info-circle", "text": ["这里有关于我的信息呢"] },
        { "selector": ".fui-cross", "text": ["你不喜欢我了吗..."] },
        { "selector": "#tor_show", "text": ["翻页比较麻烦吗，点击可以显示这篇文章的目录呢"] },
        { "selector": "#comment_go", "text": ["想要去评论些什么吗？"] },
        { "selector": "#night_mode", "text": ["深夜时要爱护眼睛呀"] },
        { "selector": ".yimem_bz", "text": ["手机扫一下，关注关注我家主人吧"] },
        { "selector": "#licenseother", "text": ["要吐槽些什么呢"] },
        { "selector": ".siderBar", "text": ["回到开始的地方吧"] },
        { "selector": "#author2", "text": ["该怎么称呼你呢"] },
        { "selector": "#author", "text": ["留下你的账号，不然就是无头像人士了"] },
        { "selector": "#url", "text": ["你的家在哪里呢，好让我去参观参观"] },
        { "selector": "#textarea", "text": ["认真填写哦，垃圾评论是禁止事项"] },
        { "selector": ".OwO-logo", "text": ["要插入一个表情吗"] },
        { "selector": "#csubmit", "text": ["要[提交]^(Commit)了吗，资源上传需要时间，请耐心等待~"] },
        { "selector": ".ImageBox", "text": ["点击图片可以放大呢"] },
        { "selector": "input[name=s]", "text": ["找不到想看的内容？搜索看看吧"] },
        { "selector": ".previous", "text": ["去上一页看看吧"] },
        { "selector": ".next", "text": ["去下一页看看吧"] },
        { "selector": ".dropdown-toggle", "text": ["这里是菜单"] },
        { "selector": "c-player a.play-icon", "text": ["想要听点音乐吗"] },
        { "selector": "c-player div.time", "text": ["在这里可以调整<span style=\"color:#0099cc;\">播放进度</span>呢"] },
        { "selector": "c-player div.volume", "text": ["在这里可以调整<span style=\"color:#0099cc;\">音量</span>呢"] },
        { "selector": "c-player div.list-button", "text": ["<span style=\"color:#0099cc;\">播放列表</span>里都有什么呢"] },
        { "selector": "c-player div.lyric-button", "text": ["有<span style=\"color:#0099cc;\">歌词</span>的话就能跟着一起唱呢"] },
        { "selector": ".waifu #live2d", "text": ["干嘛呢你，快把手拿开", "鼠…鼠标放错地方了！"] }
    ],
    "click": [
        {
            "selector": ".waifu #live2d",
            "text": [
                "是…是不小心碰到了吧",
                "萝莉控是什么呀",
                "你看到我的小熊了吗",
                "再摸的话我可要报警了！⌇●﹏●⌇",
                "110吗，这里有个变态一直在摸我(ó﹏ò｡)"
            ]
        }
    ],
    "seasons": [
        { "date": "01/01", "text": ["<span style=\"color:#0099cc;\">元旦</span>了呢，新的一年又开始了，今年是{year}年~"] },
        { "date": "02/14", "text": ["又是一年<span style=\"color:#0099cc;\">情人节</span>，{year}年找到对象了嘛~"] },
        { "date": "03/08", "text": ["今天是<span style=\"color:#0099cc;\">妇女节</span>！"] },
        { "date": "03/12", "text": ["今天是<span style=\"color:#0099cc;\">植树节</span>，要保护环境呀"] },
        { "date": "04/01", "text": ["悄悄告诉你一个秘密~<span style=\"background-color:#34495e;\">今天是愚人节，不要被骗了哦~</span>"] },
        { "date": "05/01", "text": ["今天是<span style=\"color:#0099cc;\">五一劳动节</span>，计划好假期去哪里了吗~"] },
        { "date": "06/01", "text": ["<span style=\"color:#0099cc;\">儿童节</span>了呢，快活的时光总是短暂，要是永远长不大该多好啊…"] },
        { "date": "09/03", "text": ["<span style=\"color:#0099cc;\">中国人民抗日战争胜利纪念日</span>，铭记历史、缅怀先烈、珍爱和平、开创未来。"] },
        { "date": "09/10", "text": ["<span style=\"color:#0099cc;\">教师节</span>，在学校要给老师问声好呀~"] },
        { "date": "10/01", "text": ["<span style=\"color:#0099cc;\">国庆节</span>，新中国已经成立69年了呢"] },
        { "date": "11/05-11/12", "text": ["今年的<span style=\"color:#0099cc;\">双十一</span>是和谁一起过的呢~"] },
        { "date": "12/20-12/31", "text": ["这几天是<span style=\"color:#0099cc;\">圣诞节</span>，主人肯定又去剁手买买买了~"] }
    ]
});
