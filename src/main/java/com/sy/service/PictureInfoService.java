package com.sy.service;

import com.sy.entity.PictureInfo;

/**
 * (PictureInfo)表服务接口
 *
 * @author
 * @since 2021-09-18 13:46:04
 */
public interface PictureInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    PictureInfo queryById(String id);


    /**
     * 新增数据
     *
     * @param pictureInfo 实例对象
     * @return 实例对象
     */
    PictureInfo insert(PictureInfo pictureInfo);

    /**
     * 修改数据
     *
     * @param pictureInfo 实例对象
     * @return 实例对象
     */
    PictureInfo update(PictureInfo pictureInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(String id);

}
