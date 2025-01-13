package com.ispan.chufa.dto;

import java.time.LocalDateTime;

public class FollowResponse {
	private String message;
	private boolean success;
	private Long followerId; // 關注者ID
	private Long followedId; // 被關注者ID
	private LocalDateTime followTime; // 關注時間
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getFollowerId() {
		return followerId;
	}
	public void setFollowerId(Long followerId) {
		this.followerId = followerId;
	}
	public Long getFollowedId() {
		return followedId;
	}
	public void setFollowedId(Long followedId) {
		this.followedId = followedId;
	}
	public LocalDateTime getFollowTime() {
		return followTime;
	}
	public void setFollowTime(LocalDateTime followTime) {
		this.followTime = followTime;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	

}
