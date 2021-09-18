//package com.sy.service.impl;
//
//import com.sy.entity.PictureInfo;
//import com.sy.dao.PictureInfoDao;
//import com.sy.service.PictureInfoService;
//import org.springframework.stereotype.Service;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//
//import javax.annotation.Resource;
//
///**
// * (PictureInfo)表服务实现类
// *
// * @author
// * @since 2021-09-18 13:46:04
// */
//@Service("pictureInfoService")
//public class PictureInfoServiceImpl implements PictureInfoService {
//    @Resource
//    private PictureInfoDao pictureInfoDao;
//
//    /**
//     * 通过ID查询单条数据
//     *
//     * @param id 主键
//     * @return 实例对象
//     */
//    @Override
//    public PictureInfo queryById(String id) {
//        return this.pictureInfoDao.queryById(id);
//    }
//
//    /**
//     * 分页查询
//     *
//     * @param pictureInfo 筛选条件
//     * @param pageRequest      分页对象
//     * @return 查询结果
//     */
//    @Override
//    public Page<PictureInfo> queryByPage(PictureInfo pictureInfo, PageRequest pageRequest) {
//        long total = this.pictureInfoDao.count(pictureInfo);
//        return new PageImpl<>(this.pictureInfoDao.queryAllByLimit(pictureInfo, pageRequest), pageRequest, total);
//    }
//
//    /**
//     * 新增数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 实例对象
//     */
//    @Override
//    public PictureInfo insert(PictureInfo pictureInfo) {
//        this.pictureInfoDao.insert(pictureInfo);
//        return pictureInfo;
//    }
//
//    /**
//     * 修改数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 实例对象
//     */
//    @Override
//    public PictureInfo update(PictureInfo pictureInfo) {
//        this.pictureInfoDao.update(pictureInfo);
//        return this.queryById(pictureInfo.getId());
//    }
//
//    /**
//     * 通过主键删除数据
//     *
//     * @param id 主键
//     * @return 是否成功
//     */
//    @Override
//    public boolean deleteById(String id) {
//        return this.pictureInfoDao.deleteById(id) > 0;
//    }
//}
