package com.kim.community.Dao;

import com.kim.community.Entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /***
     * 需要編寫動態SQL語句時(在<if></if>中使用), 如果條件有且僅有一個
     * 這個條件參數必須加上 @Param 標註(取別名)
     * */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int postId);

    // 更新評論數量
    int updateCommentCount(int id, int commentCount);
}
