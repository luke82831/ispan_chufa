package com.ispan.chufa.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.TagsBean;

public class PostDTO {
	private MemberInfo member;
	private Long postid; // 貼文_貼文id
	@JsonIgnore
	private List<InteractionBean> interactions; // 貼文的互動行為
	private String tags; // 貼文_標籤
	@JsonIgnore
	private Set<TagsBean> tag = new HashSet<>();
	private String postStatus; // 貼文_貼文狀態
	private String postTitle; // 貼文_貼文標題
	private LocalDateTime postTime; // 貼文_貼文時間
	private String postContent; // 貼文_自定義內文
	private String postLink; // 貼文_貼文超連結(文章、影片連結)
	private Long likeCount;
	private Long repostCount;
	@JsonIgnoreProperties({ "memberBeans", "postBeans" })
	private Set<TagsBean> tagsBeans = new HashSet<>();
	// private List<CommentDTO>CommentDTO ;
	private boolean likedByCurrentUser;
	private boolean collectByCurrentUser;
	private boolean isRepost;
	private MemberInfo repostMember; // 如果是轉發貼文，則會包含原貼文數據

	private Long forwardedFrom;

	private PostDTO repostDTO;
	

	// public List<CommentDTO> getCommentDTO() {
	// return CommentDTO;
	// }
	//
	// public void setCommentDTO(List<CommentDTO> commentDTO) {
	// CommentDTO = commentDTO;
	// }
	
	
	

	public boolean isRepost() {
		return isRepost;
	}

	public boolean isCollectByCurrentUser() {
		return collectByCurrentUser;
	}

	public void setCollectByCurrentUser(boolean collectByCurrentUser) {
		this.collectByCurrentUser = collectByCurrentUser;
	}

	public boolean isLikedByCurrentUser() {
		return likedByCurrentUser;
	}

	public void setLikedByCurrentUser(boolean likedByCurrentUser) {
		this.likedByCurrentUser = likedByCurrentUser;
	}

	public PostDTO getRepostDTO() {
		return repostDTO;
	}

	public void setRepostDTO(PostDTO repostDTO) {
		this.repostDTO = repostDTO;
	}

	public Long getForwardedFrom() {
		return forwardedFrom;
	}

	public void setForwardedFrom(Long forwardedFrom) {
		this.forwardedFrom = forwardedFrom;
	}

	public void setRepost(boolean isRepost) {
		this.isRepost = isRepost;
	}

	public MemberInfo getRepostMember() {
		return repostMember;
	}

	public void setRepostMember(MemberInfo repostMember) {
		this.repostMember = repostMember;
	}

	public Set<TagsBean> getTagsBeans() {
		return tagsBeans;
	}

	public void setTagsBeans(Set<TagsBean> tagsBeans) {
		this.tagsBeans = tagsBeans;
	}

	public Long getRepostCount() {
		return repostCount;
	}

	public void setRepostCount(Long repostCount) {
		this.repostCount = repostCount;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public Long getPostid() {
		return postid;
	}

	public void setPostid(Long postid) {
		this.postid = postid;
	}

	public List<InteractionBean> getInteractions() {
		return interactions;
	}

	public void setInteractions(List<InteractionBean> interactions) {
		this.interactions = interactions;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	// @JsonProperty("tagId") // 只序列化 memberId
	public Set<TagsBean> getTag() {
		return tag;
	}

	public void setTag(Set<TagsBean> tag) {
		this.tag = tag;
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

}
