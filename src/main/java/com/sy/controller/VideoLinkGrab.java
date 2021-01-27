package com.sy.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.VideoMapper;
import com.sy.model.Video;
import com.sy.model.resp.BaseResp;
import com.sy.service.DownloadService;
import com.sy.tool.Constants;
import com.sy.tool.ScheduledUtill;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class VideoLinkGrab {
    @Autowired
    private VideoMapper videoMapper;

    public static void main(String[] args) {
        VideoLinkGrab videoLinkGrab = new VideoLinkGrab();
//        videoLinkGrab.saveData("http://www.y80s.com/movie/list/");
        videoLinkGrab.saveData("http://www.y80s.com/movie/40606");
    }
    //每周星期天凌晨1点实行一次
    @Scheduled(cron = "0 0 23 ? * MON")
    public void Reptilia() {
        System.out.println("执行");
        VideoLinkGrab videoLinkGrab = new VideoLinkGrab();
        videoLinkGrab.saveData("http://www.y80s.com/movie/list/");
//        videoLinkGrab.saveData("http://www.y80s.com/movie/40606");
    }


    @RequestMapping(value = "getVideo", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp getVideo(Video video) {
//        System.out.println(pageNum);
        BaseResp resultVO = new BaseResp();
        Integer pageSize = 24;
        PageHelper.startPage(video.getPageNum(), pageSize);
        List<Video> videos = null;
        try {
            videos = videoMapper.select(video);
            Page<Video> blogPage = (Page<Video>) videos;
            resultVO.setData(videos);
            resultVO.setCount(blogPage.getTotal());
            resultVO.setSuccess(1);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO.setErrorMsg(e.getMessage());
            resultVO.setSuccess(0);
            return resultVO;
        }

    }

    @RequestMapping(value = "getVideoById", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp getVideoById(Integer videoid) {
        BaseResp resultVO = new BaseResp();
        System.out.println(videoid);
        Video videos = null;
        try {
            videos = videoMapper.selectByPrimaryKey(videoid);
            resultVO.setData(videos);
            resultVO.setSuccess(1);
            return resultVO;
        } catch (Exception e) {
            e.printStackTrace();
            resultVO.setErrorMsg(e.getMessage());
            resultVO.setSuccess(0);
            return resultVO;
        }

    }

    /**
     * 将获取到的数据保存在数据库中
     *
     * @param baseUrl 爬虫起点
     * @return null
     */
    public void saveData(String baseUrl) {
//        DownloadService downloadService = (DownloadService) ScheduledUtill.getBeans("DownloadService");
        Map<String, Boolean> oldMap = new LinkedHashMap<String, Boolean>(); // 存储链接-是否被遍历

        Map<String, Video> videoLinkMap = new LinkedHashMap<String, Video>(); // 视频下载链接
        String oldLinkHost = ""; // host

        Pattern p = Pattern.compile("(https?://)?[^/\\s]*"); // 比如：http://www.zifangsky.cn
        Matcher m = p.matcher(baseUrl);
        if (m.find()) {
            oldLinkHost = m.group();
        }
//        System.out.println(oldLinkHost);

        oldMap.put(baseUrl, false);
//        videoLinkMap = crawlLinks(oldLinkHost, oldMap);
        crawlLinks(oldLinkHost, oldMap);
        System.out.println("结束" + videoLinkMap.size());
        // 遍历，然后将数据保存在数据库中
//        for (Map.Entry<String, Video> mapping : videoLinkMap.entrySet()) {
//            Video video = new Video();
//            video=mapping.getValue();
//            try {
//                if (Xtool.isNotNull(mapping.getKey())){
//                    Integer videoId = downloadService.selectBytitle(video.getTitle());
//                    if (Xtool.isNotNull(videoId)) {
//                        video.setVideoid(videoId);
//                        downloadService.updateByPrimaryKeySelective(video);
//                    } else {
//                        downloadService.VideoMapper(video);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * 抓取一个网站所有可以抓取的网页链接，在思路上使用了广度优先算法 对未遍历过的新链接不断发起GET请求， 一直到遍历完整个集合都没能发现新的链接
     * 则表示不能发现新的链接了，任务结束
     * <p>
     * 对一个链接发起请求时，对该网页用正则查找我们所需要的视频链接，找到后存入集合videoLinkMap
     *
     * @param oldLinkHost 域名，如：http://www.zifangsky.cn
     * @param oldMap      待遍历的链接集合
     * @return 返回所有抓取到的视频下载链接集合
     */
    private Map<String, Video> crawlLinks(String oldLinkHost,
                                          Map<String, Boolean> oldMap) {
        DownloadService downloadService = (DownloadService) ScheduledUtill.getBeans("DownloadService");
        Map<String, Boolean> newMap = new LinkedHashMap<String, Boolean>(); // 每次循环获取到的新链接
        Map<String, Video> videoLinkMap = new LinkedHashMap<String, Video>(); // 视频下载链接
        String oldLink = "";
//        System.out.println("大小"+oldMap.size());
        for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
            int i = 0;
//            System.out.println("大小"+oldMap.size());
            System.out.println("link:" + mapping.getKey() + "--------check:"
                    + mapping.getValue());
            String VideoContent = "";
            // 如果没有被遍历过
            if (!mapping.getValue()) {
                oldLink = mapping.getKey();
                // 发起GET请求
                try {
                    System.out.println("开始链接第一");
                    URL url = new URL(oldLink);
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(2500);
                            connection.setReadTimeout(2500);
                            connection.setInstanceFollowRedirects(true);
                    if (connection.getResponseCode() == 200) {
                        System.out.println(connection.getResponseCode() + "11111111111");
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream, "UTF-8"));
                        String line = "";
                        String ne = "";
                        Pattern pattern = null;
                        Matcher matcher = null;
                        //电影详情页面，取出其中的视频下载链接，不继续深入抓取其他页面
//                        System.out.println(isMoviePage(oldLink));
//                        System.out.println(checkUrl(oldLink));
                        if (isMoviePage(oldLink)) {
                            boolean checkTitle = false;
                            String title = "";
                            Video video = new Video();
                            video.setRegion("");
                            video.setDirector("");
                            video.setType("");
                            while ((line = reader.readLine()) != null) {
                                //取出页面中的视频标题
                                if (!checkTitle) {
                                    pattern = Pattern.compile("([^\\s]+).*?</title>");
                                    matcher = pattern.matcher(line);
                                    if (matcher.find()) {
                                        title = matcher.group(1);
                                        video.setTitle(matcher.group(1));
                                        System.out.println("取出页面中的视频标题" + video.getTitle());
                                        checkTitle = true;
                                        continue;
                                    }
                                }
                                //时间 185
                                pattern = Pattern.compile("上映日期：</span>([^(</span>)]*)</span>");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    video.setCreatetime(matcher.group(1));
                                    System.out.println("时间" + video.getCreatetime());
                                    continue;
                                }
                                //剧情介绍 217
                                pattern = Pattern.compile("剧情介绍：</span>([^(</p>)]*)</p>");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    video.setInfo(matcher.group(1));
                                    continue;
                                }
                                //类型 165
                                pattern = Pattern.compile("[^<li>]<a\\s+href=\\\"/movie/list/[^>]*>([^(</a>)]*)</a>");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    if (Arrays.asList(Constants.type).contains(matcher.group(1))){
                                        video.setType(video.getType()+":"+matcher.group(1));
                                    }else if (Arrays.asList(Constants.language).contains(matcher.group(1))){
                                        video.setDirector(video.getDirector()+":"+matcher.group(1));
                                    }else if (Arrays.asList(Constants.region).contains(matcher.group(1))){
                                        video.setRegion(video.getRegion()+":"+matcher.group(1));
                                    }
                                    System.out.println("类型" + video.getType());
                                    continue;
                                }
                                //豆瓣 201
                                pattern = Pattern.compile("<span\\s+class=\\\"score\\s+sc\\d+\\\"\\s+></span>([^(</div>)]*)</div>");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    video.setDouban(matcher.group(1));
                                    continue;
                                }
                                //图片抓取 118
                                pattern = Pattern.compile("img\\.mimiming\\.com[\\s\\S]*\\.jpg");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    video.setCoverurl("http://" + matcher.group(0));
                                    continue;
                                }
                                //在线播放抓取 226
                                pattern = Pattern.compile("/movie/[\\S\\s]*/play");
                                matcher = pattern.matcher(line);
                                if (matcher.find()) {
                                    URL rl = new URL(oldLinkHost + matcher.group(0));
                                    System.out.println("开始链接第二");
                                    HttpURLConnection tion = (HttpURLConnection) rl.openConnection();
                                    tion.setRequestMethod("GET");
                                    tion.setConnectTimeout(30000);
                                    tion.setReadTimeout(30000);
                                    tion.setInstanceFollowRedirects(true);
                                    System.out.println(tion.getResponseCode() + "22222222222222");
                                    if (tion.getResponseCode() == 200) {
                                        InputStream stream = tion.getInputStream();
                                        BufferedReader der = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                                        while ((ne = der.readLine()) != null) {
//                                            System.out.println(111);
                                            //在线播放抓取
                                            pattern = Pattern.compile("src=\"(https://dpplay\\.zuidajiexi\\.com[\\s\\S]*)\"\\s+frameborder=");
                                            matcher = pattern.matcher(ne);
                                            if (matcher.find()) {
                                                System.out.println("下载地址" + matcher.group(1));
                                                video.setVideourl(matcher.group(1));
                                                videoLinkMap.put(title, video);
                                                if (Xtool.isNotNull(mapping.getKey())) {
                                                    Integer videoId = downloadService.selectBytitle(video.getTitle());
                                                    System.out.println("id"+videoId);
                                                    if (Xtool.isNotNull(videoId)) {
                                                        video.setVideoid(videoId);
                                                        System.out.println("插入");
                                                        downloadService.updateByPrimaryKeySelective(video);
                                                    } else {
                                                        downloadService.VideoMapper(video);
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        der.close();
                                        stream.close();
                                        tion.disconnect();
                                    }
                                    break;
                                }
//                                //下载链接 321
//                                pattern = Pattern.compile("(thunder:[^\"]+).*thunder[rR]es[tT]itle=\"");
//                                matcher = pattern.matcher(line);
//                                if (matcher.find()) {
//                                    video.setSubtitle(matcher.group(0));
//                                    break;
//                                }

                            }
                        } else if (checkUrl(oldLink)) {
                            //电影列表页面
                            while ((line = reader.readLine()) != null) {

                                pattern = Pattern
                                        .compile("<a href=\"([^\"\\s]*)\"");
                                matcher = pattern.matcher(line);
                                while (matcher.find()) {
                                    String newLink = matcher.group(1).trim(); // 链接
                                    // 判断获取到的链接是否以http开头
                                    if (!newLink.startsWith("http")) {
                                        if (newLink.startsWith("/"))
                                            newLink = oldLinkHost + newLink;
                                        else
                                            newLink = oldLinkHost + "/" + newLink;
                                    }
                                    // 去除链接末尾的 /
                                    if (newLink.endsWith("/"))
                                        newLink = newLink.substring(0,
                                                newLink.length() - 1);
                                    // 去重，并且丢弃其他网站的链接
                                    if (!oldMap.containsKey(newLink)
                                            && !newMap.containsKey(newLink)
                                            && (checkUrl(newLink) || isMoviePage(newLink))) {
//                                        System.out.println("temp: " + newLink);
                                        newMap.put(newLink, false);
                                    }
                                }
                            }
                        }

                        reader.close();
                        inputStream.close();
                    }
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                oldMap.replace(oldLink, false, true);
            }
        }
        // 有新链接，继续遍历
        if (!newMap.isEmpty()) {
            oldMap.putAll(newMap);
            videoLinkMap.putAll(crawlLinks(oldLinkHost, oldMap)); // 由于Map的特性，不会导致出现重复的键值对
        }
        return videoLinkMap;
    }

    /**
     * 判断是否是电影列表页面
     *
     * @param url 待检查URL
     * @return 状态
     */
    public boolean checkUrl(String url) {
        Pattern pattern = Pattern.compile("http://www.y80s.com/movie/\\d*");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
            return true; //2015年的列表
        else
            return false;
    }

    /**
     * 判断页面是否是电影详情页面
     *
     * @param url 页面链接
     * @return 状态
     */
    public boolean isMoviePage(String url) {
        Pattern pattern = Pattern.compile("http://www.y80s.com/movie/\\d+");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
            return true; //电影页面
        else
            return false;
    }

}
