package com.ispan.chufa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.CommentBean;
import com.ispan.chufa.repository.CommentRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    // 創建留言
    public CommentBean createComment(CommentBean bean) {
        if (bean != null && bean.getContent() != "") {
            // 留言_留言ID 設定為自增
            // 留言_貼文id RequestBody獲取controller內設定
            bean.setCommentState("公開"); // 留言_留言狀態 預設(公開)
            // 留言_留言者id RequestBody獲取controller內設定
            bean.setCommentCreatedAt(LocalDateTime.now()); // 留言_創建時間 set現在時間
            // 留言_更新時間 預設NULL
            // 留言_留言內文 RequestBody獲取controller內設定
            // 留言_上層留言id RequestBody獲取controller內設定
        }
        return commentRepository.save(bean);
    }

    // 刪除留言
    public Boolean deleteComment(Long id) {
        if (id != null) {
            if (commentRepository.existsById(id)) {
                commentRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    // 更新留言
    public CommentBean updateComment(Long id, String content) {
        CommentBean bean = findById(id);
        if (bean != null) {
            bean.setCommentUpdatedAt(LocalDateTime.now());// 留言_更新時間 set現在時間
            bean.setContent(content);// 留言_留言內文
            return commentRepository.save(bean);
        } else {
            return null;
        }
    }

    // 更新標籤狀態
    public CommentBean updateCommentState(Long id, String commentState) {
        CommentBean bean = findById(id);
        if (bean != null) {
            bean.setCommentUpdatedAt(LocalDateTime.now());// 留言_更新時間 set現在時間
            bean.setCommentState(commentState);// 標籤系統_標籤名稱 RequestBody獲取
            return commentRepository.save(bean);
        } else {
            return null;
        }
    }

    // 用ID查詢留言
    public CommentBean findById(Long commentId) {
        if (commentId != null) {
            Optional<CommentBean> optional = commentRepository.findById(commentId);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    // 用postId查詢根留言
    public List<CommentBean> findByPostId(Long postid) {
        return commentRepository.findByPostBeanPostid(postid);
    }

    // 用userid查詢根留言
    public List<CommentBean> findByUserid(Long userId) {
        return commentRepository.findByMemberBeanUserid(userId);
    }

    // 用上層查詢留言
    public List<CommentBean> findByParentId(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    // 查詢ID是否存在
    public boolean existsById(Long commentid) {
        return commentRepository.existsById(commentid);
    }
}
