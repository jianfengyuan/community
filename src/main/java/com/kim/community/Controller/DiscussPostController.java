package com.kim.community.Controller;

import com.kim.community.Annotation.LoginRequired;
import com.kim.community.Entity.Comment;
import com.kim.community.Entity.DiscussPost;
import com.kim.community.Entity.Page;
import com.kim.community.Entity.User;
import com.kim.community.Service.CommentService;
import com.kim.community.Service.DiscussPostService;
import com.kim.community.Service.LikeService;
import com.kim.community.Service.UserService;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "尚未登錄");
        }
        DiscussPost post = new DiscussPost();
        post.setContent(content);
        post.setTitle(title);
        post.setUserId(Integer.toString(user.getId()));
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return CommunityUtil.getJsonString(200, "發佈成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(Integer.parseInt(discussPost.getUserId()));
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);
        model.addAttribute("postLikeCount", likeService.findEntityCount(ENTITY_TYPE_POST, discussPostId));
        int likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("postLikeStatus", likeStatus);
        // 評論分頁信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(discussPost.getCommentCount());
        // 評論: 給帖子的評論
        // 回復: 給評論的評論
        // 以下是 評論 列表
        List<Comment> commentList = commentService.FindCommentByEntity(
                ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment :
                    commentList) {
                // 每個評論的VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                commentVo.put("commentLikeCount", likeService.findEntityCount(ENTITY_TYPE_COMMENT, comment.getId()));
                likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("commentLikeStatus", likeStatus);
                // 每個回覆的 VO
                // 每個評論的 回覆 VO 列表
                List<Comment> replyList = commentService.FindCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply :
                            replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回覆目標
                        System.out.println(reply);
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        System.out.println(target);
                        replyVo.put("target", target);
                        replyVo.put("replyLikeCount", likeService.findEntityCount(ENTITY_TYPE_COMMENT, reply.getId()));
                        likeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("replyLikeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
