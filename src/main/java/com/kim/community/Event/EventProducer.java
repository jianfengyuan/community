package com.kim.community.Event;

import com.alibaba.fastjson.JSONObject;
import com.kim.community.Entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    KafkaTemplate template;

    public void fileEvent(Event event) {
        template.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
