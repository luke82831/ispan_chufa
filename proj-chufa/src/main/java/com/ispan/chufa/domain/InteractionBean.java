package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "interaction")
public class InteractionBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long actionId; // 互動行為 ID

	@Column(name = "interaction_type")
	private String interactionType; // 收藏(COLLECT)、轉發(SHARE)、點讚(LIKE)

	@Column(name = "interaction_time")
	private LocalDateTime interactionTime; // 互動行為時間

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
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
