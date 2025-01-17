package com.ispan.chufa.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ispan.chufa.domain.PostBean;

public class InteractionDTO {
	private String status;
	private String message;
	private boolean success;
	private Long actionId; // 互動行為 ID
	private MemberInfo member; // 使用者資料 (多對一關聯
	@JsonIgnore
	private PostBean post; // 貼文 (多對一關聯)
	private String interactionType; // 收藏(COLLECT)、轉發(SHARE)、點讚(LIKE)
	private LocalDateTime interactionTime; // 互動行為時間
	private PostDTO postdto; // 貼文 (多對一關聯)
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PostDTO getPostdto() {
		return postdto;
	}

	public void setPostdto(PostDTO postdto) {
		this.postdto = postdto;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public MemberInfo getMember() {
		return member;
	}

	public void setMember(MemberInfo memberDTO) {
		this.member = memberDTO;
	}

	public PostBean getPost() {
		return post;
	}

	public void setPost(PostBean post) {
		this.post = post;
	}

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public LocalDateTime getInteractionTime() {
		return interactionTime;
	}

	public void setInteractionTime(LocalDateTime interactionTime) {
		this.interactionTime = interactionTime;
	}
	

}
