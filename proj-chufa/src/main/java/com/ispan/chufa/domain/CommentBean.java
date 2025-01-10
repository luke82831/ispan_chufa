package com.ispan.chufa.domain;

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
    private String Title; // 留言_留言狀態
    private java.util.Date UserID; // 留言_留言者id (FK)一對多，多方
    private String CreatedAt; // 留言_創建時間
    private String UpdatedAt; // 留言_更新時間
    private String Content; // 留言_留言內文
    private String ParentID; // 留言_上層留言id

    @Override
    public String toString() {
        return "CommentBean [CommentID=" + CommentID + ", PostID=" + PostID + ", Title=" + Title + ", UserID=" + UserID
                + ", CreatedAt=" + CreatedAt + ", UpdatedAt=" + UpdatedAt + ", Content=" + Content + ", ParentID="
                + ParentID + "]";
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public java.util.Date getUserID() {
        return UserID;
    }

    public void setUserID(java.util.Date userID) {
        UserID = userID;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

}
