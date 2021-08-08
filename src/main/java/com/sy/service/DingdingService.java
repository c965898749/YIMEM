package com.sy.service;

import com.sy.entity.Dingding;

import java.util.List;

/**
 * (Dingding)表服务接口
 *
 * @author makejava
 * @since 2021-08-08 15:29:38
 */
public interface DingdingService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Dingding queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Dingding> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param dingding 实例对象
     * @return 实例对象
     */
    Dingding insert(Dingding dingding);

    /**
     * 修改数据
     *
     * @param dingding 实例对象
     * @return 实例对象
     */
    Dingding update(Dingding dingding);

    void update1();

    Integer update2();

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
