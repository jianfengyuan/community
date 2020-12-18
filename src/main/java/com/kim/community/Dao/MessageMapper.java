package com.kim.community.Dao;

import com.kim.community.Entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 查詢當前用戶的會話列表, 針對每個會話只返回一條最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查詢當前用戶的會話數量
    int selectConversationCount(int userId);

    // 查詢某個會話所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查詢某個會話所包含的私信數量
    int selectLetterCount(String conversationId);

    // 查詢未讀私信數量
    int selectLetterUnreadCount(int userId ,String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    Message selectLatestNotice(String topic, int userId);

    int selectNoticeCount(String topic, int userId);

    int selectUnreadNoticeCount(String topic, int userId);

    List<Message> selectNotices(int userId, String conversationId, int offset, int limit);
}
