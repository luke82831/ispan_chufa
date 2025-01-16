package com.ispan.chufa.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@ManyToMany(mappedBy = "postBeans")
	private Set<TagsBean> tagsBeans = new HashSet<>();

	@OneToMany(mappedBy = "postBean")
	private Set<CommentBean> commentBeans;

	@JsonIgnoreProperties("commentBeans")
	@ManyToOne // 多對一關係
	@JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_posts_member")) // 外鍵列
	private MemberBean memberBean;

	@Override
	public String toString() {
		return "PostBean [postid=" + postid + ", postStatus=" + postStatus + ", postTitle=" + postTitle + ", postTime="
				+ postTime + ", postContent=" + postContent + ", postLink=" + postLink + ", tagsBeans=" + tagsBeans
				+ ", commentBeans=" + commentBeans + ", memberBean=" + memberBean + "]";
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

	public Set<TagsBean> getTagsBeans() {
		return tagsBeans;
	}

	public void setTagsBeans(Set<TagsBean> tagsBeans) {
		this.tagsBeans = tagsBeans;
	}

	public Set<CommentBean> getCommentBeans() {
		return commentBeans;
	}

	public void setCommentBeans(Set<CommentBean> commentBeans) {
		this.commentBeans = commentBeans;
	}

	public MemberBean getMemberBean() {
		return memberBean;
	}

	public void setMemberBean(MemberBean memberBean) {
		this.memberBean = memberBean;
	}

}
