package com.ispan.chufa.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.chufa.AccessToken;
import com.ispan.chufa.LineUserInfo;
import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.domain.MemberBean.Role;
import com.ispan.chufa.jwt.JsonWebTokenUtility;
import com.ispan.chufa.service.LineAPIService;
import com.ispan.chufa.service.MemberService;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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
        this.jsonWebTokenUtility = jsonWebTokenUtility;
    }

    /**
     * Step 1: Redirect to LINE authorization page
     */
    @GetMapping("/lineLogin")
    public ResponseEntity<String> lineLogin(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("line-state", state);

        String authorizeUrl = String.format(
                "https://access.line.me/oauth2/v2.1/authorize"
                        + "?response_type=code"
                        + "&client_id=%s"
                        + "&redirect_uri=%s"
                        + "&state=%s"
                        + "&scope=openid%%20profile",
                channelId,
                callbackUrl,
                state);

        System.out.println("Authorize URL: " + authorizeUrl);
        System.out.println("Generated state: " + state);
        return ResponseEntity.ok("redirect:" + authorizeUrl);
    }

    /**
     * Step 2: Handle LINE callback
     */
    @GetMapping("/callback/api/auth/line-login")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session) {
        if (code == null || state == null) {
            System.err.println("Error: Missing 'code' or 'state' parameter.");
            return ResponseEntity.badRequest().body("Missing 'code' or 'state' parameter");
        }

        System.out.println("Received code: " + code);
        System.out.println("Received state: " + state);
        // Validate state for CSRF protection
        String sessionState = (String) session.getAttribute("line-state");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.badRequest().body("Invalid state parameter");
        }

        try {
            // Exchange code for AccessToken
            AccessToken accessToken = lineAPIService.issueToken(code, callbackUrl);
            if (accessToken == null || accessToken.getAccess_token() == null) {
                return ResponseEntity.status(500).body("Failed to get access token from LINE");
            }

            // Fetch user info using AccessToken
            LineUserInfo userInfo = lineAPIService.getUserInfo(accessToken);
            if (userInfo == null) {
                return ResponseEntity.status(500).body("Failed to get user info from LINE");
            }

            // Check if user exists in the database
            MemberBean member = memberService.findByEmail(userInfo.getEmail());
            if (member == null) {
                // Create a new user if not exists
                member = new MemberBean();
                member.setEmail(userInfo.getEmail());
                member.setName(userInfo.getDisplayName());
                member.setRole(Role.USER); // Default role
                member.setUsername(userInfo.getEmail()); // 默认使用 email 作为用户名
                member.setPassword(new byte[0]); // 设置空密码
                member.setPhoneNumber("N/A"); // 如果手机号是非空字段，设置默认值

                memberService.saveMember(member);
            }

            // Generate JWT Token for the user
            String token = jsonWebTokenUtility.createToken(member.getEmail());

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登入成功");
            response.put("token", token);

            Map<String, Object> userData = new HashMap<>();
            userData.put("email", member.getEmail());
            userData.put("name", member.getName());
            userData.put("role", member.getRole().toString());
            response.put("user", userData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred during LINE login");
        }
    }
}
