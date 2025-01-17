package com.ispan.chufa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ispan.chufa.domain.MemberBean;

@SpringBootTest
public class MemberServiceTests {
	@Autowired
	private MemberService memberService;
	
	

//    @BeforeEach
//    public void setUp() {
//        // 初始化測試資料，創建一個測試用的會員
//        member = new MemberBean();
//        member.setEmail("test@example.com");
//        member.setUsername("testuser");
//        member.setPassword("12345678".getBytes());  // 不使用加密，直接使用原始密碼
//        member.setRole(MemberBean.Role.USER);
//        memberRepository.save(member); // 儲存到資料庫
//    }
	

    @Test
    public void testLogin_Success() {
        // 驗證使用正確的 email 和 password 是否能登入成功
        MemberBean loggedInMember = memberService.login("test@example.com", "plainPassword");
        assertNotNull(loggedInMember); // 確保登入成功後能取得 MemberBean
        assertEquals("test@example.com", loggedInMember.getEmail()); // 驗證登入的帳號是否正確
    }

    @Test
    public void testLogin_UserNotFound() {
        // 驗證提供不存在的 email 時應該返回 null
        MemberBean loggedInMember = memberService.login("nonexistent@example.com", "anyPassword");
        assertNull(loggedInMember); // 應該返回 null，因為帳號不存在
    }

    @Test
    public void testLogin_IncorrectPassword() {
        // 驗證提供錯誤密碼時應該返回 null
        MemberBean loggedInMember = memberService.login("test@example.com", "wrongPassword");
        assertNull(loggedInMember); // 應該返回 null，因為密碼錯誤
    }

    @Test
    public void testChangePassword_Success() {
        // 驗證密碼更改成功的情況
        boolean isChanged = memberService.changePassword("test@example.com", "plainPassword", "newPassword");
        assertTrue(isChanged); // 應該返回 true，表示密碼更改成功
        
        // 驗證更改後的密碼是否生效
        MemberBean updatedMember = memberService.login("test@example.com", "newPassword");
        assertNotNull(updatedMember); // 應該成功登入
        assertEquals("test@example.com", updatedMember.getEmail());
    }

    @Test
    public void testChangePassword_Fail_WrongOldPassword() {
        // 驗證提供錯誤舊密碼時，密碼無法更新
        boolean isChanged = memberService.changePassword("test@example.com", "wrongPassword", "newPassword");
        assertFalse(isChanged); // 應該返回 false，表示密碼更改失敗（舊密碼錯誤）
    }

    @Test
    public void testChangePassword_Fail_UserNotFound() {
        // 驗證提供不存在的 email 時，密碼無法更新
        boolean isChanged = memberService.changePassword("nonexistent@example.com", "plainPassword", "newPassword");
        assertFalse(isChanged); // 應該返回 false，表示使用者不存在
    }
}