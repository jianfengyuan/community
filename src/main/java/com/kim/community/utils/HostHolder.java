package com.kim.community.utils;

import com.kim.community.Entity.User;
import org.springframework.stereotype.Component;

/*
* 持有用戶信息, 代替Session對象
* */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUsers(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
