package com.sy.service.impl;

import com.sy.entity.PictureInfo;
import com.sy.mapper.PictureInfoMapper;
import com.sy.service.PictureInfoService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

/**
 * (PictureInfo)表服务实现类
 *
 * @author
 * @since 2021-09-18 13:46:04
 */
@Service("pictureInfoService")
public class PictureInfoServiceImpl implements PictureInfoService {
    @Resource
    private PictureInfoMapper pictureInfoMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PictureInfo queryById(String id) {
        return this.pictureInfoMapper.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param pictureInfo 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */

    /**
     * 新增数据
     *
     * @param pictureInfo 实例对象
     * @return 实例对象
     */
    @Override
    public PictureInfo insert(PictureInfo pictureInfo) {
        this.pictureInfoMapper.insert(pictureInfo);
        return pictureInfo;
    }

    /**
     * 修改数据
     *
     * @param pictureInfo 实例对象
     * @return 实例对象
     */
    @Override
    public PictureInfo update(PictureInfo pictureInfo) {
        this.pictureInfoMapper.update(pictureInfo);
        return this.queryById(pictureInfo.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        return this.pictureInfoMapper.deleteById(id) > 0;
    }
}
