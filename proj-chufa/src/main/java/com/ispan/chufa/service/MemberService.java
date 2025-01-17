package com.ispan.chufa.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.PlaceBean;
import com.ispan.chufa.repository.MemberRepository;
import com.ispan.chufa.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PlaceRepository placeRepository;

	public void addPlaceToMember(Long userid, Long placeId) {
		// 查找 Member
		MemberBean member = memberRepository.findById(userid)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 查找 Place
		PlaceBean place = placeRepository.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place not found"));

		// 避免重複添加
		if (!member.getPlaces().contains(place)) {
			member.getPlaces().add(place); // 將 Place 添加到 Member 的 places 列表
			place.getMembers().add(member); // 將 Member 添加到 Place 的 members 列表（雙向關係）
		}

		// 保存更新
		memberRepository.save(member);
		placeRepository.save(place);
	}

	public void removePlaceFromMember(Long userid, Long placeId) {
		// 查找 Member
		MemberBean member = memberRepository.findById(userid)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 查找 Place
		PlaceBean place = placeRepository.findById(placeId)
				.orElseThrow(() -> new EntityNotFoundException("Place not found"));

		// 從雙向關係中移除
		if (member.getPlaces().contains(place)) {
			member.getPlaces().remove(place); // 從 Member 的 places 列表中移除
			place.getMembers().remove(member); // 從 Place 的 members 列表中移除
		}

		// 保存更新
		memberRepository.save(member);
		placeRepository.save(place);
	}

	public MemberBean findByEmail(String email) {
		return memberRepository.findByEmail(email).orElse(null);
	}

	public List<MemberBean> findAllMembers() {
		return memberRepository.findAll();
	}

	public MemberBean findById(Long userid) {
		return memberRepository.findById(userid).orElse(null);
	}

	public MemberBean login(String email, String password) {
		if (email != null && email.length() != 0 && password != null && password.length() != 0) {

			// 通過 email 查詢
			Optional<MemberBean> optional = memberRepository.findByEmail(email);
			if (optional.isPresent()) {
				MemberBean bean = optional.get();
				byte[] pass = bean.getPassword(); // 資料庫抓出的密碼
				byte[] temp = password.getBytes(); // 使用者輸入的密碼
				if (Arrays.equals(pass, temp)) {
					return bean;
				}
			}
		}
		return null;
	}

	public boolean changePassword(String email, String oldPassword, String newPassword) {
		// 使用 email 和舊密碼進行登入驗證
		MemberBean bean = this.login(email, oldPassword);
		if (bean != null) {
			// 將新密碼轉換為 byte[]
			byte[] temp = newPassword.getBytes();
			bean.setPassword(temp); // 設置新密碼
			memberRepository.save(bean); // 保存更新後的密碼
			return true; // 返回 true 表示密碼更新成功
		}
		return false; // 返回 false 表示密碼更新失敗（例如舊密碼錯誤）
	}

	// 檢查帳號是否已存在
	public boolean isUsernameExists(String username) {
		return memberRepository.existsByUsername(username);
	}

	// 檢查電子郵件是否已存在
	public boolean isEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}

	// public void register(MemberBean member) {
	//
	// // 儲存會員資料
	// memberRepository.save(member);
	// }

	public void saveMember(MemberBean memberBean) {
		System.out.println("Saving member: " + memberBean.getEmail() + ", Phone: " + memberBean.getPhoneNumber());
		memberRepository.save(memberBean);
	} // 儲存 MemberBean 物件到資料庫

	public MemberBean updateMember(Long memberId, String nickname, String phoneNumber, String email, String bio) {
		// 根據會員 ID 找到會員
		MemberBean member = memberRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("Member not found"));

		// 更新資料
		if (nickname != null && !nickname.isEmpty()) {
			member.setNickname(nickname);
		}
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			member.setPhoneNumber(phoneNumber);
		}
		if (email != null && !email.isEmpty()) {
			member.setEmail(email); // 確保使用 setter 來執行 email 格式驗證
		}
		if (bio != null && !bio.isEmpty()) {
			member.setBio(bio);
		}

		// 儲存更新後的會員資料
		return memberRepository.save(member);
	}

	public void saveProfilePicture(MultipartFile file, Long memberId) throws IOException {
		byte[] profilePictureBytes = file.getBytes();

		// 根據 ID 找到會員
		MemberBean member = memberRepository.findById(memberId)
				.orElseThrow(() -> new RuntimeException("Member not found"));

		member.setProfilePicture(profilePictureBytes);
		memberRepository.save(member);
	}

	public boolean updateMemberRole(Long memberId, MemberBean.Role newRole) {
		// 查找用户
		MemberBean member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 如果用户角色已经是目标角色，无需更新
		if (member.getRole() == newRole) {
			return false; // 表示角色未改变
		}

		// 更新用户角色
		member.setRole(newRole);
		memberRepository.save(member);
		return true; // 表示角色更新成功
	}
}
