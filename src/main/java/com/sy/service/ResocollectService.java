package com.sy.service;

import com.sy.model.Download;
import com.sy.model.Resocollect;


import java.util.List;

public interface ResocollectService {
    Integer save(Resocollect download);
    Integer remove(Integer userid, Integer id);
    Resocollect findAll(Integer userid,Integer id);
    List<Resocollect>  findByUserid(Integer userid, Integer page, Integer pageSize);
    Integer findAllCount(Integer userid);
}
