package com.kim.community.Controller;

import com.alibaba.fastjson.JSONObject;
import com.kim.community.Entity.Message;
import com.kim.community.Entity.Page;
import com.kim.community.Entity.User;
import com.kim.community.Service.MessageService;
import com.kim.community.Service.UserService;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
@RequestMapping(path = "/message")
public class MessageController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setPath("/message/letter/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        System.out.println(page);
        // 會話列表
        List<Map<String, Object>> conversations = new ArrayList<>();
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        if (conversationList != null) {
            for (Message conversation : conversationList) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("conversation", conversation);
                msg.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));
                msg.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                msg.put("target", userService.findUserById(targetId));
                conversations.add(msg);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查詢未讀消息數量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId());
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId());
        model.addAttribute("unreadNoticeCount", unreadNoticeCount);
        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/message/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> lettersOV = new ArrayList<>();
        if (letters != null) {
            for (Message letter :
                    letters) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                lettersOV.add(map);
            }
        }
        model.addAttribute("letters", lettersOV);
        model.addAttribute("target", getTargetUser(conversationId));
        List<Integer> ids = getReadLetterIds(letters);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    private User getTargetUser(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getReadLetterIds(List<Message> messageList) {
        List<Integer> ids = new ArrayList<>();
        if (messageList != null) {
            for (Message message :
                    messageList) {
                System.out.println(message.getToId() + " " + hostHolder.getUser().getId() + " " + message.getStatus());
                if (message.getToId() == hostHolder.getUser().getId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJsonString(1, "目標用戶不存在");
        }
        System.out.println(content);
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setConversationId(message.getToId() > message.getFromId() ?
                message.getFromId() + "_" + message.getToId() :
                message.getToId() + "_" + message.getFromId());
        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0, "發送成功");
    }

    @RequestMapping(path = "notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> messageVO = new HashMap<>();
        // 查詢點讚類通知
        Message likeNotice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        int likeNoticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
        int unreadLikeNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
        if (likeNotice != null) {
            messageVO.put("message", likeNotice);
            String content = HtmlUtils.htmlEscape(likeNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            messageVO.put("count", likeNoticeCount);
            messageVO.put("unread", unreadLikeNoticeCount);
        }
        model.addAttribute("likeNotice", messageVO);
        // 查詢評論類通知
        Message commentNotice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        int commentNoticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
        int unreadCommentNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
        messageVO = new HashMap<>();
        if (commentNotice != null) {
            messageVO.put("message", commentNotice);
            String content = HtmlUtils.htmlEscape(commentNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            messageVO.put("count", commentNoticeCount);
            messageVO.put("unread", unreadCommentNoticeCount);
        }
        model.addAttribute("commentNotice", messageVO);
        // 查詢關注類通知
        Message followNotice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        int followNoticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
        int unreadFollowNoticeCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        if (followNotice != null) {
            messageVO.put("message", followNotice);
            String content = HtmlUtils.htmlEscape(followNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("count", followNoticeCount);
            messageVO.put("unread", unreadFollowNoticeCount);
        }
        model.addAttribute("followNotice", messageVO);

        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId());
        model.addAttribute("unreadNoticeCount", unreadNoticeCount);
        int unreadLetterCount = messageService.findLetterUnreadCount(user.getId());
        model.addAttribute("unreadLetterCount", unreadLetterCount);
        return "/site/notice";
    }
}

