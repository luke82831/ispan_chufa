package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tags")
public class TagsBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 設定為自增
    private Integer TagID; // 標籤系統_標籤id
    private String TagName; // 標籤系統_標籤名稱
    private LocalDateTime TagCreatedAt; // 標籤系統_創建時間
    private LocalDateTime TagUpdatedAt; // 標籤系統_更新時間

    @Override
    public String toString() {
        return "TagsBean [TagID=" + TagID + ", TagName=" + TagName + ", TagCreatedAt=" + TagCreatedAt
                + ", TagUpdatedAt=" + TagUpdatedAt + "]";
    }

    public Integer getTagID() {
        return TagID;
    }

    public void setTagID(Integer tagID) {
        TagID = tagID;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public LocalDateTime getTagCreatedAt() {
        return TagCreatedAt;
    }

    public void setTagCreatedAt(LocalDateTime tagCreatedAt) {
        TagCreatedAt = tagCreatedAt;
    }

    public LocalDateTime getTagUpdatedAt() {
        return TagUpdatedAt;
    }

    public void setTagUpdatedAt(LocalDateTime tagUpdatedAt) {
        TagUpdatedAt = tagUpdatedAt;
    }

}
