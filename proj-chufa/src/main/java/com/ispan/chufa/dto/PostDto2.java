package com.ispan.chufa.dto;

import java.time.LocalDateTime;

public class PostDto2 {
	    private Long postid;
	    private String postTitle;  
	    private String postContent;
	    private String postlink;
	    private String status;
	    private LocalDateTime postTime;
	    private LocalDateTime interactionTime;
	    private Long interactionId;
	    private String interactionName;
	    private String interactionNickname;
	    private byte[] interactionprofilePicture;
	    private String postType;
	    private Long authorId;
	    private String authorName;
	    private String authorNickname;
	    private byte[] authorprofilePicture;
	    private Long Like;
	    private Long Repost;
	    
	    public PostDto2() {
	        // 无参构造函数
	    }
		
		public PostDto2(Long postid, String postTitle, String postContent, String postlink, String status,
				LocalDateTime postTime, LocalDateTime interactionTime, Long interactionId, String interactionName,
				String interactionNickname, byte[] interactionprofilePicture, String postType, Long authorId,
				String authorName, String authorNickname, byte[] authorprofilePicture) {
			super();
			this.postid = postid;
			this.postTitle = postTitle;
			this.postContent = postContent;
			this.postlink = postlink;
			this.status = status;
			this.postTime = postTime;
			this.interactionTime = interactionTime;
			this.interactionId = interactionId;
			this.interactionName = interactionName;
			this.interactionNickname = interactionNickname;
			this.interactionprofilePicture = interactionprofilePicture;
			this.postType = postType;
			this.authorId = authorId;
			this.authorName = authorName;
			this.authorNickname = authorNickname;
			this.authorprofilePicture = authorprofilePicture;
		}
		
		
		public Long getPostid() {
			return postid;
		}
		public void setPostid(Long postid) {
			this.postid = postid;
		}
		public String getPostTitle() {
			return postTitle;
		}
		public void setPostTitle(String postTitle) {
			this.postTitle = postTitle;
		}
		public String getPostContent() {
			return postContent;
		}
		public void setPostContent(String postContent) {
			this.postContent = postContent;
		}
		public String getPostlink() {
			return postlink;
		}
		public void setPostlink(String postlink) {
			this.postlink = postlink;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public LocalDateTime getpostTime() {
			return postTime;
		}
		public void setpostTime(LocalDateTime postTime) {
			this.postTime = postTime;
		}
		public LocalDateTime getInteractionTime() {
			return interactionTime;
		}
		public void setInteractionTime(LocalDateTime interactionTime) {
			this.interactionTime = interactionTime;
		}
		public Long getInteractionId() {
			return interactionId;
		}
		public void setInteractionId(Long interactionId) {
			this.interactionId = interactionId;
		}
		public String getInteractionName() {
			return interactionName;
		}
		public void setInteractionName(String interactionName) {
			this.interactionName = interactionName;
		}
		public String getInteractionNickname() {
			return interactionNickname;
		}
		public void setInteractionNickname(String interactionNickname) {
			this.interactionNickname = interactionNickname;
		}
		public byte[] getInteractionprofilePicture() {
			return interactionprofilePicture;
		}
		public void setInteractionprofilePicture(byte[] interactionprofilePicture) {
			this.interactionprofilePicture = interactionprofilePicture;
		}
		public String getPostType() {
			return postType;
		}
		public void setPostType(String postType) {
			this.postType = postType;
		}
		public Long getAuthorId() {
			return authorId;
		}
		public void setAuthorId(Long authorId) {
			this.authorId = authorId;
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
		public byte[] getAuthorprofilePicture() {
			return authorprofilePicture;
		}
		public void setAuthorprofilePicture(byte[] authorprofilePicture) {
			this.authorprofilePicture = authorprofilePicture;
		}
		public Long getLike() {
			return Like;
		}
		public void setLike(Long like) {
			Like = like;
		}
		public Long getRepost() {
			return Repost;
		}
		public void setRepost(Long repost) {
			Repost = repost;
		}
		
	    
	    

}
