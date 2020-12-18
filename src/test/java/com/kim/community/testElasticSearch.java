package com.kim.community;

import com.kim.community.Dao.DiscussPostMapper;
import com.kim.community.Dao.ElasticSearch.DiscussPostRepository;
import com.kim.community.Entity.DiscussPost;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testElasticSearch {
    @Autowired
    private DiscussPostMapper mapper;
    @Autowired
    private DiscussPostRepository repository;
    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private ElasticsearchOperations operations;

    @Test
    public void testInsert() {
        repository.save(mapper.selectDiscussPostById(241));
        repository.save(mapper.selectDiscussPostById(242));
        repository.save(mapper.selectDiscussPostById(243));
//        repository.save(mapper.selectDiscussPostById(244));
//        System.out.println(new Date());
    }

    @Test
    public void testInsertList() {
        repository.saveAll(mapper.selectDiscussPosts(101, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(102, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(103, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(111, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(112, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(131, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(132, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(133, 0, 100));
        repository.saveAll(mapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = mapper.selectDiscussPostById(231);
        post.setContent("踩坑踩坑踩坑");
        repository.save(post);
    }

    @Test
    public void testSearchRepository() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();

        Page<DiscussPost> page = repository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(3, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();
//        NativeSearchQuery searchQuery = new NativeSearchQuery(new BoolQueryBuilder());
//        searchQuery.setPageable(PageRequest.of(0, 10));
        SearchHits<DiscussPost> searchHits = operations.search(searchQuery, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        List<DiscussPost> postList = new ArrayList<>();
        page.getSearchHits().getTotalHits();
        for (SearchHit<DiscussPost> postSearchHit :
                page) {
            DiscussPost post = (DiscussPost) postSearchHit.getContent();
            if (!postSearchHit.getHighlightField("content").isEmpty()) {
                post.setContent(postSearchHit.getHighlightField("content").get(0));
            }
            if (!postSearchHit.getHighlightField("title").isEmpty()) {
                post.setContent(postSearchHit.getHighlightField("title").get(0));
            }
            postList.add(post);
        }
//        for (SearchHitsIterator<DiscussPost> it = searchHits; it.hasNext(); ) {
//            SearchHit hit = it.next();
//            DiscussPost post = (DiscussPost) hit.getContent();
//            if (!hit.getHighlightField("content").isEmpty()) {
//                post.setContent(hit.getHighlightField("content").get(0).toString());
//            }
//            if (!hit.getHighlightField("title").isEmpty()) {
//                post.setContent(hit.getHighlightField("title").get(0).toString());
//            }
//            postList.add(post);
//        }
        System.out.println(postList.size());
        System.out.println(postList);
    }
}
