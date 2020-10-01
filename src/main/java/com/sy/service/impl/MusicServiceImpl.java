package com.sy.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sy.mapper.MusicMapper;
import com.sy.model.Invitation;
import com.sy.model.Music;
import com.sy.model.resp.BaseResp;
import com.sy.service.MusicService;
import com.sy.tool.Xtool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MusicServiceImpl implements MusicService {
    @Autowired
    private MusicMapper musicMapper;
    @Override
    public int insert(Music record) {
        return 0;
    }

    @Override
    public int insertSelective(Music record) {
        return musicMapper.insertSelective(record);
    }

    @Override
    public List<Music> findall() {
        return null;
    }

    @Override
    public BaseResp selectByPams(Map<String,String> param) {
        BaseResp baseResp=new BaseResp();
        if(!param.isEmpty()){

            try {

                int pageNum = Integer.parseInt(param.get("pageNum"));
                int pageSize = Integer.parseInt(param.get("pageSize"));
                if (Xtool.isNotNull(pageNum)&&Xtool.isNotNull(pageSize)){
                    Music music=new Music();
                    music.setArtist(param.get("name"));
                    music.setUserid(Integer.parseInt(param.get("usrid")));
                    PageHelper.startPage(pageNum,pageSize);
//                    PageHelper.startPage(1,5);
                    System.out.println(music);
                    List<Music> list=musicMapper.selectByPams(music);
                    Page<Music> page = (Page<Music>)list;
                    if (Xtool.isNotNull(list)){
                        List arrayList=new ArrayList();
                        list.forEach(x->{
                            Map map=new HashMap();
                            map.put("name",x.getName());
                            map.put("url",x.getUrl());
                            map.put("artist",x.getArtist());
                            map.put("cover",x.getCover());
                            map.put("lrc",x.getLrc());
                            arrayList.add(map);
                        });
                        baseResp.setSuccess(200);
                        baseResp.setData(arrayList);
                        baseResp.setCount(page.getTotal());
                        return baseResp;
                    }else {
                        baseResp.setSuccess(0);
                        baseResp.setErrorMsg("未找到资源");
                        return baseResp;
                    }
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                baseResp.setSuccess(0);
                baseResp.setErrorMsg("服务器异常");
                return baseResp;

            }


        }
        baseResp.setSuccess(0);
        baseResp.setErrorMsg("服务器异常");
        return baseResp;
    }
}
