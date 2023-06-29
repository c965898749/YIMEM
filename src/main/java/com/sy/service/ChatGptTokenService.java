package com.sy.service;

import com.sy.entity.ChatGptToken;

import java.util.List;


/**
 * (ChatGptToken)表服务接口
 *
 * @author makejava
 * @since 2023-06-28 17:02:00
 */
public interface ChatGptTokenService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ChatGptToken queryById(Integer id);

    List<ChatGptToken> selectALL();

    /**
     * 新增数据
     *
     * @param chatGptToken 实例对象
     * @return 实例对象
     */
    ChatGptToken insert(ChatGptToken chatGptToken);

    /**
     * 修改数据
     *
     * @param chatGptToken 实例对象
     * @return 实例对象
     */
    ChatGptToken update(ChatGptToken chatGptToken);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}
