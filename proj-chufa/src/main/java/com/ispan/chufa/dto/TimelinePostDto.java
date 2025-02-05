package com.ispan.chufa.dto;

import java.time.LocalDateTime;

public class TimelinePostDto {
	private Long postId;
    private String content;
    private String link;
    private String status;
    private LocalDateTime timestamp; // 使用 LocalDateTime
    private String title;
    private Long authorId;
    private Long interactionId;
    private LocalDateTime interactionTime; // 使用 LocalDateTime
    private String postType;
    private String authorName;
    private String authorNickname;
	
	public TimelinePostDto(Long postId, String content, String link, String status, LocalDateTime timestamp,
			String title, Long authorId, Long interactionId, LocalDateTime interactionTime, String postType,
			String authorName, String authorNickname) {
		super();
		this.postId = postId;
		this.content = content;
		this.link = link;
		this.status = status;
		this.timestamp = timestamp;
		this.title = title;
		this.authorId = authorId;
		this.interactionId = interactionId;
		this.interactionTime = interactionTime;
		this.postType = postType;
		this.authorName = authorName;
		this.authorNickname = authorNickname;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getAuthorId() {
		return authorId;
	}
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	public Long getInteractionId() {
		return interactionId;
	}
	public void setInteractionId(Long interactionId) {
		this.interactionId = interactionId;
	}
	public LocalDateTime getInteractionTime() {
		return interactionTime;
	}
	public void setInteractionTime(LocalDateTime interactionTime) {
		this.interactionTime = interactionTime;
	}
	public String getPostType() {
		return postType;
	}
	public void setPostType(String postType) {
		this.postType = postType;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorNickname() {
		return authorNickname;
	}
	public void setAuthorNickname(String authorNickname) {
		this.authorNickname = authorNickname;
	}
	


}
