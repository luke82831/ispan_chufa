package com.ispan.chufa.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class JackPostDTO {
    private Long postid; // 貼文_貼文id
    private String postStatus; // 貼文_貼文狀態
    private String postTitle; // 貼文_貼文標題
    private LocalDateTime postTime; // 貼文_貼文時間
    private String postContent; // 貼文_自定義內文
    private String postLink; // 貼文_貼文超連結(文章、影片連結)
    // @JsonIgnore
    private MemberInfo member;
    private JackScheduleDTO scheduleBean;
    // @JsonIgnore
    // private Set<CommentBean> commentBeans;
    // @JsonIgnore
    // private List<InteractionBean> interactions; // 貼文的互動行為
    // @JsonIgnore
    private Set<JackTagsDTO> tagsBeans;
    // @JsonIgnore
    // private Set<PlaceBean> place = new HashSet<>();

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

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public MemberInfo getMember() {
        return member;
    }

    public void setMember(MemberInfo member) {
        this.member = member;
    }

    public JackScheduleDTO getScheduleBean() {
        return scheduleBean;
    }

    public void setScheduleBean(JackScheduleDTO scheduleBean) {
        this.scheduleBean = scheduleBean;
    }

    public Set<JackTagsDTO> getTagsBeans() {
        return tagsBeans;
    }

    public void setTagsBeans(Set<JackTagsDTO> tagsBeans) {
        this.tagsBeans = tagsBeans;
    }

}
