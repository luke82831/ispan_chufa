package com.ispan.chufa.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class PostBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 設定為自增
	private Long postid; // 貼文_貼文id
	private String postStatus; // 貼文_貼文狀態
	private String postTitle; // 貼文_貼文標題
	private LocalDateTime postTime; // 貼文_貼文時間
	@Column(columnDefinition = "VARCHAR(MAX)")
	private String postContent; // 貼文_自定義內文
	private String postLink; // 貼文_貼文超連結(文章、影片連結)


	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "userid", nullable = false, foreignKey = @ForeignKey(name = "fk_posts_member"))
	@JsonBackReference
	MemberBean member;
	
	@OneToMany(mappedBy = "postBean")
	private Set<CommentBean> commentBeans;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	// @JsonManagedReference
	private List<InteractionBean> interactions; // 貼文的互動行為

    @ManyToMany
    @JoinTable(
        name = "post_tags", // 中間表名稱
        joinColumns = @JoinColumn(name = "postid"), // 關聯到 PostBean 的主鍵
        inverseJoinColumns = @JoinColumn(name = "tagId") // 關聯到 TagsBean 的主鍵
    )
    private Set<TagsBean> tagsBeans = new HashSet<>();
	
	@ManyToMany(mappedBy = "posts")
	private Set<PlaceBean> place = new HashSet<>();

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

	public MemberBean getMember() {
		return member;
	}

	public void setMember(MemberBean member) {
		this.member = member;
	}

	public Set<CommentBean> getCommentBeans() {
		return commentBeans;
	}

	public void setCommentBeans(Set<CommentBean> commentBeans) {
		this.commentBeans = commentBeans;
	}

	public List<InteractionBean> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<InteractionBean> interactions) {
		this.interactions = interactions;
	}

	public Set<PlaceBean> getPlaces() {
		return place;
	}

	public void setPlaces(Set<PlaceBean> places) {
		this.place = places;
	}

	public String getPostLink() {
		return postLink;
	}

	public void setPostLink(String postLink) {
		this.postLink = postLink;
	}

	public Set<PlaceBean> getPlace() {
		return place;
	}

	public void setPlace(Set<PlaceBean> place) {
		this.place = place;
	}

	public Set<TagsBean> getTagsBeans() {
		return tagsBeans;
	}

	public void setTagsBeans(Set<TagsBean> tagsBeans) {
		this.tagsBeans = tagsBeans;
	}

	@Override
	public String toString() {
		return "PostBean [postid=" + postid + ", postStatus=" + postStatus + ", postTitle=" + postTitle + ", postTime="
				+ postTime + ", postContent=" + postContent + ", postLink=" + postLink + ", member=" + member
				+ ", commentBeans=" + commentBeans + ", interactions=" + interactions + ", tagsBeans=" + tagsBeans
				+ ", place=" + place + "]";
	}


	}
	
