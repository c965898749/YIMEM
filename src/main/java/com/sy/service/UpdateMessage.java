package com.sy.service;

public interface UpdateMessage {
    Integer delRediskey(int userId);
    void updateUserInfo(int userId);
}
