package com.kim.community.utils;

public class RedisUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kapcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";


    // 某個實體的讚
    // like: entity: entityType: entityId -> set(userId)
    public static String getEntityKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
    // 某個用戶的讚
    // like: user: userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 一個用戶關注某一個實體
    // followee: userId: entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 一個實體的粉絲
    // follower: entityType: entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // kaptcha: kaptchaOwner -> value(kaptcha)
    public static String getKaptchaKey(String kaptchaOwner) {
        return PREFIX_KAPTCHA + SPLIT + kaptchaOwner;
    }

    // ticket: ticket -> value(ticket.class)
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // user: userId -> value(user)
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
