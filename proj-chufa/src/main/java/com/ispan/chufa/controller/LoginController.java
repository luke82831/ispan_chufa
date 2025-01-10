package com.ispan.chufa.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageSource messageSource; // 注入 MessageSource

    // 用於顯示登入頁面
    @GetMapping("/secure/login/controller")
    public String showLoginPage() {
        return "/secure/login"; // 返回登入頁面
    }

    // 處理登入表單提交
    @PostMapping("/secure/login/controller")
    public String login(@RequestParam String email, @RequestParam String password, Model model, HttpSession session,
            Locale locale) {
        if (email == null || email.isEmpty()) {
            addError(model, "login.page.form.email.required", locale);
            return "/secure/login"; // 返回登入頁面
        }

        if (password == null || password.isEmpty()) {
            addError(model, "login.page.form.password.required", locale);
            return "/secure/login"; // 返回登入頁面
        }

        try {
            MemberBean member = memberService.login(email, password);

            // 登入成功，將用戶資訊存入 session
            session.setAttribute("loggedInUser", member);
            return "redirect:/secure/profile"; // 跳轉到主頁或其他頁面
        } catch (Exception e) {
            handleLoginError(e, model, locale);
            return "/secure/login"; // 返回登入頁面
        }
    }

    // 顯示會員資料頁面
    @GetMapping("/secure/profile")
    public String showProfilePage(HttpSession session, Model model) {
        MemberBean member = (MemberBean) session.getAttribute("loggedInUser");
        if (member != null) {
            model.addAttribute("member", member); // 將會員資料傳送到頁面
            return "/secure/profile"; // 返回會員資料頁面
        }
        return "redirect:/secure/login/controller"; // 如果沒登入，重定向到登入頁面
    }

    // 處理會員資料更新
    @PostMapping("/secure/profile/update")
    public String updateProfile(@RequestParam("name") String name, @RequestParam("nickname") String nickname,
            @RequestParam("phoneNumber") String phoneNumber, @RequestParam("email") String email, HttpSession session,
            Model model) {
        MemberBean member = (MemberBean) session.getAttribute("loggedInUser");

        if (member != null) {
            member.setName(name);
            member.setNickname(nickname);
            member.setPhoneNumber(phoneNumber);
            member.setEmail(email);

            // 儲存更新後的資料
            memberService.saveMember(member);

            // 更新 session 中的資料
            session.setAttribute("loggedInUser", member);
            model.addAttribute("member", member); // 更新頁面顯示資料
            model.addAttribute("message", "Profile updated successfully"); // 顯示更新成功訊息
            return "/secure/profile"; // 返回會員資料頁面
        }
        return "redirect:/secure/login/controller"; // 若未登入，跳轉至登入頁面
    }

    // 用於登出
    @GetMapping("/secure/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 清除 session
        return "redirect:/secure/login/controller"; // 登出後跳轉回登入頁面
    }

    // 顯示註冊頁面
    @GetMapping("/secure/register")
    public String showRegisterPage() {
        return "/secure/register"; // 返回註冊頁面
    }

    // 處理註冊表單提交
    @PostMapping("/secure/register")
    public String register(@RequestParam("username") String username, @RequestParam("email") String email,
            @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("name") String name, @RequestParam("birth") String birth, Model model, Locale locale) {

        // 密碼驗證
        if (!password.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "密碼和確認密碼不一致，請重新輸入。");
            return "/secure/register"; // 密碼不匹配，返回註冊頁面
        }

        // 檢查帳號和郵箱是否已存在
        if (memberService.isUsernameExists(username)) {
            model.addAttribute("usernameError", "該帳號已經存在，請選擇其他帳號。");
            return "/secure/register"; // 返回註冊頁面
        }
        if (memberService.isEmailExists(email)) {
            model.addAttribute("emailError", "該電子郵件已經被註冊，請使用其他郵件。");
            return "/secure/register"; // 返回註冊頁面
        }

        MemberBean memberBean = new MemberBean();
        memberBean.setUsername(username);
        memberBean.setEmail(email);
        memberBean.setPassword(password.getBytes()); // 存儲明文密碼
        memberBean.setName(name);
        memberBean.setBirth(java.sql.Date.valueOf(birth)); // 將字串轉換為 Date

        memberService.saveMember(memberBean); // 儲存會員資料

        return "redirect:/secure/login/controller"; // 註冊成功，跳轉到登入頁面
    }

    // 上傳頭像
    @PostMapping("/{id}/upload-profile-picture")
    public String uploadProfilePicture(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            memberService.saveProfilePicture(file, id);
            return "Profile picture uploaded successfully";
        } catch (Exception e) {
            return "Failed to upload profile picture: " + e.getMessage();
        }
    }

    // 處理登入錯誤
    private void handleLoginError(Exception e, Model model, Locale locale) {
        if (e.getMessage().contains("Invalid email")) {
            addError(model, "login.page.form.invalid.email", locale);
        } else if (e.getMessage().contains("Invalid password")) {
            addError(model, "login.page.form.invalid.password", locale);
        } else {
            addError(model, "login.page.form.invalid.credentials", locale);
        }
    }

    // 統一處理錯誤訊息
    private void addError(Model model, String messageKey, Locale locale) {
        String errorMessage = messageSource.getMessage(messageKey, null, locale);
        model.addAttribute("error", errorMessage);
    }
}
