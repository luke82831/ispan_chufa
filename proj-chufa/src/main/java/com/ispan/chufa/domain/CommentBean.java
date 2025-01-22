package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 留言_留言ID 設定為自增
    private Long commentId;

    @ManyToOne // 多對一關係
    @JoinColumn(name = "postid", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_post")) // 留言_貼文id
                                                                                                        // 不能NULL
    private PostBean postBean;

    @Column(name = "commentstate", nullable = false) // 留言_留言狀態 不能NULL
    private String commentState;

    @ManyToOne // 多對一關係
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comments_member")) // 留言_留言者id
                                                                                                           // 不能NULL
    private MemberBean memberBean;

    @Column(name = "comment_created_at", nullable = false) // 留言_創建時間 不能NULL
    private LocalDateTime commentCreatedAt;

    @Column(name = "comment_updated_at") // 留言_更新時間
    private LocalDateTime commentUpdatedAt;

    @Column(name = "content", columnDefinition = "varchar(max) NOT NULL") // 留言_留言內文 指定SQL型態為varchar(max) 不能NULL
    private String content;

    @Column(name = "parentid") // 留言_上層留言id
    private Long parentId;

    //Getter and Setter
    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public PostBean getPostBean() {
        return postBean;
    }

    public void setPostBean(PostBean postBean) {
        this.postBean = postBean;
    }

    public String getCommentState() {
        return commentState;
    }

    public void setCommentState(String commentState) {
        this.commentState = commentState;
    }

    public MemberBean getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(MemberBean memberBean) {
        this.memberBean = memberBean;
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
    
    @Override
    public String toString() {
        return "CommentBean [commentId=" + commentId + ", postBean=" + postBean + ", commentState=" + commentState
                + ", memberBean=" + memberBean + ", commentCreatedAt=" + commentCreatedAt + ", commentUpdatedAt="
                + commentUpdatedAt + ", content=" + content + ", parentId=" + parentId + "]";
    }

}
