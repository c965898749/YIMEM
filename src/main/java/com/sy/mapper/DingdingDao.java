package com.sy.mapper;

import com.sy.entity.Dingding;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Dingding)表数据库访问层
 *
 * @author makejava
 * @since 2021-08-08 15:29:34
 */
public interface DingdingDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Dingding queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Dingding> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param dingding 实例对象
     * @return 对象列表
     */
    List<Dingding> queryAll(Dingding dingding);

    /**
     * 新增数据
     *
     * @param dingding 实例对象
     * @return 影响行数
     */
    int insert(Dingding dingding);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Dingding> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Dingding> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Dingding> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Dingding> entities);

    /**
     * 修改数据
     *
     * @param dingding 实例对象
     * @return 影响行数
     */
    int update(Dingding dingding);

    int update1();

    int update2();

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

