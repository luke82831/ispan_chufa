package com.ispan.chufa.dto;

public class PostInfo {
    private Long postid; // 貼文_貼文id
    private String postStatus; // 貼文_貼文狀態
    private String postTitle; // 貼文_貼文標題

    @Override
    public String toString() {
        return "PostInfo [postid=" + postid + ", postStatus=" + postStatus + ", postTitle=" + postTitle + "]";
    }

    public Long getPostid() {
        return postid;
    }

    public void setPostid(Long postid) {
        this.postid = postid;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

}
