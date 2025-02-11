package com.sy.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.json.JSONUtil;
import com.sy.mapper.EmilMapper;
import com.sy.mapper.UserMapper;
import com.sy.model.Emil;
import com.sy.model.MailModel;
import com.sy.model.User;
import com.sy.model.resp.ResultVO;
import com.sy.service.EmailService;
import com.sy.service.UserServic;
import com.sy.tool.Xtool;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {
    private static Logger logger = Logger.getLogger(EmailServiceImpl.class);

    //    private String excelPath = "d://";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmilMapper emilMapper;
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private SimpleMailMessage simpleMailMessage;
    @Autowired
    public RedisTemplate redisTemplate;
    @Autowired
    UserServic servic;

    /**
     * 发送邮件
     *
     * @throws Exception
     * @author chenyq
     * @date 2016-5-9 上午11:18:21
     */

    @Override
    public ResultVO emailManage(String to, User user, HttpServletRequest request) {
        ResultVO resultVO = new ResultVO();
        if (redisTemplate.hasKey(to)) {
            Long time = redisTemplate.getExpire(to, TimeUnit.MINUTES);
            resultVO.setError(0);
            resultVO.setMessage(time + "秒后重新发送邮件");
            return resultVO;
        }
        MailModel mail = new MailModel();
        int idcode = (int) (Math.random() * 100000);
        //内容
        String content = "<div id=\"contentDiv\" onmouseover=\"getTop().stopPropagation(event);\" onclick=\"getTop().preSwapLink(event, 'html', 'ZC2708-4B3OUiNj8GLWCDFvxBD8Ta5');\" style=\"position:relative;font-size:14px;height:auto;padding:15px 15px 10px 15px;z-index:1;zoom:1;line-height:1.7;\" class=\"body\">    <div id=\"qm_con_body\"><div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\" style=\"\">        <style>\n" +
                "html{-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{line-height:1.6;font-family:\"Helvetica Neue\",Helvetica,Arial,sans-serif;font-size:16px}body,dd,dl,fieldset,h1,h2,h3,h4,h5,ol,p,textarea,ul{margin:0}button,fieldset,input,legend,textarea{padding:0}button,input,select,textarea{font-family:inherit;font-size:100%;margin:0}ol,ul{padding-left:0;list-style-type:none}a img,fieldset{border:0}a{text-decoration:none}.radius_avatar{display:inline-block;background-color:#FFF;padding:3px;border-radius:50%;-moz-border-radius:50%;-webkit-border-radius:50%;overflow:hidden;vertical-align:middle}.radius_avatar img{display:block;width:100%;height:100%;border-radius:50%;-moz-border-radius:50%;-webkit-border-radius:50%;background-color:#EEE}.btn_app{margin-top:10px;position:relative;display:block;margin-left:auto;margin-right:auto;padding-left:14px;padding-right:14px;-webkit-box-sizing:border-box;box-sizing:border-box;font-size:16px;text-align:center;text-decoration:none;color:#FFF;line-height:2.625;border-radius:5px;-webkit-tap-highlight-color:transparent;overflow:hidden}.btn_app:after{content:\" \";width:200%;height:200%;position:absolute;top:0;left:0;border:1px solid rgba(0,0,0,.2);-webkit-transform:scale(.5);transform:scale(.5);-webkit-transform-origin:0 0;transform-origin:0 0;-webkit-box-sizing:border-box;box-sizing:border-box;border-radius:10px}.btn_app_primary{background-color:#42C642}.btn_app_primary:link,.btn_app_primary:visited{color:#FFF}.btn_app_primary:active{color:rgba(255,255,255,.6)}.btn_app_default{background-color:#F7F7F7;color:#454545}.btn_app_default:link,.btn_app_default:visited{color:#454545}.btn_app_default:active{color:#C9C9C9}.skin_app_default{background-image:url(data:image/png;base64,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);-webkit-background-size:100% auto;background-size:100% auto;background-position:50% 0;background-repeat:no-repeat;background-color:#FFF}body,html{position:relative;height:100%}a,a:link,a:visited{color:#42C642}.mail_area{text-align:center;height:100%;-webkit-box-sizing:border-box;box-sizing:border-box;display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;-webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;-webkit-box-pack:center;-webkit-justify-content:center;-ms-flex-pack:center;justify-content:center;font-family:\"Helvetica Neue\",\"Hiragino Sans GB\",\"Microsoft YaHei\",\"\\9ED1\\4F53\",Arial,sans-serif}.mail{position:relative;display:inline-block;width:80%;margin-top:-150px;text-align:left}.mail_pc{background-color:#E6E6EA;display:block}.mail_pc .mail{width:850px;margin:45px 0;box-shadow:0 0 25px 5px rgba(0,0,0,.09);-moz-box-shadow:0 0 25px 5px rgba(0,0,0,.09);-webkit-box-shadow:0 0 25px 5px rgba(0,0,0,.09);background-color:#FFF;border-radius:8px;-moz-border-radius:8px;-webkit-border-radius:8px;overflow:hidden}.mail_pc .mail_inner{padding:17% 16% 10%}.mail_pc .mail_msg .btn_app{width:225px}.pic_skin_top{position:absolute;top:0;left:0;width:145px;height:175px;background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJEAAACvBAMAAAAPhiHNAAAAFVBMVEUAAADY2OLh4eLY2NrX19rX19nb29z1cYcUAAAAB3RSTlMAGgQUEAgMAJvI/gAAAnxJREFUaN7t2MFy0zAUheEzIe46WLLXN4VkbbuQtZPSrkWYslaC4f0fgU07HphGVuN/2fMA3xxdSxolupzlx/xEpbLOh7ySafOlJgkV+ZAzanGlktnnS+lKBVbpQ74UqMVVaajAKq2pStpTlW6wSmuqklpoL2kJbW/pRFUq8i8Bat6doHl7CTq8UdD+rqagG2rc+k6Nu8geN1UpUJU6qpIXVSlQlTpBe6nWZE7QntQSOm9atNCQ9IkaUpE5JGrcgVrbgVpbJ2htlaC11RJzTLwpY09CkE4U9IOCPlPQkoIKDMr4bDuJ+f5flZP9zEM7ZtJxQYzkTYz0ICGSD2KknWmmNBYiJHeQEOnBNFMaHUByl53F4Q2SP6YO+ypb2t3qcrbjHyhpyX05T17RfUp6Vh6PSmaxn3oU3H17/H00TWXbjj8x52Tx63kCc6FN+/JhgUL574L0hF5SznCKf+751cyFjemvdYbEG3NGn2u30+b+tdvv7XV+tonHYX62/9W5bhMsNkPindBkz2a42CZ/E9w+DXdz/2x6+jPcj0g6M98qYzwmlZjUYFLEJKMkJ0qqMGmFST0mGSU5UVKFSQ0mRUwySvKipBKTekwySvKipBKTekwySvKipBKTIiU5UVKFSQ0mGSV5UdIKkwIleVFSiUmBkrwoqcSkQElelNRgklFSLUrqKcmJkkpMCpRUi5J6SnKipA6TjJJKUVKgpEqCpEhJtSgpUlItSoqUVIuSAiVVoiSjpE6Q5IySekFSLUoySuoESV6UFCipEyTVgiRnlBQFSZ0gqRYkeYMkZ4KkIEiKgqSzIOkgSDoLkqIYyQUxkjcx0k5M3EFQTO/JyF9Bt4tB+689SAAAAABJRU5ErkJggg==) no-repeat}.pic_skin_bottom{position:absolute;bottom:0;right:0;width:300px;height:265px;background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAAEJBAMAAADcMgfDAAAAFVBMVEUAAADY2OLg4OHZ2dvZ2drY2NvZ2dtyNy61AAAAB3RSTlMAGgUKFhIO15MmpwAABDhJREFUeNrs3EFqG0EYBeHW3zPaF/gAljRoPxpL+wbnAA2S9000vv8REifBkEAgsZEpw9QJPt4BXlpaWlr6XEXyFaczSdaLCWSsPAM2Vj6DjpWvoGPFEXysfcHHiiv4WPuCjxVX8LFyRcjag5B1xMh6QMiKhpAVFSErKkJWVISsqAhZURGyomJkNYysB4ysASOrx8jKGFlRlayGkTVgZK0xsqIoWQ0ja8DIyihZVckaMLIySlZVsgaMrIySNSpZPUpWUbIGjKxAyRqVrIyS1ZSsHiWrKlk9SlZVsjqUrKJkdShZRcnqULKqktWjZFUla42S1ZSsjJI1KlmBkjU4WUXJ6lGympKVUbI2TlZRsjqUrKZkZZSsjZNVlKw1StaoZAVKVu9kjUpWoGR1TlZTsgIlq3OympIVKFm9kzUqWYGS1TtZGyerKFkZJWvlZFUlK1Cyeidr42QVJSujZHVO1uhkFSVrjZK1crKak4WStXayVk7WyB89Xi7P03TYvrabpuf58vixrMJrd5ev0zb9tdid5vMHsTI/+/J0uE//0vY0l9uzuh8rTffpf4ppLrdljXdPh/SWdqfzDVnbd12M1N9ZmnZzMbJeZGclK6U4FiPre6eqZKW0873+/IKlpW/tnM1OwkAUhU+AuHbaadcTja6rTVjzE1gXMK750/d/BMuogRBDJFzgM/Z7gpN7T8/caWamoaGh4e8QNxfl+4ayjJsOXZe78u3lx4Hdj17L64i770ZFhxitnnVJWnHY/A0HdwDmo8lRXEJZ+0CdDtQsyACzQm0Zna9k3b47gWypGpioDX4lax77zgC/tPXUxBmRFXYxtXaGjIPVy3G2eNk8/WcNsFQbZPKKD07WZyrQZH01kCVr20BjvE6gfZ4GjlZ3Bo9v2uJngWer02ebtTMnToInP74J6178BIGi7FXVoniqxkE8VdmzgKpmAqr67h8qGfySd9TBuTzgzvbUzBC3bPfwhax4MIyFoAjrVMierSBT31A1uMAaCnjAbqMKaPehBLS7paqWmd1T1eDWnFwSL91jiuLOBPuoCnY237k5soVTEbMhFTEbsiDgouMLZAunIn6FqYhfoQ8iBmkl4lq400LQbSIfkJFVIf2eC+n3QsR8H4iY75lEzPcKGQ7R77yLV1u/ky7PpcxiBWSxEhGXHc8s1gDprKZYRxULGfAD5mrILFYi5L4iIIfSVBJwKC0Ugb00kQsZpXMJGKWZkOnQQxreB6Thd6MU9IZJgTR8LhF30hXT8PoPhu8wDb9AGr7FTPgOcv7TBDn/tZkjTQcZWpogQ6vNDK0OcizVAtnDVtPDI3hCLjxi9vCG2UOjvcWtangRX0jAeMgkYjwkEjEe4kyD27Z6ScDpIWFaq2JaKyCtlTOt1WNaq0BaK0Y8b0FMJAEXxBjxvJ/ekoCzVowH3lYsxgMvTC3j4QPTTiPJb6EN4gAAAABJRU5ErkJggg==) no-repeat}h1{font-weight:400;position:absolute;right:48px;top:48px;line-height:300px;overflow:hidden;width:314px;height:32px;}.mail_info{padding:1.6em 0 0 56px;margin-top:4.3em;position:relative;border-top:1px #BBBBBD dashed;font-size:15px}.mail_info .radius_avatar{width:40px;height:40px;padding:0;position:absolute;top:1.6em;left:0}.mail_info strong{font-weight:400}.mail_info p{color:#C1C1C3;margin-top:-.05em;font-size:12px}.mail_msg{word-wrap:break-word;word-break:break-all}.mail_msg h2{font-weight:400;font-size:20px;color:#1D1D26;padding:1.34em 0 .6em}.mail_msg p{margin-bottom:24px}.mail_msg .btn_app{margin-top:45px}#app_mail .mail_msg .btn_app,#app_mail .mail_msg .btn_app:link,#app_mail .mail_msg .btn_app:visited{text-decoration:none}\n" +
                "    </style>\n" +
                "    \n" +
                "    <div class=\"mail_area mail_pc\" id=\"app_mail\">\n" +
                "        <div class=\"mail\">\n" +
                "            <div class=\"mail_inner\">\n" +
                "                <h1>YIMEM网技术平台</h1>\n" +
                "                <div class=\"mail_msg\">\n" +
                "                    <p>\n" +
                "                        HI，" + user.getUsername() + " 你好!<br>\n" +
                "                        感谢您对本站的支持与信赖，下面是您个人账号的激活码。\n" +
                "                    </p>\n" +
                "                    <p>\n" +
                idcode +
                "                    </p>\n" +
                "                    <p>\n" +
                "                        如果这不是你的邮件请忽略，很抱歉打扰你，请原谅。\n" +
                "                    </p>\n" +
                "                    <div class=\"mail_info\" ,=\"\" align=\"right\">\n" +
                "                        <strong>YIMEM团队</strong>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"pic_skin_top\"></div>\n" +
                "                <div class=\"pic_skin_bottom\"></div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "<style type=\"text/css\">.qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}</style></div></div><!-- --><style>#mailContentContainer .txt {height:auto;}</style>  </div>";
        mail.setContent(content);
        mail.setToEmails(to);
        mail.setSubject("YIMEM网站账号激活");
        resultVO = sendEmail(mail, idcode);
        return resultVO;
    }

    @SneakyThrows
    @Override
    public ResultVO registSendIdCode(String mail, String idcode, HttpServletResponse response, HttpServletRequest request) {
        ResultVO resultVO = new ResultVO();
        User user = servic.getUserByRedis(request);
        if (user == null) {
            resultVO.setError(0);
            resultVO.setMessage("你还未登录！");
            return resultVO;
        }
        if (redisTemplate.hasKey(mail)) {
            String Idcode = (String) redisTemplate.opsForValue().get(mail);
            if (Xtool.isNotNull(Idcode) && Xtool.isNotNull(idcode)) {
                if (Idcode.equals(idcode)) {
                    Emil emil = new Emil();
                    emil.setEmil(mail);
                    emil.setUserId(user.getUserId());
                    System.out.println(mail);
                    System.out.println(idcode);
                    System.out.println(Idcode);

                    try {
                        Integer count = emilMapper.insertSelective(emil);
                        System.out.println(count);
                        if (count > 0) {
                            user.setIsEmil("1");
                            userMapper.updateuser(user);
                            resultVO.setError(1);
                            resultVO.setMessage("验证成功！");
                            return resultVO;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        resultVO.setError(0);
        resultVO.setMessage("验证失败");
        return resultVO;
    }

    @Override
    public ResultVO sendEmail(MailModel mail, Integer idcode) {
        ResultVO resultVO = new ResultVO();
        // 建立邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            // 设置发件人邮箱
            if (mail.getEmailFrom() != null) {
                messageHelper.setFrom(mail.getEmailFrom());
            } else {
                try {
                    messageHelper.setFrom(new InternetAddress(simpleMailMessage.getFrom(), "YIMEM网管理员", "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // 设置收件人邮箱
            if (mail.getToEmails() != null) {
                String[] toEmailArray = mail.getToEmails().split(";");
                List<String> toEmailList = new ArrayList<String>();
                if (null == toEmailArray || toEmailArray.length <= 0) {
                    resultVO.setError(0);
                    resultVO.setMessage("收件人邮箱不得为空");
                    return resultVO;
                } else {
                    for (String s : toEmailArray) {
                        if (s != null && !s.equals("")) {
                            toEmailList.add(s);
                        }
                    }
                    if (null == toEmailList || toEmailList.size() <= 0) {
                        resultVO.setError(0);
                        resultVO.setMessage("收件人邮箱不得为空");
                        return resultVO;
                    } else {
                        toEmailArray = new String[toEmailList.size()];
                        for (int i = 0; i < toEmailList.size(); i++) {
                            toEmailArray[i] = toEmailList.get(i);
                        }
                    }
                }
                messageHelper.setTo(toEmailArray);
            } else {
                messageHelper.setTo(simpleMailMessage.getTo());
            }

            // 邮件主题
            if (mail.getSubject() != null) {
                messageHelper.setSubject(mail.getSubject());
            } else {

                messageHelper.setSubject(simpleMailMessage.getSubject());
            }

            // true 表示启动HTML格式的邮件
            messageHelper.setText(mail.getContent(), true);

            // 添加图片
            if (null != mail.getPictures()) {
                for (Iterator<Map.Entry<String, String>> it = mail.getPictures().entrySet()
                        .iterator(); it.hasNext(); ) {
                    Map.Entry<String, String> entry = it.next();
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每张图片的ID和图片地址是否齐全！");
                    }

                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("图片" + filePath + "不存在！");
                    }

                    FileSystemResource img = new FileSystemResource(file);
                    messageHelper.addInline(cid, img);
                }
            }

            // 添加附件
            if (null != mail.getAttachments()) {
                for (Iterator<Map.Entry<String, String>> it = mail.getAttachments()
                        .entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, String> entry = it.next();
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每个附件的ID和地址是否齐全！");
                    }

                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("附件" + filePath + "不存在！");
                    }

                    FileSystemResource fileResource = new FileSystemResource(file);
                    messageHelper.addAttachment(cid, fileResource);
                }
            }
            messageHelper.setSentDate(new Date());
            // 发送邮件
            javaMailSender.send(message);
            logger.info("------------发送邮件完成----------");
            resultVO.setError(1);
            System.out.println(111111);
            ValueOperations opsForValue = redisTemplate.opsForValue();
            opsForValue.set(mail.getToEmails(), String.valueOf(idcode), 60, TimeUnit.SECONDS);
        } catch (MessagingException e) {
            resultVO.setError(0);
            resultVO.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return resultVO;
    }
}

