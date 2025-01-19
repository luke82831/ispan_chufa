package com.ispan.chufa.dto;

public class MemberInfo {
	private Long userid;
	private String name;
	private byte[] profilePicture;
	private String nickname;

	public MemberInfo(Long userid, String name, byte[] profilePicture, String nickname) {
		this.userid = userid;
		this.name = name;
		this.profilePicture = profilePicture;
		this.nickname = nickname;
	}

	public MemberInfo() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public byte[] getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

}
