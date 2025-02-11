package com.sy.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import com.sy.model.Music;
import com.sy.model.User;
import com.sy.model.resp.BaseResp;
import com.sy.service.MusicService;
import com.sy.service.UserServic;
import com.sy.tool.Xtool;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class MusicController {

    @Autowired
    private MusicService musicService;
    @Autowired
    UserServic servic;
    private final String prefixUrl = "http://music.163.com/api/playlist/detail?id=";
    private final String playUrl = "http://music.163.com/song/media/outer/url?id=";
    private final String lyricUrl = "http://music.163.com/api/song/lyric?os=pc&lv=-1&kv=-1&tv=-1&id=";

    @RequestMapping("getMymusic")
    public BaseResp getMymusic(@RequestParam Map<String, String> param, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp = musicService.selectByPams(param);
        return baseResp;
    }

    @PostMapping("addMymusic")
    public BaseResp addMymusic(Music music, HttpServletRequest request) throws Exception {
        BaseResp baseResp = new BaseResp();
        User user = servic.getUserByRedis(request);
        if (user != null) {
            music.setUserid(user.getUserId());
            try {
                Integer count = musicService.insertSelective(music);
                return count > 0 ? new BaseResp(200, "插入成功") : new BaseResp(0, "插入失败");
            } catch (Exception e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
                return baseResp;
            }
        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("未登入");
        return baseResp;
    }

    @GetMapping("/getPlayList")
    public List<Music> getPlayList(String listId) throws IOException {
        //拼接完整的url
        System.out.println(listId);
        String lastUrl = prefixUrl + listId;
        //发起http请求获取歌单信息
        URL url = new URL(lastUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String result = getResponse(conn);
        if (JSON.parseObject(result).getJSONObject("result") != null) {
            JSONArray arr = JSON.parseObject(result).getJSONObject("result").getJSONArray("tracks");
            List<Music> list = getAllMusic(arr);
            return list;
        }
        return null;
    }

    public String getResponse(HttpURLConnection conn) throws IOException {
        //设置属性
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
        conn.setRequestProperty("cookie", "appver=1.5.0.75771");
        conn.setRequestProperty("referer", "http://music.163.com/");
        //开启连接
        conn.connect();
        //获取响应
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        //关闭流
        br.close();
        //关闭连接
        conn.disconnect();
        return sb.toString();
    }

    public List<Music> getAllMusic(JSONArray arr) throws IOException {
        List<Music> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Music music = new Music();
            music.setName(obj.getString("name"));
            music.setUrl(playUrl + obj.getString("id") + ".mp3");
            music.setArtist(obj.getJSONArray("artists").getJSONObject(0).getString("name"));
            music.setCover(obj.getJSONObject("album").getString("blurPicUrl"));
//            获取歌词
            String lastUrl = lyricUrl + obj.getString("id");
            //发起http请求获取歌单信息
            JSONObject jsonObject = doGetStr(lastUrl);
//            URL url = new URL(lastUrl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            String result = getResponse(conn);
//            System.out.println(result);
//            System.out.println();;
            music.setLrc(jsonObject.getJSONObject("lrc").getString("lyric"));
            list.add(music);
        }
        return list;
    }

    public JSONObject doGetStr(String url) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
//            String result = EntityUtils.toString(entity,"UTF-8");
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.parseObject(result);
        }
        return jsonObject;
    }
}

