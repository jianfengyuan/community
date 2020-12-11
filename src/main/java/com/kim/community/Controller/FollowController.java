package com.kim.community.Controller;

import com.kim.community.Entity.Event;
import com.kim.community.Entity.Page;
import com.kim.community.Entity.User;
import com.kim.community.Event.EventProducer;
import com.kim.community.Service.FollowService;
import com.kim.community.Service.UserService;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer producer;
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        Event event = new Event()
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setTopic(TOPIC_FOLLOW)
                .setEntityUserId(entityId);
        producer.fileEvent(event);
        return CommunityUtil.getJsonString(0, "關注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJsonString(0, "已取消關注");
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }
        model.addAttribute("user", user);
        page.setPath("/followers/" + userId);
        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowerList(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("isFollowed", isFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }

    private boolean isFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.findFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }
        model.addAttribute("user", user);
        page.setPath("/followees/" + userId);
        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFolloweeList(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("isFollowed", isFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

}
