package com.ispan.chufa.dto;

import java.time.LocalDateTime;

// CommentBean 映射到 CommentDTO
public class CommentDTO {
    private Long commentId;
    // PostBean 映射到 PostInfo
    private PostInfo postBean;
    private String commentState;
    // MemberBean 映射到 MemberInfo
    private MemberInfo memberBean;
    private LocalDateTime commentCreatedAt;
    private LocalDateTime commentUpdatedAt;
    private String content;
    private Long parentId;

    @Override
    public String toString() {
        return "CommentBean [commentId=" + commentId + ", postBean=" + postBean + ", commentState=" + commentState
                + ", memberBean=" + memberBean + ", commentCreatedAt=" + commentCreatedAt + ", commentUpdatedAt="
                + commentUpdatedAt + ", content=" + content + ", parentId=" + parentId + "]";
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentState() {
        return commentState;
    }

    public void setCommentState(String commentState) {
        this.commentState = commentState;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public MemberInfo getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(MemberInfo memberBean) {
        this.memberBean = memberBean;
    }

    public PostInfo getPostBean() {
        return postBean;
    }

    public void setPostBean(PostInfo postBean) {
        this.postBean = postBean;
    }

}
