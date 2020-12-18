package com.kim.community.Dao.ElasticSearch;

import com.kim.community.Entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
// 泛型: 需要处理的实体类和实体类的主键类型
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
