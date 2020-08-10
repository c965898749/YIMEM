package com.sy.tool;

public class Constants {
    public static final String SIMPLE_QUEUE_NAME = "simple_queue";
    public static final String REPLAY_INFORMATION = "ReplayInformation";
    public static final String FANS_INFORMATION = "FansInformation";
    public static final String LIKE_INFORMATION = "LikeInformation";
    public static final String IMAGE_SERVER_URL = "http://www.yimem.com/";
    public static final Integer Max_SIZE = 1000000;
    public static final String LOGIN_SUCCESS = "success";

    public static final String LOGIN_FAILED = "failed";

    public static final String SESSION_USER = "userSession";

    public static final String MENUS = "MENUS";

    public static final String FUNURLS = "FUNURLS";

    public static final String TO_USER_NAME="gh_7564ec5aa11b";
    public static final String APPID = "wxcbe24adb36f7b45a";
    public static final String APPSECRET = "2304599f8013f2836071eac5ef21bdc9";

    public static final String TO_USER_NAME_2="gh_55db4b3b1b3f";
    public static final String APPID_2 = "wxf577f7f0011c36a3";
    public static final String APPSECRET_2= "6377b80773c62a761835e918a45d62c3";

    public static final String REDIRECT_URI = "http%3A%2F%2Fwww.yimem.com%2Fwechat%2Flogin";

    public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    public static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

    public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    public static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

    public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

    public static final String TICKET_URL="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";

    public static final String[] type={"动作","喜剧","爱情","科幻","灾难","恐怖","悬疑","奇幻","战争","悬疑","犯罪"," 惊悚","动画","伦理",
            "剧情","冒险","历史","家庭","歌舞","传记","音乐"," 西部","运动","古装","家庭","情色","同性","武侠","短片","黑色电影","儿童",
    "舞台艺术","纪录片","鬼怪"};
    public static final String[]  language={"国语","粤语","英语","韩语","日语","法语","其他"};
    public static final String[] region={"大陆","香港","台湾","美国","法国","英国","日本","韩国","德国","泰国","印度","意大利","西班牙","加拿大","其他"};

}
