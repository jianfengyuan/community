package com.kim.community.Service;

import com.kim.community.Dao.MessageMapper;
import com.kim.community.Entity.Message;
import com.kim.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId ,String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int findLetterUnreadCount(int userId) {
        return messageMapper.selectLetterUnreadCount(userId, null);
    }

    public int addMessage(Message message) {
        message.setContent(sensitiveFilter.filter(HtmlUtils.htmlEscape(message.getContent())));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(topic, userId);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(topic, userId);
    }

    public int findUnreadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnreadNoticeCount(topic, userId);
    }
    public int findUnreadNoticeCount(int userId) {
        return messageMapper.selectUnreadNoticeCount(null, userId);
    }
}