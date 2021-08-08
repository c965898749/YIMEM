package com.sy.service.impl;

import com.sy.entity.Dingding;
import com.sy.mapper.DingdingDao;
import com.sy.service.DingdingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Dingding)表服务实现类
 *
 * @author makejava
 * @since 2021-08-08 15:29:38
 */
@Service("dingdingService")
public class DingdingServiceImpl implements DingdingService {
    @Resource
    private DingdingDao dingdingDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Dingding queryById(Integer id) {
        return this.dingdingDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Dingding> queryAllByLimit(int offset, int limit) {
        return this.dingdingDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param dingding 实例对象
     * @return 实例对象
     */
    @Override
    public Dingding insert(Dingding dingding) {
        this.dingdingDao.insert(dingding);
        return dingding;
    }

    /**
     * 修改数据
     *
     * @param dingding 实例对象
     * @return 实例对象
     */
    @Override
    public Dingding update(Dingding dingding) {
        this.dingdingDao.update(dingding);
        return this.queryById(dingding.getId());
    }

    @Override
    public void update1() {
        this.dingdingDao.update1();
    }

    @Override
    public Integer update2() {
       return this.dingdingDao.update2();
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.dingdingDao.deleteById(id) > 0;
    }
}
