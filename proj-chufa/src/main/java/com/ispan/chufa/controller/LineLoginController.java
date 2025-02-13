package com.ispan.chufa.controller;

import java.net.URI;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.jwt.JsonWebTokenUtility;
import com.ispan.chufa.service.LineAPIService;
import com.ispan.chufa.service.MemberService;
import com.ispan.chufa.util.AccessToken;
import com.ispan.chufa.util.LineUserInfo;
import com.ispan.chufa.util.PasswordUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://192.168.23.105:6173", allowCredentials = "true")
@RequestMapping("/ajax/secure")
public class LineLoginController {

    @Value("${line.login.callback-url}")
    private String callbackUrl;

    @Value("${line.login.channel-id}")
    private String channelId;

    private final LineAPIService lineAPIService;
    private final MemberService memberService;
    private final JsonWebTokenUtility jsonWebTokenUtility;

    // Constructor-based injection
    public LineLoginController(LineAPIService lineAPIService, MemberService memberService,
            JsonWebTokenUtility jsonWebTokenUtility) {
        this.lineAPIService = lineAPIService;
        this.memberService = memberService;
        this.jsonWebTokenUtility = jsonWebTokenUtility; // 初始化 JsonWebTokenUtility
    }

    private RestTemplate template = new RestTemplate();

    /**
     * Step 1: Redirect to LINE authorization page
     */
    @GetMapping("/lineLogin")
    public ResponseEntity<String> lineLogin(HttpSession session) {

        String state = UUID.randomUUID().toString();
        session.setAttribute("line-state", state);

        System.out.println("Session ID (lineLogin): " + session.getId());
        System.out.println("Generated state: " + state);

        String authorizeUrl = ("""
                https://access.line.me/oauth2/v2.1/authorize\
                ?response_type=code\
                &client_id=%s\
                &redirect_uri=%s\
                &state=%s\
                &scope=openid%%20profile\
                """).formatted(
                channelId,
                callbackUrl,
                state);

        URI uri = URI.create(authorizeUrl);

        // RequestEntity<Void> request = RequestEntity.get(uri).build();

        System.out.println("Authorize URL: " + authorizeUrl);
        System.out.println("Generated state: " + state);
        return ResponseEntity.status(302).location(URI.create(authorizeUrl)).build();
    }

    /**
     * Step 2: Handle LINE callback
     */
    @GetMapping("/callback/api/auth/line-login")
    public ResponseEntity<?> callback(@RequestParam String code,
            @RequestParam String state,
            HttpSession session) {
        JSONObject responseJson = new JSONObject(); // 提升变量作用域
        System.out.println("hahahahaha");
        try {
            // 驗證 state
            String sessionState = (String) session.getAttribute("line-state");
            if (sessionState == null || !sessionState.equals(state)) {
                responseJson.put("success", false);
                responseJson.put("message", "Invalid state parameter");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            // 換取 Access Token
            AccessToken accessToken = lineAPIService.issueToken(code, callbackUrl);
            LineUserInfo userInfo = lineAPIService.getUserInfo(accessToken);

            // 處理電子郵件
            String email = userInfo.getEmail();
            if (email == null || email.isBlank()) {
                // 生成虛擬電子郵件
                email = userInfo.getUserId() + "@example.com";
                System.out.println("Generated virtual email: " + email);
            }

            // 根據 email 查詢用戶
            MemberBean member = memberService.findByEmail(email);
            if (member == null) {
                // 如果用戶不存在，創建新用戶
                member = new MemberBean();
                member.setEmail(email);
                member.setLineUserId(userInfo.getUserId());
                member.setName(userInfo.getDisplayName());

                // 為 username 賦值
                member.setUsername(email); // 使用 email 作為默認 username

                // 生成隨機密碼並轉換為 byte[]
                String newPassword = PasswordUtil.generateSecurePassword();
                byte[] passwordBytes = newPassword.getBytes(); // 將密碼轉換為 byte[]

                member.setPassword(passwordBytes); // 設置 byte[] 密碼

                memberService.saveMember(member);
            }
            System.out.println("New user created: " + email);

            // 生成 JWT Token
            String token = jsonWebTokenUtility.createToken(email);
            System.out.println("Generated Token: " + token);
            String redirectUrl = "http://192.168.23.105:6173/secure/Profile?token=%s&source=line".formatted(token);

            if (token == null) {
                responseJson.put("success", false);
                responseJson.put("message", "無法生成 Token，请稍后再试");
                return ResponseEntity.status(500).body(responseJson.toString());
            }

            // 保存用户到 Session
            session.setAttribute("loggedInUser", member);

            // 构建响应
            responseJson.put("success", true);
            responseJson.put("message", "登入成功");
            responseJson.put("token", token);
            responseJson.put("user", new JSONObject()
                    .put("name", member.getName())
                    .put("email", member.getEmail())
                    .put("role", member.getRole() != null ? member.getRole().toString() : "USER"));

            // return ResponseEntity.ok(responseJson.toString());
            return ResponseEntity.status(302).location(URI.create(redirectUrl)).build();

            // return
            // ResponseEntity.status(302).location(URI.create("http://localhost:5173/secure/Profile")).build();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                responseJson.put("success", false);
                responseJson.put("message", "伺服器發生错误: " + e.getMessage());
            } catch (Exception jsonException) {
                jsonException.printStackTrace();
            }
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }
}

// System.out.println("Received code1: " + code);
// System.out.println("Received state1: " + state);
