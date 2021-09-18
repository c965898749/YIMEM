//package com.sy.dao;
//
//import com.sy.entity.PictureInfo;
//import org.apache.ibatis.annotations.Param;
////import org.springframework.data.domain.Pageable;
//import java.util.List;
//
///**
// * (PictureInfo)表数据库访问层
// *
// * @author
// * @since 2021-09-18 13:46:01
// */
//public interface PictureInfoDao {
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
//     * 查询指定行数据
//     *
//     * @param pictureInfo 查询条件
//     * @param pageable         分页对象
//     * @return 对象列表
//     */
//    List<PictureInfo> queryAllByLimit(PictureInfo pictureInfo, @Param("pageable") Pageable pageable);
//
//    /**
//     * 统计总行数
//     *
//     * @param pictureInfo 查询条件
//     * @return 总行数
//     */
//    long count(PictureInfo pictureInfo);
//
//    /**
//     * 新增数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 影响行数
//     */
//    int insert(PictureInfo pictureInfo);
//
//    /**
//     * 批量新增数据（MyBatis原生foreach方法）
//     *
//     * @param entities List<PictureInfo> 实例对象列表
//     * @return 影响行数
//     */
//    int insertBatch(@Param("entities") List<PictureInfo> entities);
//
//    /**
//     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
//     *
//     * @param entities List<PictureInfo> 实例对象列表
//     * @return 影响行数
//     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
//     */
//    int insertOrUpdateBatch(@Param("entities") List<PictureInfo> entities);
//
//    /**
//     * 修改数据
//     *
//     * @param pictureInfo 实例对象
//     * @return 影响行数
//     */
//    int update(PictureInfo pictureInfo);
//
//    /**
//     * 通过主键删除数据
//     *
//     * @param id 主键
//     * @return 影响行数
//     */
//    int deleteById(String id);
//
//}
//
