package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    // 還沒寫好
    // 創建貼文
    public PostBean createPost(PostBean bean) {
        // postid 自動產生
        // postStatus 預設"公開"
        bean.setPostStatus("公開");
        // postTitle RequestBody獲得
        // postTime 現在時間
        bean.setPostTime(LocalDateTime.now());
        // postContent RequestBody獲得
        // postLink RequestBody獲得
        // tagsBeans RequestBody獲得
        // commentBeans RequestBody獲得
        // memberBean RequestBody獲得
        return postRepository.save(bean);
    }

    // 刪除貼文
    public Boolean deletePost(Long id) {
        if (id != null) {
            if (postRepository.existsById(id)) {
                postRepository.deleteById(id);
                return true;
            }
        }
        return null;
    }

    // 用ID查詢貼文
    public PostBean findById(Long id) {
        if (id != null) {
            Optional<PostBean> optional = postRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    // 更新貼文
    public PostBean updatePost(Long id, String postTitle, String postContent) {
        PostBean bean = findById(id);
        if (bean != null) {
            // postTitle
            bean.setPostTitle(postTitle);
            // postContent
            bean.setPostContent(postContent);
            return postRepository.save(bean);
        } else {
            return null;
        }
    }
}
