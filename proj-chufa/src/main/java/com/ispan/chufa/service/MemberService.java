package com.ispan.chufa.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.repository.MemberRepository;

@Service
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;

	public MemberBean login(String email, String password) {
		if (email != null && email.length() != 0 && 
				password != null && password.length() != 0) {

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
	
    public void register(MemberBean member) {
        // 假設這裡有加密密碼的邏輯
        // member.setPassword(encodePassword(member.getPassword()));
        
        // 儲存會員資料
        memberRepository.save(member);
    }
}
