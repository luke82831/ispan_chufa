package com.ispan.chufa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="followlist")
public class FollowBean {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flid; // 關注編號 ID

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private MemberBean follower; // 關注者 (使用者資料)

    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private MemberBean followed; // 被關注的對象 (使用者資料)

    private LocalDateTime followTime; // 關注時間

    private String followStatus; // 關注狀態 (e.g., "ACTIVE", "BLOCKED")

	public Long getFlid() {
		return flid;
	}

	public void setFlid(Long flid) {
		this.flid = flid;
	}

	public MemberBean getFollower() {
		return follower;
	}

	public void setFollower(MemberBean follower) {
		this.follower = follower;
	}

	public MemberBean getFollowed() {
		return followed;
	}

	public void setFollowed(MemberBean followed) {
		this.followed = followed;
	}

	public LocalDateTime getFollowTime() {
		return followTime;
	}

	public void setFollowTime(LocalDateTime followTime) {
		this.followTime = followTime;
	}

	public String getFollowStatus() {
		return followStatus;
	}

	public void setFollowStatus(String followStatus) {
		this.followStatus = followStatus;
	}
    
    
}
