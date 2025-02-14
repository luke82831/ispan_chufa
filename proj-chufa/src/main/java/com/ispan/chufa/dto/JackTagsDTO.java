package com.ispan.chufa.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class JackTagsDTO {
    private Long tagId;
    private String tagState;
    private String tagName;
    private LocalDateTime tagCreatedAt;
    private LocalDateTime tagUpdatedAt;
    private Set<PostInfo> postBeans;

    // private Set<MemberBean> memberBeans = new HashSet<>();
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagState() {
        return tagState;
    }

    public void setTagState(String tagState) {
        this.tagState = tagState;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public LocalDateTime getTagCreatedAt() {
        return tagCreatedAt;
    }

    public void setTagCreatedAt(LocalDateTime tagCreatedAt) {
        this.tagCreatedAt = tagCreatedAt;
    }

    public LocalDateTime getTagUpdatedAt() {
        return tagUpdatedAt;
    }

    public void setTagUpdatedAt(LocalDateTime tagUpdatedAt) {
        this.tagUpdatedAt = tagUpdatedAt;
    }

    public Set<PostInfo> getPostBeans() {
        return postBeans;
    }

    public void setPostBeans(Set<PostInfo> postBeans) {
        this.postBeans = postBeans;
    }

    @Override
    public String toString() {
        return "JackTagsDTO [tagId=" + tagId + ", tagState=" + tagState + ", tagName=" + tagName + ", tagCreatedAt="
                + tagCreatedAt + ", tagUpdatedAt=" + tagUpdatedAt + ", postBeans=" + postBeans + "]";
    }

}
