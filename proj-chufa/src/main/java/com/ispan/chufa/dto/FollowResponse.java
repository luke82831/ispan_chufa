package com.ispan.chufa.dto;

import java.time.LocalDateTime;

public class FollowResponse {
	private String message;
	private boolean success;
	private Long follower; // 關注者ID
	private Long followed; // 被關注者ID
	private LocalDateTime followTime; // 關注時間
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getFollowerId() {
		return follower;
	}
	public void setFollowerId(Long followerId) {
		this.follower = followerId;
	}
	public Long getFollowedId() {
		return followed;
	}
	public void setFollowedId(Long followedId) {
		this.followed = followedId;
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
