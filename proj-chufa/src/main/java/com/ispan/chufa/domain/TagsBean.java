package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tags")
public class TagsBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 標籤系統_標籤id 設定為自增
    private Integer tagId;
    @Column(name = "tag_name", nullable = false) // 標籤系統_標籤名稱 不能NULL
    private String tagName;
    @Column(name = "tag_created_at", nullable = false) // 標籤系統_創建時間 不能NULL
    private LocalDateTime tagCreatedAt;
    @Column(name = "tag_updated_at") // 標籤系統_更新時間 不能NULL
    private LocalDateTime tagUpdatedAt;

    @Override
    public String toString() {
        return "TagsBean [tagId=" + tagId + ", tagName=" + tagName + ", tagCreatedAt=" + tagCreatedAt
                + ", tagUpdatedAt=" + tagUpdatedAt + "]";
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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

}
