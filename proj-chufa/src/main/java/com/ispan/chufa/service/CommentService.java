package com.ispan.chufa.service;

import java.time.LocalDateTime;
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
    public Boolean deleteComment(Integer id) {
        if (id != null) {
            if (commentRepository.existsById(id)) {
                commentRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    // 用ID查詢留言
    public CommentBean findById(Integer id) {
        if (id != null) {
            Optional<CommentBean> optional = commentRepository.findById(id);
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    // 更新留言
    public CommentBean updateComment(Integer id, String content) {
        CommentBean bean = findById(id);
        if (bean != null) {
            // 留言_留言ID 不用修改
            // 留言_貼文id 不用修改
            // 留言_留言狀態 不用修改
            // 留言_留言者id 不用修改
            // 留言_創建時間 不用修改
            bean.setCommentUpdatedAt(LocalDateTime.now());// 留言_更新時間 set現在時間
            bean.setContent(content);// 留言_留言內文 RequestBody獲取
            // 留言_上層留言id 不用修改
            return commentRepository.save(bean);
        } else {
            return null;
        }
    }

    // 更新標籤狀態
    public CommentBean updateCommentState(Integer id, String commentState) {
        CommentBean bean = findById(id);
        if (bean != null) {
            bean.setCommentUpdatedAt(LocalDateTime.now());// 留言_更新時間 set現在時間
            bean.setCommentState(commentState);// 標籤系統_標籤名稱 RequestBody獲取
            return commentRepository.save(bean);
        } else {
            return null;
        }
    }

    // 查詢ID是否存在
    public boolean existsById(Integer id) {
        return commentRepository.existsById(id);
    }
}
