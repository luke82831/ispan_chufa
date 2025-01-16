package com.ispan.chufa.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class MemberBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成流水號
	private Long userid;

	@Enumerated(EnumType.STRING) // 使用 String 儲存枚舉的值（"ADMIN" 或 "USER"）
	@Column(name = "role", nullable = false) // 身分欄位，必填
	private Role role; // 用來表示身分

	@Column(name = "username", nullable = false, unique = true) // 自定義帳號
	private String username;

	@JsonIgnore // 讓Response不會印出
	@Column(name = "password")
	private byte[] password;

	@Column(name = "phone_number", nullable = false, unique = true) // 手機號碼作為唯一值
	private String phoneNumber;

	@Column(name = "email", nullable = false, unique = true) // 信箱
	private String email;

	@Column(name = "name", nullable = false, length = 20) // 姓名欄位，限制長度
	private String name;

	@Column(name = "gender", nullable = false)
	private String gender;

	@Column(name = "nickname", length = 50) // 暱稱欄位，限制長度
	private String nickname;

	@Lob // 標示為大物件類型，對應資料庫中的 BLOB
	@Column(name = "profile_picture")
	private byte[] profilePicture;

	@Column(name = "bio", columnDefinition = "TEXT") // 自介欄位，使用 TEXT 類型
	private String bio;

	@Column(name = "birth")
	private java.util.Date birth;

	@OneToMany(mappedBy = "memberBean")
	private Set<CommentBean> commentBeans;

	@JsonIgnore
	@OneToMany(mappedBy = "memberBean")
	private Set<PostBean> postBeans;

	@ManyToMany(mappedBy = "memberBeans")
	private Set<TagsBean> tagsBeans = new HashSet<>();

	// Getters and Setters

	public Long getUserid() {
		return userid;
	}

	@Override
	public String toString() {
		return "MemberBean [userid=" + userid + ", role=" + role + ", username=" + username + ", password="
				+ Arrays.toString(password) + ", phoneNumber=" + phoneNumber + ", email=" + email + ", name=" + name
				+ ", gender=" + gender + ", nickname=" + nickname + ", profilePicture="
				+ Arrays.toString(profilePicture) + ", bio=" + bio + ", birth=" + birth + ", commentBeans="
				+ commentBeans + ", postBeans=" + postBeans + ", tagsBeans=" + tagsBeans + "]";
	}

	public Set<CommentBean> getCommentBeans() {
		return commentBeans;
	}

	public void setCommentBeans(Set<CommentBean> commentBeans) {
		this.commentBeans = commentBeans;
	}

	public Set<PostBean> getPostBeans() {
		return postBeans;
	}

	public void setPostBeans(Set<PostBean> postBeans) {
		this.postBeans = postBeans;
	}

	public Set<TagsBean> getTagsBeans() {
		return tagsBeans;
	}

	public void setTagsBeans(Set<TagsBean> tagsBeans) {
		this.tagsBeans = tagsBeans;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	// 定義 Role 枚舉類型作為內部類別
	public enum Role {
		ADMIN, USER; // 管理員和一般會員
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getPassword() {
		return password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
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
		// 檢查 email 是否符合正規表達式
		if (email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
			this.email = email;
		} else {
			throw new IllegalArgumentException("Invalid email format.");
		}
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

	public byte[] getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
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
