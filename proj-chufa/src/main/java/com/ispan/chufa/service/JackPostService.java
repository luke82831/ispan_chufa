package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.PostBean;
import com.ispan.chufa.repository.PostRepository;

@Service
public class JackPostService {
    @Autowired
    private PostRepository postRepository;

    // 創建貼文
    public PostBean createPost(PostBean bean) {
        // postStatus 預設"公開"
        bean.setPostStatus("公開");
        // postTime 現在時間
        bean.setPostTime(LocalDateTime.now());
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

    // 查詢ID是否存在
    public boolean existsById(Long id) {
        return postRepository.existsById(id);
    }
}
