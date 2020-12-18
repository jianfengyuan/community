package com.kim.community.utils;

public interface CommunityConstant {
    /*
     * 激活成功*/
    int ACTIVATION_SUCCESS = 0;
    // 重複激活
    int ACTIVATION_REPEAT = 1;
    // 激活失敗
    int ACTIVATION_FAILED = 2;
    // 默認狀態的登錄憑證的超時時間
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    // 記住狀態下的登錄憑證超時時間
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    // 實體類型 : 帖子
    int ENTITY_TYPE_POST = 1;
    // 實體類型: 評論
    int ENTITY_TYPE_COMMENT = 2;
    // 實體類型: 用戶
    int ENTITY_TYPE_USER = 3;
    // 私信未讀狀態
    int STATUS_UNREAD = 0;
    // 私信已讀狀態
    int STATUS_READ = 1;
    // 點讚事件
    String TOPIC_LIKE = "like";
    // 評論事件
    String TOPIC_COMMENT = "comment";
    // 關注事件
    String TOPIC_FOLLOW = "follow";
    // 发帖事件
    String TOPIC_PUBLISH = "publish";
    // 系統userID
    int SYSTEM_ID = 1;

}
