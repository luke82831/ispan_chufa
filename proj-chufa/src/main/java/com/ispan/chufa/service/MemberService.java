package com.ispan.chufa.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	public boolean isEmailRegistered(String email) {
		System.out.println("檢查 Email 是否註冊: " + email);
		System.out.println("結果：" + memberRepository.existsByEmail(email));
		return memberRepository.existsByEmail(email.trim());
	}

	public boolean updatePassword(String email, String newPassword) {
		Optional<MemberBean> optionalMember = memberRepository.findByEmail(email);
		if (optionalMember.isPresent()) {
			MemberBean member = optionalMember.get();
			member.setPassword(newPassword.getBytes()); // ⚠️ 這裡應該加密密碼，未來可以改進
			memberRepository.save(member);
			return true;
		}
		return false;
	}

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
		Optional<MemberBean> optionalMember = memberRepository.findByEmail(email);

		if (optionalMember.isPresent()) {
			MemberBean member = optionalMember.get();

			// ✅ 如果 avatar 是 null，設定預設值並儲存到資料庫
			if (member.getAvatar() == null || member.getAvatar().isEmpty()) {
				member.setAvatar("/images/avatar.jpg");
				memberRepository.save(member); // **存入資料庫**
			}

			return member;
		}

		return null;
	}

	public List<MemberBean> findAllMembers() {
		return memberRepository.findAll();
	}

	public MemberBean findById(Long userid) {
		return memberRepository.findById(userid).orElse(null);
	}

	public MemberBean getUserById(Long userId) {
		return memberRepository.findById(userId).orElse(null);
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

	public void saveMember(MemberBean memberBean) {
		if (memberBean.getPassword() == null || memberBean.getPassword().length < 6) {
			throw new IllegalArgumentException("密碼長度必須至少為 6 個字元");
		}
		if (memberBean.getRole() == null) {
			memberBean.setRole(MemberBean.Role.USER);
		}
		if (memberBean.getAvatar() == null || memberBean.getAvatar().isEmpty()) {
			memberBean.setAvatar("/images/avatar.jpg");
		}
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
		if (member.getAvatar() == null || member.getAvatar().isEmpty()) {
			member.setAvatar("/images/avatar.jpg");
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

	public boolean deleteMemberById(Long memberId) {
		// 1. 先從資料庫查詢是否存在該會員
		Optional<MemberBean> optionalMember = memberRepository.findById(memberId);

		// 2. 判斷是否有找到
		if (optionalMember.isPresent()) {
			// 3. 存在，則刪除並回傳 true
			memberRepository.deleteById(memberId);
			return true;
		} else {
			// 4. 不存在，回傳 false
			return false;
		}
	}

	public MemberBean getMemberById(Long userId) {
		return memberRepository.findById(userId).orElse(null);
	}

	// 分頁方法
	public Page<MemberBean> getMembersWithPagination(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return memberRepository.findAll(pageable);
	}

	public boolean updateMemberEmail(Long memberId, String newEmail) {
		// 依據 memberId 找到會員
		MemberBean member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("會員不存在"));

		// 檢查新的 email 是否為空
		if (newEmail == null || newEmail.trim().isEmpty()) {
			throw new IllegalArgumentException("新電子郵件不可為空");
		}

		// 檢查電子郵件格式（可依需求調整正規表達式）
		if (!newEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			throw new IllegalArgumentException("電子郵件格式錯誤");
		}

		// 檢查該電子郵件是否已被其他會員使用
		MemberBean existingMember = memberRepository.findByEmail(newEmail).orElse(null);
		if (existingMember != null && !existingMember.getUserid().equals(memberId)) {
			throw new IllegalArgumentException("該電子郵件已被其他會員使用");
		}

		// 更新會員電子郵件
		member.setEmail(newEmail);
		memberRepository.save(member);
		return true;
	}

}
