package com.kim.community.Service;

import com.kim.community.Dao.LoginTicketMapper;
import com.kim.community.Dao.UserMapper;
import com.kim.community.Entity.LoginTicket;
import com.kim.community.Entity.User;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.MailClient;
import com.kim.community.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;



    public User findUserById(int userId) {
        User user = getCache(userId);
        if (user == null) {
            user = initCache(userId);
        }
        return user;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            throw new IllegalArgumentException("參數不能為空");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "賬號不能為空");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密碼不能為空");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "郵箱不能為空");
            return map;
        }

        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "該賬戶已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "該郵箱已被註冊");
            return map;
        }
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));

        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 使用模板 發送激活郵件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/comunity/activation/101(用戶id)/code
        // mybatis.configuration.use-generated-keys=true
        // 開啟了上面選項, 在底層 insert完數據後 自動把ID 回填
        String url = domain + contextPath + "/activation/" + user.getId() + "/" +user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        System.out.println("郵件已發送");
//        mailClient.sendMail(user.getEmail(),"激活賬號", content);
        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            deleteCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILED;
        }
    }

    public Map<String, Object> Login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "賬號不能為空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密碼不能為空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg","賬號不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "該賬號未激活");
            return map;
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if (user.getPassword().equals(password)) {
            map.put("passwordMsg", "密碼不正確");
        }
        // 生成登錄憑證

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 10000));
        String ticketKey = RedisUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket, 60*60, TimeUnit.SECONDS);
//        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        String ticketKey = RedisUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        //        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        int row = userMapper.updateHeader(userId, headerUrl);
        deleteCache(userId);
//        String userKey = RedisUtil.getUserKey(userId);
//        User user = (User) redisTemplate.opsForValue().get(userId);
//        user.setHeaderUrl(headerUrl);
//        redisTemplate.opsForValue().set(userKey, user);
        return row;
    }

    public int updatePassword(int userId, String password) {
        User user = userMapper.selectById(userId);
        int row = userMapper.updatePassword(userId, CommunityUtil.md5(password + user.getSalt()));
        deleteCache(userId);
        return row;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 60* 60, TimeUnit.SECONDS);
        return user;
    }

    private void deleteCache(int userId) {
        String userKey = RedisUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    private User getCache(int userId) {
        String userKey = RedisUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

}
