package com.ispan.chufa.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ispan.chufa.domain.InteractionBean;
import com.ispan.chufa.domain.Post;



public class InteractionDTO {
	private InteractionBean interactionbean;
	private String message;
	private boolean success; private Long actionId; // 互動行為 ID
    private MemberInfo member; // 使用者資料 (多對一關聯)
    private Post post; // 貼文 (多對一關聯)
    private String interactionType; // 收藏(COLLECT)、轉發(SHARE)、點讚(LIKE)
    private LocalDateTime interactionTime; // 互動行為時間
    
	public InteractionBean getInteractionbean() {
		return interactionbean;
	}
	public void setInteractionbean(InteractionBean interactionBean) {
		this.interactionbean = interactionBean;
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
	public void setMember(MemberInfo member) {
		this.member = member;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
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
