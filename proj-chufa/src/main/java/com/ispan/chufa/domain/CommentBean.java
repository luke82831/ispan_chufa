package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Comments")
public class CommentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 留言_留言ID 設定為自增
    private Integer commentId;

    @Column(name = "post_id", nullable = false) // 留言_貼文id 不能NULL (FK)一對多，多方
    private Integer postId;

    @Column(name = "comment_state", nullable = false) // 留言_留言狀態 不能NULL
    private Integer commentState;

    @Column(name = "user_id", nullable = false) // 留言_留言者id 不能NULL (FK)一對多，多方
    private Integer userId;

    @Column(name = "comment_created_at", nullable = false) // 留言_創建時間 不能NULL
    private LocalDateTime commentCreatedAt;

    @Column(name = "comment_updated_at") // 留言_更新時間
    private LocalDateTime commentUpdatedAt;

    @Column(name = "content", columnDefinition = "varchar(max) NOT NULL") // 留言_留言內文 指定SQL型態為varchar(max) 不能NULL
    private String content;

    @Column(name = "post_id") // 留言_上層留言id
    private Integer parentId;

    @Override
    public String toString() {
        return "CommentBean [commentId=" + commentId + ", postId=" + postId + ", commentState=" + commentState
                + ", userId=" + userId + ", commentCreatedAt=" + commentCreatedAt + ", commentUpdatedAt="
                + commentUpdatedAt + ", content=" + content + ", parentId=" + parentId + "]";
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getCommentState() {
        return commentState;
    }

    public void setCommentState(Integer commentState) {
        this.commentState = commentState;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(LocalDateTime commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
    }

    public LocalDateTime getCommentUpdatedAt() {
        return commentUpdatedAt;
    }

    public void setCommentUpdatedAt(LocalDateTime commentUpdatedAt) {
        this.commentUpdatedAt = commentUpdatedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
