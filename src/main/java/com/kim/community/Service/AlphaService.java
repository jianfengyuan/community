package com.kim.community.Service;

import com.kim.community.Dao.AlphaDao;
import com.kim.community.Dao.DiscussPostMapper;
import com.kim.community.Dao.UserMapper;
import com.kim.community.Entity.DiscussPost;
import com.kim.community.Entity.User;
import com.kim.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service("alphaService")
public class AlphaService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("AlphaService 初始化");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AlphaService 销毁");
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        User user = new User();
        user.setUsername("test 1");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("12312@qq.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        DiscussPost post = new DiscussPost();
        post.setUserId(String.valueOf(user.getId()));
        post.setTitle("test test1");
        post.setContent("testing~");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

//        Integer.valueOf("ABC");
        return "ok!";
    }
}
