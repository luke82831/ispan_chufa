package com.ispan.chufa.dto;

public class FollowRequest {
	private Long followerid;
    private Long followedid;
	public Long getFollowerid() {
		return followerid;
	}
	public void setFollowerid(Long followerid) {
		this.followerid = followerid;
	}
	public Long getFollowedid() {
		return followedid;
	}
	public void setFollowedid(Long followedid) {
		this.followedid = followedid;
	}
	@Override
	public String toString() {
		return "FollowRequest [followerid=" + followerid + ", followedid=" + followedid + "]";
	}
    
}
    
