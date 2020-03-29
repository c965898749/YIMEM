package com.sy.service.impl;

import com.sy.expection.CsdnExpection;
import com.sy.mapper.DownloadreplyMapper;
import com.sy.mapper.UploadMapper;
import com.sy.model.Downloadreply;
import com.sy.model.Upload;
import com.sy.service.DownloadreplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DownloadreplyServiceImpl implements DownloadreplyService {
    @Autowired
    private DownloadreplyMapper mapper;
    @Autowired
    private UploadMapper mapper2;

    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    @Override
    public Integer save(Downloadreply downloadreply) throws CsdnExpection {
        if (downloadreply.getUserid() == null) {
            throw new CsdnExpection("用户id不能为空");
        }
        if (downloadreply.getDowid() == null) {
            throw new CsdnExpection("资源id不能为空");
        }
        if (downloadreply.getContent() == null || "".equals(downloadreply.getContent().trim())) {
            throw new CsdnExpection("内容不能为空");
        }
        if (downloadreply.getAppraise() == null||"".equals(downloadreply.getAppraise())) {
            downloadreply.setAppraise(5);
        }

        List<Downloadreply> lists=mapper.selectAllByDowid(downloadreply.getDowid());
        Integer totalAppraise=0;
        for(Downloadreply lit:lists){
            totalAppraise+=lit.getAppraise();
        }
        Integer appraise=null;
        Integer count=mapper.selectcountByDowid(downloadreply.getDowid());
        if (count>0){
            appraise= totalAppraise/count;
        }else{
            appraise=downloadreply.getAppraise();
        }
        Upload upload=mapper2.selectById(downloadreply.getDowid());
        mapper2.updataReplyCount(downloadreply.getDowid(),(upload.getReplyCount()+1),appraise);
        return mapper.insert(downloadreply);
    }

    @Override
    public Integer remove(Integer id) {
        return null;
    }

    @Override
    public List<Downloadreply> findByDowid(Integer dowid) throws CsdnExpection {
        return mapper.selectByDowid(dowid);
    }
}
