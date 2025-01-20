package com.ispan.chufa.controller;

import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.chufa.domain.MemberBean;
import com.ispan.chufa.jwt.JsonWebTokenUtility;
import com.ispan.chufa.service.MemberService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/ajax/secure")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class LoginAjaxController {

    @Autowired
    private MemberService memberService;

    // 添加地點到會員
    @PostMapping("/{userid}/places/{placeId}")
    public ResponseEntity<String> addPlaceToMember(@PathVariable Long userid, @PathVariable Long placeId) {
        try {
            memberService.addPlaceToMember(userid, placeId);
            return ResponseEntity.ok("Place added to member successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // 從會員中移除地點
    @DeleteMapping("/{userid}/places/{placeId}")
    public ResponseEntity<String> removePlaceFromMember(@PathVariable Long userid, @PathVariable Long placeId) {
        try {
            memberService.removePlaceFromMember(userid, placeId);
            return ResponseEntity.ok("Place removed from member successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    // 處理登入請求
    @PostMapping("/login")
    public String login(@RequestBody String requestBody, HttpSession session) {
        JSONObject responseJson = new JSONObject();

        try {
            JSONObject requestJson = new JSONObject(requestBody);
            String email = requestJson.optString("email");
            String password = requestJson.optString("password");

            if (email == null || email.isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "請輸入電子郵件地址");
                return responseJson.toString();
            }

            if (password == null || password.isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "請輸入密碼");
                return responseJson.toString();
            }

            MemberBean member = memberService.login(email, password);
            if (member == null) {
                responseJson.put("success", false);
                responseJson.put("message", "登入失敗，帳號或密碼錯誤");
            } else {
                session.setAttribute("loggedInUser", member);

                String token = jsonWebTokenUtility.createToken(member.getEmail());
                if (token == null) {
                    responseJson.put("success", false);
                    responseJson.put("message", "無法生成 Token，請稍後再試");
                } else {
                    responseJson.put("success", true);
                    responseJson.put("message", "登入成功");
                    responseJson.put("token", token);
                    responseJson.put("user", new JSONObject()
                            .put("name", member.getName())
                            .put("email", member.getEmail())
                            .put("role", member.getRole()));
                }
            }
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
        }

        return responseJson.toString();
    }

    // 顯示會員資料頁面
    @GetMapping("/profile")
    public String showProfilePage(@RequestHeader("Authorization") String authorizationHeader) {
        JSONObject responseJson = new JSONObject();
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                responseJson.put("success", false);
                responseJson.put("message", "缺少或無效的 Authorization header");
                return responseJson.toString();
            }

            String token = authorizationHeader.substring(7);
            String email = jsonWebTokenUtility.validateToken(token);

            if (email == null) {
                responseJson.put("success", false);
                responseJson.put("message", "無效或過期的 Token");
                return responseJson.toString();
            }

            MemberBean member = memberService.findByEmail(email);
            if (member != null) {
                String profilePictureBase64 = null;
                if (member.getProfilePicture() != null) {
                    profilePictureBase64 = "data:image/jpeg;base64," +
                            Base64.getEncoder().encodeToString(member.getProfilePicture());
                }

                responseJson.put("success", true);
                responseJson.put("user", new JSONObject()
                        .put("name", member.getName())
                        .put("email", member.getEmail())
                        .put("nickname", member.getNickname())
                        .put("profile_picture", profilePictureBase64) // 返回 Base64
                        .put("birth", member.getBirth())
                        .put("bio", member.getBio())
                        .put("phone_number", member.getPhoneNumber())
                        .put("username", member.getUsername())
                        .put("gender", member.getGender()));
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "會員資料未找到");
            }
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
        }

        return responseJson.toString();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody MemberBean updatedMember,
            @RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("Received Member Data: " + updatedMember); // 调试点

        JSONObject responseJson = new JSONObject();
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                responseJson.put("success", false);
                responseJson.put("message", "缺少或無效的 Authorization header");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            String token = authorizationHeader.substring(7);
            String email = jsonWebTokenUtility.validateToken(token);
            if (email == null) {
                responseJson.put("success", false);
                responseJson.put("message", "無效或過期的 Token");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            MemberBean existingMember = memberService.findByEmail(email);
            if (existingMember == null) {
                responseJson.put("success", false);
                responseJson.put("message", "會員未找到");
                return ResponseEntity.status(404).body(responseJson.toString());
            }

            // 更新資料
            existingMember.setName(updatedMember.getName());
            existingMember.setNickname(updatedMember.getNickname());
            existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
            existingMember.setGender(updatedMember.getGender());
            existingMember.setBirth(updatedMember.getBirth());
            existingMember.setBio(updatedMember.getBio());

            memberService.saveMember(existingMember);

            responseJson.put("success", true);
            responseJson.put("message", "資料更新成功");
            responseJson.put("user", new JSONObject()
                    .put("name", existingMember.getName())
                    .put("email", existingMember.getEmail())
                    .put("nickname", existingMember.getNickname())
                    .put("phone_number", existingMember.getPhoneNumber())
                    .put("gender", existingMember.getGender())
                    .put("birth", existingMember.getBirth())
                    .put("bio", existingMember.getBio()))
                    .put("role", existingMember.getRole().toString());

            System.out.println("Response JSON: " + responseJson.toString());

            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    // 處理圖片上傳
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email) {
        JSONObject responseJson = new JSONObject();

        try {
            if (file.isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "文件不可為空");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            MemberBean member = memberService.findByEmail(email);
            if (member == null) {
                responseJson.put("success", false);
                responseJson.put("message", "會員未找到");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            member.setProfilePicture(file.getBytes());
            memberService.saveMember(member);

            responseJson.put("success", true);
            responseJson.put("profile_picture",
                    "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(member.getProfilePicture()));
            responseJson.put("message", "圖片上傳成功");
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    // 處理登出請求
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", true);
        responseJson.put("message", "已成功登出");
        return responseJson.toString();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MemberBean newMember) {
        JSONObject responseJson = new JSONObject();
        try {
            // 檢查必填欄位
            if (newMember.getUsername() == null || newMember.getUsername().isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "帳號為必填項");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }
            if (newMember.getEmail() == null || !newMember.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                responseJson.put("success", false);
                responseJson.put("message", "請輸入有效的電子郵件");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }
            if (newMember.getPassword() == null || newMember.getPassword().length == 0) {
                responseJson.put("success", false);
                responseJson.put("message", "密碼為必填項");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }
            if (newMember.getName() == null || newMember.getName().isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "姓名為必填項");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            // 檢查信箱是否已存在
            if (memberService.findByEmail(newMember.getEmail()) != null) {
                responseJson.put("success", false);
                responseJson.put("message", "電子郵件已被註冊");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            // 保存到資料庫
            memberService.saveMember(newMember);

            // 回應成功訊息
            responseJson.put("success", true);
            responseJson.put("message", "註冊成功");
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace(); // 打印異常以便調試
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    @PutMapping("/members/{memberId}/role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Long memberId,
            @RequestBody String roleRequestBody,
            @RequestHeader("Authorization") String authorizationHeader) {
        JSONObject responseJson = new JSONObject();

        try {
            // 验证管理员权限
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                responseJson.put("success", false);
                responseJson.put("message", "缺少或無效的 Authorization header");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            String token = authorizationHeader.substring(7);
            String email = jsonWebTokenUtility.validateToken(token);

            if (email == null) {
                responseJson.put("success", false);
                responseJson.put("message", "無效或過期的 Token");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            // 检查当前用户是否为管理员
            MemberBean currentUser = memberService.findByEmail(email);
            if (currentUser == null || currentUser.getRole() != MemberBean.Role.ADMIN) {
                responseJson.put("success", false);
                responseJson.put("message", "只有管理員可以更改用戶角色");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            // 从请求体中解析新的角色
            JSONObject requestJson = new JSONObject(roleRequestBody);
            String newRoleString = requestJson.optString("role");
            MemberBean.Role newRole;
            try {
                newRole = MemberBean.Role.valueOf(newRoleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                responseJson.put("success", false);
                responseJson.put("message", "無效的角色值");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            // 更新用户角色
            boolean isUpdated = memberService.updateMemberRole(memberId, newRole);

            if (isUpdated) {
                responseJson.put("success", true);
                responseJson.put("message", "用戶角色更新成功");
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "用戶角色未變更");
            }
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

}
