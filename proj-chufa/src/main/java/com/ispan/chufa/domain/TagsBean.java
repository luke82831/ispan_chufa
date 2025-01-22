package com.ispan.chufa.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class TagsBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 標籤系統_標籤id 設定為自增
    private Long tagId;
    @Column(name = "tag_state", nullable = false) // 標籤系統_標籤狀態 不能NULL
    private String tagState;
    @Column(name = "tag_name", nullable = false, unique = true) // 標籤系統_標籤名稱 不能NULL 唯一值
    private String tagName;
    @Column(name = "tag_created_at", nullable = false) // 標籤系統_創建時間 不能NULL
    private LocalDateTime tagCreatedAt;
    @Column(name = "tag_updated_at") // 標籤系統_更新時間
    private LocalDateTime tagUpdatedAt;

    @ManyToMany(mappedBy = "tagsBeans") // mappedBy 指向 PostBean 中的屬性名稱
    private Set<PostBean> postBeans = new HashSet<>();
    
    @ManyToMany // 多對多 標籤對成員
    @JoinTable(name = "tags_members", // 聯結表名稱
            joinColumns = @JoinColumn(name = "tagsBean_tagId"), // 當前實體的外鍵列
            inverseJoinColumns = @JoinColumn(name = "memberBean_userid") // 關聯實體的外鍵列
    )
    private Set<MemberBean> memberBeans = new HashSet<>();

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

    public Set<MemberBean> getMemberBeans() {
        return memberBeans;
    }

    public void setMemberBeans(Set<MemberBean> memberBeans) {
        this.memberBeans = memberBeans;
    }

	public Set<PostBean> getPostBeans() {
		return postBeans;
	}

	public void setPostBeans(Set<PostBean> postBeans) {
		this.postBeans = postBeans;
	}

	@Override
	public String toString() {
		return "TagsBean [tagId=" + tagId + ", tagState=" + tagState + ", tagName=" + tagName + ", tagCreatedAt="
				+ tagCreatedAt + ", tagUpdatedAt=" + tagUpdatedAt + ", postBeans=" + postBeans + ", memberBeans="
				+ memberBeans + "]";
	}
    
}
