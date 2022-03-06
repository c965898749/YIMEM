package com.sy.controller;

import com.sy.entity.Dingding;
import com.sy.mapper.VideoMapper;
import com.sy.model.Video;
import com.sy.service.DingdingService;
import com.sy.tool.Constants;
import com.sy.tool.HttpUtils;
import com.sy.tool.SmbUtil;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/**
 * (Dingding)表控制层
 *
 * @author makejava
 * @since 2021-08-08 15:29:39
 */
@RestController
@RequestMapping("send")
public class DingdingController {
    /**
     * 服务对象
     */
    @Resource
    private DingdingService dingdingService;
    @Autowired
    private VideoMapper videoMapper;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Dingding selectOne(Integer id) {
        return this.dingdingService.queryById(id);
    }


    //    @GetMapping("message")
//    public void message() {
//         this.dingdingService.update1();
//    }
//
//    //每隔15分钟检测ding钉钉辅助是否正常运行
////    @Scheduled(cron = "0 0 23 ? * MON")
////    @Scheduled(cron = "0 */20 * * * ?")
//    public void Reptilia() {
//        if (this.dingdingService.update2()<=0){
//            try {
//                HttpUtils.sendGet2(" http://wxpusher.zjiecode.com/api/send/message/?appToken=AT_aY0GVMPtIhpUkwZ2TQGDR4K3LzxB5uvZ&content=qd&uid=UID_xA5O5SvdVyjWI2xMK1tcLUaqsqA6&summary="+ URLEncoder.encode("打卡辅助已掉线","utf-8"), Constants.UTF8);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//    @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 1 * * ?")
    public void Reptilia() {
        List<String> list = SmbUtil.getSharedFileList("smb://yimem:c866971331@192.168.0.117/video/AV/");
        if (Xtool.isNotNull(list)) {
            list.forEach(x -> {
                System.out.println(x);
                if (Xtool.isNull(videoMapper.selectBytitle(x))){
                    Video video=new Video();
                    video.setTitle(x);
                    video.setClickcount(0);
                    video.setLikecount(0);
                    video.setCollectcount(0);
                    video.setState(1);
                    video.setRegion("大陆");
                    video.setDirector("国语");
                    video.setType("动作");
                    Random random=new Random();
                    int  n=random.nextInt( 10 )+1;
                    video.setCoverurl("imgs/vvfm/640 ("+n+").webp");
                    video.setVideourl("/common/video/"+x);
                    videoMapper.insert(video);
                }
            });
        }
    }

}
