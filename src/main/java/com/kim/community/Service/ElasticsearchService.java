package com.kim.community.Service;

import com.kim.community.Dao.ElasticSearch.DiscussPostRepository;
import com.kim.community.Entity.DiscussPost;
import com.kim.community.Entity.Page;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository repository;
    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private ElasticsearchOperations operations;

    public void saveDiscussPost(DiscussPost post) {
        repository.save(post);
    }

    public void deleteDiscussPost(int id) {
        repository.deleteById(id);
    }

    public SearchPage<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();
        SearchHits<DiscussPost> searchHits = operations.search(searchQuery, DiscussPost.class);
        SearchPage<DiscussPost> page = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());
        List<DiscussPost> postList = new ArrayList<>();
        for (SearchHit<DiscussPost> postSearchHit :
                page) {
            DiscussPost post = postSearchHit.getContent();
            if (!postSearchHit.getHighlightField("content").isEmpty()) {
                post.setContent(postSearchHit.getHighlightField("content").get(0));
            }
            if (!postSearchHit.getHighlightField("title").isEmpty()) {
                post.setContent(postSearchHit.getHighlightField("title").get(0));
            }
        }
//        SearchHits<DiscussPost> posts = template.search(searchQuery, DiscussPost.class);
//        List<DiscussPost> postList = new ArrayList<>();
//        for (SearchHitsIterator<DiscussPost> it = posts; it.hasNext(); ) {
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
        return page;
    }
}
