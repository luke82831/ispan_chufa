package com.ispan.chufa.controller;

import java.io.IOException;
import java.util.Base64;
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
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session,
            Locale locale) {
        if (email == null || email.isEmpty()) {
            addError(model, "login.page.form.email.required", locale);
            return "/secure/login";
        }

        if (password == null || password.isEmpty()) {
            addError(model, "login.page.form.password.required", locale);
            return "/secure/login";
        }

        try {
            MemberBean member = memberService.login(email, password);

            // 登入成功，將用戶資訊存入 session
            session.setAttribute("loggedInUser", member);
            return "redirect:/secure/profile";
        } catch (Exception e) {
            handleLoginError(e, model, locale);
            return "/secure/login";
        }
    }

    // 顯示會員資料頁面 (重點：把 byte[] -> Base64)
    @GetMapping("/secure/profile")
    public String showProfilePage(HttpSession session, Model model) {
        MemberBean member = (MemberBean) session.getAttribute("loggedInUser");
        if (member != null) {
            // 如果有存圖片，轉成 Base64 放進 base64Pic
            if (member.getProfilePicture() != null && member.getProfilePicture().length > 0) {
                String encoded = Base64.getEncoder().encodeToString(member.getProfilePicture());
                member.setBase64Pic(encoded);
            } else {
                member.setBase64Pic(null);
            }

            model.addAttribute("member", member);

            // 判斷是否為 ADMIN，傳遞一個布林值給前端
            if (member.getRole() == MemberBean.Role.ADMIN) {
                model.addAttribute("isAdmin", true);
            } else {
                model.addAttribute("isAdmin", false);
            }

            return "/secure/profile"; // 返回會員資料頁面
        }
        return "redirect:/secure/login/controller"; // 沒登入就跳回登入頁面
    }

    // 處理會員資料更新
    @PostMapping("/secure/profile")
    public String updateProfile(
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("gender") String gender,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "profilePicture", required = false) MultipartFile file,
            HttpSession session,
            Model model) {
        MemberBean member = (MemberBean) session.getAttribute("loggedInUser");
        if (member != null) {
            member.setName(name);
            member.setNickname(nickname);
            member.setPhoneNumber(phoneNumber);
            member.setEmail(email);
            member.setGender(gender);

            if (bio != null) {
                member.setBio(bio);
            }

            // 如果有上傳檔案，且檔案不為空，存入資料庫
            try {
                if (file != null && !file.isEmpty()) {
                    member.setProfilePicture(file.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 儲存更新後的資料
            memberService.saveMember(member);

            // 更新 session 中的資料
            session.setAttribute("loggedInUser", member);

            // 更新完後建議使用 Redirect，重新走一次 showProfilePage()
            return "redirect:/secure/profile";
        }
        return "redirect:/secure/login/controller";
    }

    // 用於登出
    @GetMapping("/secure/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/secure/login/controller";
    }

    // 顯示註冊頁面
    @GetMapping("/secure/register")
    public String showRegisterPage() {
        return "/secure/register";
    }

    // 處理註冊表單提交
    @PostMapping("/secure/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("name") String name,
            @RequestParam("birth") String birth,
            Model model,
            Locale locale) {
        // 密碼驗證
        if (!password.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "密碼和確認密碼不一致，請重新輸入。");
            return "/secure/register";
        }

        // 檢查帳號和郵箱是否已存在
        if (memberService.isUsernameExists(username)) {
            model.addAttribute("usernameError", "該帳號已經存在，請選擇其他帳號。");
            return "/secure/register";
        }
        if (memberService.isEmailExists(email)) {
            model.addAttribute("emailError", "該電子郵件已經被註冊，請使用其他郵件。");
            return "/secure/register";
        }

        MemberBean memberBean = new MemberBean();
        memberBean.setUsername(username);
        memberBean.setEmail(email);
        memberBean.setPassword(password.getBytes());
        memberBean.setName(name);
        memberBean.setBirth(java.sql.Date.valueOf(birth));

        // 這裡預設給 USER 權限 (假設你有 Role.USER)
        memberBean.setRole(MemberBean.Role.USER);

        memberService.saveMember(memberBean);

        return "redirect:/secure/login/controller";
    }

    // 上傳頭像 (可有可無)
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
