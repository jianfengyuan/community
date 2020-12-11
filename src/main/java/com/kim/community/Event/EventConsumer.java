package com.kim.community.Event;

import com.alibaba.fastjson.JSONObject;
import com.kim.community.Entity.Event;
import com.kim.community.Entity.Message;
import com.kim.community.Service.MessageService;
import com.kim.community.utils.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value()==null) {
            logger.error("消息內容為空");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息內容為空");
            return;
        }
        // 發送站內信 -> 把消息內容存入message table中,
        // message table中有系統發出的站內信, conversation_id 為消息的topic
        // 此類消息為站內信消息, from_id = 1, 即為系統發出
        Message message = new Message();
        message.setCreateTime(new Date());
        message.setFromId(SYSTEM_ID);
        message.setConversationId(event.getTopic());
        message.setToId(event.getEntityUserId());
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
