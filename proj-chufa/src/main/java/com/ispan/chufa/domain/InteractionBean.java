package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "interaction")
public class InteractionBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long actionId; // 互動行為 ID

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	// @JsonBackReference
	@JsonManagedReference
	private MemberBean member; // 使用者資料 (多對一關聯)

	@ManyToOne
	@JoinColumn(name = "postid", nullable = false)
	@JsonManagedReference
	private PostBean post; // 貼文 (多對一關聯)

	@Column(name = "interction_type")
	private String interactionType; // 收藏(COLLECT)、轉發(SHARE)、點讚(LIKE)

	@Column(name = "interaction_time")
	private LocalDateTime interactionTime; // 互動行為時間

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public MemberBean getMember() {
		return member;
	}

	public void setMember(MemberBean member) {
		this.member = member;
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
