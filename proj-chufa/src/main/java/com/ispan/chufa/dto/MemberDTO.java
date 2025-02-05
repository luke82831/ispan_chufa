package com.ispan.chufa.dto;

import java.util.Date;

public class MemberDTO {
	private Long userid;
    private String username;
    private String phoneNumber;
    private String email;
    private String name;
    private String gender;
    private String nickname;
    private byte[] profilePicture;
    private String bio;
    private java.util.Date birth;

  

	public MemberDTO(Long userid, String username, String phoneNumber, String email, String name, String gender,
			String nickname, byte[] profilePicture, String bio, Date birth) {
		super();
		this.userid = userid;
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.name = name;
		this.gender = gender;
		this.nickname = nickname;
		this.profilePicture = profilePicture;
		this.bio = bio;
		this.birth = birth;
	}
	
	

	public byte[] getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}



	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public java.util.Date getBirth() {
		return birth;
	}

	public void setBirth(java.util.Date birth) {
		this.birth = birth;
	}
    
    

}
