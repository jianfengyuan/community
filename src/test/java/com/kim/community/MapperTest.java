package com.kim.community;

import com.kim.community.Dao.DiscussPostMapper;
import com.kim.community.Dao.LoginTicketMapper;
import com.kim.community.Dao.MessageMapper;
import com.kim.community.Dao.UserMapper;
import com.kim.community.Entity.DiscussPost;
import com.kim.community.Entity.LoginTicket;
import com.kim.community.Entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

//@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setEmail("nowcoder101@sina.com");
        user.setSalt("11111111");
        user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
        user.setCreateTime(new Date());
        int i = userMapper.insertUser(user);
        System.out.println(i);
    }

    @Test
    public void testUpdateUser() {
        int i = userMapper.updatePassword(150, "555555");
        System.out.println(i);
    }

    @Test
    public void testSelectPost() {
        List<DiscussPost> postList = discussPostMapper.selectDiscussPosts(11, 0, 10);
        System.out.println(postList);
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        loginTicketMapper.updateStatus(loginTicket.getTicket(), 1);
    }

    @Test
    public void testSelectLetter() {
        System.out.println(messageMapper.selectConversations(111, 1, 10));
    }
}
