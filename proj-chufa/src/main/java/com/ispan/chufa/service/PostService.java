package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CommentBean;
import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    // 還沒寫好
    // 創建貼文
    public PostBean createComment(PostBean bean) {
        return postRepository.save(bean);
    }

    // 刪除貼文
    public Boolean deleteComment(Integer id) {
        return null;
    }

    // 用ID查詢貼文
    public CommentBean findById(Integer id) {
        return null;
    }

    // 更新貼文
    public CommentBean updateComment(Integer id, String content) {
        return null;
    }

}
