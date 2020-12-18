package com.kim.community.Controller;

import com.kim.community.Entity.DiscussPost;
import com.kim.community.Entity.Page;
import com.kim.community.Service.ElasticsearchService;
import com.kim.community.Service.LikeService;
import com.kim.community.Service.UserService;
import com.kim.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    // search?keyword=xxxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        SearchPage<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchResult) {
            Map<String, Object> map = new HashMap<>();
            DiscussPost post = hit.getContent();
            map.put("post", post);
            map.put("user", userService.findUserById(Integer.parseInt(post.getUserId())));
            map.put("likeCount", likeService.findEntityCount(ENTITY_TYPE_POST, post.getId()));
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        page.setPath("/search?keyword=" + keyword);
        page.setRows((int) searchResult.getSearchHits().getTotalHits());
        System.out.println("------------" + page.getCurrent());
        System.out.println("-------------" + page.getTotal());
        System.out.println("-------------" + page.getFrom());
        System.out.println("-------------" + page.getTo());
        return "/site/search";
    }
}
