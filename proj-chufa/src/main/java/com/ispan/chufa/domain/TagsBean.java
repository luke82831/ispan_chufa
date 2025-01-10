package com.ispan.chufa.domain;

import java.time.LocalDate;

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
    private LocalDate TagCreatedAt; // 標籤系統_創建時間
    private LocalDate TagUpdatedAt; // 標籤系統_更新時間

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

    public LocalDate getTagCreatedAt() {
        return TagCreatedAt;
    }

    public void setTagCreatedAt(LocalDate tagCreatedAt) {
        TagCreatedAt = tagCreatedAt;
    }

    public LocalDate getTagUpdatedAt() {
        return TagUpdatedAt;
    }

    public void setTagUpdatedAt(LocalDate tagUpdatedAt) {
        TagUpdatedAt = tagUpdatedAt;
    }

}
