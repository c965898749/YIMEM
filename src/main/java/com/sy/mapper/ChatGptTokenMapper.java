package com.sy.mapper;

import com.sy.entity.ChatGptToken;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (ChatGptToken)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-28 17:01:56
 */
public interface ChatGptTokenMapper {

    List<ChatGptToken> selectALL();

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ChatGptToken queryById(Integer id);


    /**
     * 统计总行数
     *
     * @param chatGptToken 查询条件
     * @return 总行数
     */
    long count(ChatGptToken chatGptToken);

    /**
     * 新增数据
     *
     * @param chatGptToken 实例对象
     * @return 影响行数
     */
    int insert(ChatGptToken chatGptToken);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<ChatGptToken> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<ChatGptToken> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<ChatGptToken> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<ChatGptToken> entities);

    /**
     * 修改数据
     *
     * @param chatGptToken 实例对象
     * @return 影响行数
     */
    int update(ChatGptToken chatGptToken);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}

