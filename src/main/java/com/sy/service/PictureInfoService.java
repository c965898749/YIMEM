//package com.sy.service;
//
//import com.sy.entity.PictureInfo;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//
///**
// * (PictureInfo)表服务接口
// *
// * @author
// * @since 2021-09-18 13:46:04
// */
//public interface PictureInfoService {
//
//    /**
//     * 通过ID查询单条数据
//     *
//     * @param id 主键
//     * @return 实例对象
//     */
//    PictureInfo queryById(String id);
//
//    /**
//     * 分页查询
//     *
//     * @param pictureInfo 筛选条件
//     * @param pageRequest      分页对象
//     * @return 查询结果
//     */
//    Page<PictureInfo> queryByPage(PictureInfo pictureInfo, PageRequest pageRequest);
//
//    /**
//     * 新增数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 实例对象
//     */
//    PictureInfo insert(PictureInfo pictureInfo);
//
//    /**
//     * 修改数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 实例对象
//     */
//    PictureInfo update(PictureInfo pictureInfo);
//
//    /**
//     * 通过主键删除数据
//     *
//     * @param id 主键
//     * @return 是否成功
//     */
//    boolean deleteById(String id);
//
//}
