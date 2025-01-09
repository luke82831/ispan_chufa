package com.ispan.chufa.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "post")
public class PostBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 設定為自增
	private int postid; // 貼文_貼文id
	
// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
// @JoinColumn(name="post")
//	private List<PostBean> posts; // 關聯到多個 Post
 
//@OneToOne(cascade = CascadeType.PERSIST)
//@JoinColumn(name="travelid")
//Travel travel ;
    private String postStatus; // 貼文_貼文狀態
    private String postTitle; // 貼文_貼文標題
    private java.util.Date postTime; // 貼文_貼文時間
    private String postContent; // 貼文_自定義內文
    private String tags; // 貼文_標籤
    private String postLink; // 貼文_貼文超連結(文章、影片連結)
	public int getPostid() {
		return postid;
	}
	public void setPostid(int postid) {
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
	public java.util.Date getPostTime() {
		return postTime;
	}
	public void setPostTime(java.util.Date postTime) {
		this.postTime = postTime;
	}
	public String getPostContent() {
		return postContent;
	}
	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getPostLink() {
		return postLink;
	}
	public void setPostLink(String postLink) {
		this.postLink = postLink;
	}

}
