package com.ispan.chufa.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.service.MemberService;

import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageSource messageSource;  // 注入 MessageSource

    // 用於顯示登入頁面
    @GetMapping("/secure/login/controller")
    public String showLoginPage() {
        return "/secure/login"; // 返回登入頁面
    }

    @PostMapping("/secure/login/controller")
    public String login(String email, String password, Model model, HttpSession session, Locale locale) {
        // 檢查 email 和 password 是否為空
        if (email == null || email.isEmpty()) {
            String emailError = messageSource.getMessage("login.page.form.email.required", null, locale); // 使用 MessageSource 獲取訊息
            model.addAttribute("error", emailError);
            return "/secure/login"; // 返回登入頁面
        }

        if (password == null || password.isEmpty()) {
            String passwordError = messageSource.getMessage("login.page.form.password.required", null, locale); // 使用 MessageSource 獲取訊息
            model.addAttribute("error", passwordError);
            return "/secure/login"; // 返回登入頁面
        }

        try {
            // 調用 MemberService 進行用戶驗證
            MemberBean member = memberService.login(email, password);

            // 將用戶資訊存入 session
            session.setAttribute("loggedInUser", member);

            // 登入成功，跳轉到主頁或其他頁面
            return "redirect:/index";

        } catch (Exception e) {
            // 處理登入失敗
            String loginError = messageSource.getMessage("login.page.form.invalid.credentials", null, locale); // 獲取錯誤消息
            model.addAttribute("error", loginError);
            return "/secure/login"; // 返回登入頁面
        }
    }
    
    @GetMapping("/index")
    public String showIndexPage(HttpSession session, Model model) {
        // 從 session 中取得已登入的會員資料
        MemberBean member = (MemberBean) session.getAttribute("loggedInUser");

        if (member != null) {
            model.addAttribute("member", member); // 將會員資料傳遞給模型
            return "/index"; // 返回會員資料頁面
        } else {
            return "redirect:/secure/login/controller"; // 如果沒登入，重定向到登入頁面
        }
    }
    
    // 用於登出
    @GetMapping("/secure/logout")
    public String logout(HttpSession session) {
        // 清除 session 中的用戶資料
        session.invalidate();

        // 登出後重定向回登入頁面
        return "redirect:/secure/login/controller";
    }
    
    // 顯示註冊頁面
    @GetMapping("/secure/register")
    public String showRegisterPage() {
        return "/secure/register"; // 返回註冊頁面
    }

    // 處理註冊表單提交
    @PostMapping("/secure/register")
    public String register(MemberBean member, String confirmPassword, Model model, Locale locale) {
        // 驗證密碼和確認密碼是否一致
        if (!member.getPassword().equals(confirmPassword)) {
            String confirmPasswordError = messageSource.getMessage("register.page.form.password.confirm", null, locale);
            model.addAttribute("error", confirmPasswordError);
            return "/secure/register"; // 返回註冊頁面
        }

        try {
            // 呼叫服務層進行註冊處理
            memberService.register(member);

            // 註冊成功，跳轉到登入頁面
            return "redirect:/secure/login/controller";
        } catch (Exception e) {
            // 處理註冊錯誤
            String registrationError = messageSource.getMessage("register.page.form.error", null, locale);
            model.addAttribute("error", registrationError);
            return "/secure/register"; // 返回註冊頁面
        }
    }
}
