package com.sy.service.impl;

import com.sy.entity.ChatGptToken;
import com.sy.mapper.ChatGptTokenMapper;
import com.sy.service.ChatGptTokenService;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

/**
 * (ChatGptToken)表服务实现类
 *
 * @author makejava
 * @since 2023-06-28 17:02:00
 */
@Service("chatGptTokenService")
public class ChatGptTokenServiceImpl implements ChatGptTokenService {
    @Resource
    private ChatGptTokenMapper chatGptTokenMapper;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ChatGptToken queryById(Integer id) {
        return this.chatGptTokenMapper.queryById(id);
    }

    @Override
    public List<ChatGptToken> selectALL() {
        return chatGptTokenMapper.selectALL();
    }


    /**
     * 新增数据
     *
     * @param chatGptToken 实例对象
     * @return 实例对象
     */
    @Override
    public ChatGptToken insert(ChatGptToken chatGptToken) {
        this.chatGptTokenMapper.insert(chatGptToken);
        return chatGptToken;
    }

    /**
     * 修改数据
     *
     * @param chatGptToken 实例对象
     * @return 实例对象
     */
    @Override
    public ChatGptToken update(ChatGptToken chatGptToken) {
        this.chatGptTokenMapper.update(chatGptToken);
        return this.queryById(chatGptToken.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.chatGptTokenMapper.deleteById(id) > 0;
    }
}
