package com.ispan.chufa.service;

import java.time.LocalDateTime;

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
            bean.setCommentState(1); // 留言_留言狀態 預設1(顯示)
            // 留言_留言者id RequestBody獲取controller內設定
            bean.setCommentCreatedAt(LocalDateTime.now()); // 留言_創建時間 set現在時間
            // 留言_更新時間 預設NULL
            // 留言_留言內文 RequestBody獲取controller內設定
            // 留言_上層留言id RequestBody獲取controller內設定
        }
        return commentRepository.save(bean);
    }

    // 更新留言
    public CommentBean updateComment(CommentBean bean) {
        if (bean != null && bean.getContent() != "") {
            // 留言_留言ID RequestBody獲取bean 不用修改
            // 留言_貼文id RequestBody獲取bean 不用修改
            // 留言_留言狀態 RequestBody獲取bean 不用修改
            // 留言_留言者id RequestBody獲取bean 不用修改
            // 留言_創建時間 RequestBody獲取bean 不用修改
            bean.setCommentUpdatedAt(LocalDateTime.now());// 留言_更新時間 set現在時間
            // 留言_留言內文 RequestBody獲取controller內設定
            // 留言_上層留言id RequestBody獲取bean 不用修改
        }
        return commentRepository.save(bean);
    }

    // 刪除留言
    public Boolean deleteComment(CommentBean bean) {
        if (bean != null) {
            if (commentRepository.existsById(bean.getCommentId())) {
                commentRepository.deleteById(bean.getCommentId());
                return true;
            }
        }
        return false;
    }
}
