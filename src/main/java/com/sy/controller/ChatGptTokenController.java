package com.sy.controller;

import com.sy.entity.ChatGptToken;
import com.sy.service.ChatGptTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (ChatGptToken)表控制层
 *
 * @author makejava
 * @since 2023-06-28 17:01:56
 */
@RestController
@RequestMapping("chatGptToken")
public class ChatGptTokenController {
    /**
     * 服务对象
     */
    @Resource
    private ChatGptTokenService chatGptTokenService;



    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<ChatGptToken> queryById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.chatGptTokenService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param chatGptToken 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<ChatGptToken> add(ChatGptToken chatGptToken) {
        return ResponseEntity.ok(this.chatGptTokenService.insert(chatGptToken));
    }

    /**
     * 编辑数据
     *
     * @param chatGptToken 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<ChatGptToken> edit(ChatGptToken chatGptToken) {
        return ResponseEntity.ok(this.chatGptTokenService.update(chatGptToken));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Integer id) {
        return ResponseEntity.ok(this.chatGptTokenService.deleteById(id));
    }

}

