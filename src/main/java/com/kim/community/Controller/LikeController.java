package com.kim.community.Controller;

import com.kim.community.Entity.Event;
import com.kim.community.Entity.User;
import com.kim.community.Event.EventProducer;
import com.kim.community.Service.LikeService;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer producer;

    @RequestMapping(path = "like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityId, entityType, entityUserId);
        long likeCount = likeService.findEntityCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityId(entityType)
                    .setEntityUserId(entityUserId)
                    .setEntityId(entityId)
                    .setData("postId", postId);
            producer.fileEvent(event);
        }

        return CommunityUtil.getJsonString(0, null, map);
    }
}
