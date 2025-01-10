package com.ispan.chufa.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Comments")
public class CommentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 設定為自增
    private Integer CommentID; // 留言_留言ID
    private Integer PostID; // 留言_貼文id (FK)一對多，多方
    private Integer Title; // 留言_留言狀態
    private Integer UserID; // 留言_留言者id (FK)一對多，多方
    private LocalDate CommentCreatedAt; // 留言_創建時間
    private LocalDate CommentUpdatedAt; // 留言_更新時間
    private String Content; // 留言_留言內文
    private Integer ParentID; // 留言_上層留言id

    @Override
    public String toString() {
        return "CommentBean [CommentID=" + CommentID + ", PostID=" + PostID + ", Title=" + Title + ", UserID=" + UserID
                + ", CommentCreatedAt=" + CommentCreatedAt + ", CommentUpdatedAt=" + CommentUpdatedAt + ", Content="
                + Content + ", ParentID=" + ParentID + "]";
    }

    public Integer getCommentID() {
        return CommentID;
    }

    public void setCommentID(Integer commentID) {
        CommentID = commentID;
    }

    public Integer getPostID() {
        return PostID;
    }

    public void setPostID(Integer postID) {
        PostID = postID;
    }

    public Integer getTitle() {
        return Title;
    }

    public void setTitle(Integer title) {
        Title = title;
    }

    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer userID) {
        UserID = userID;
    }

    public LocalDate getCommentCreatedAt() {
        return CommentCreatedAt;
    }

    public void setCommentCreatedAt(LocalDate commentCreatedAt) {
        CommentCreatedAt = commentCreatedAt;
    }

    public LocalDate getCommentUpdatedAt() {
        return CommentUpdatedAt;
    }

    public void setCommentUpdatedAt(LocalDate commentUpdatedAt) {
        CommentUpdatedAt = commentUpdatedAt;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Integer getParentID() {
        return ParentID;
    }

    public void setParentID(Integer parentID) {
        ParentID = parentID;
    }

}
