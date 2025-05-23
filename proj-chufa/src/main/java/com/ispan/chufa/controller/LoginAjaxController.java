package com.ispan.chufa.controller;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import com.ispan.chufa.service.EmailService;
import com.ispan.chufa.service.MemberService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/ajax/secure")
@CrossOrigin(origins = "http://192.168.23.77:5173", allowedHeaders = "*", allowCredentials = "true")
public class LoginAjaxController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 忘記密碼 - 發送驗證碼
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        if (!memberService.isEmailRegistered(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("該 Email 未註冊");
        }
        emailService.sendVerificationCode(email);
        return ResponseEntity.ok("驗證碼已發送，請查收 Email");
    }

    // 忘記密碼 - 驗證驗證碼
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        if (!emailService.verifyCode(email, code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("驗證碼錯誤或已過期");
        }
        // 產生短時效 Token
        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("RESET_TOKEN_" + email, resetToken, 10, TimeUnit.MINUTES);
        return ResponseEntity.ok().body(new JSONObject().put("resetToken", resetToken).toString());
    }

    // 忘記密碼 - 重設密碼
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String resetToken,
            @RequestParam String newPassword) {
        String storedToken = redisTemplate.opsForValue().get("RESET_TOKEN_" + email);
        if (storedToken == null || !storedToken.equals(resetToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無效的請求");
        }

        if (memberService.updatePassword(email, newPassword)) {
            redisTemplate.delete("RESET_TOKEN_" + email);
            return ResponseEntity.ok("密碼已更新");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("密碼更新失敗");
    }

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
                        .put("userid", member.getUserid())
                        .put("name", member.getName())
                        .put("email", member.getEmail())
                        .put("nickname", member.getNickname())
                        .put("profile_picture", profilePictureBase64) // 返回 Base64
                        .put("birth", member.getBirth())
                        .put("bio", member.getBio())
                        .put("phone_number", member.getPhoneNumber())
                        .put("username", member.getUsername())
                        .put("gender", member.getGender())
                        .put("role", member.getRole() != null ? member.getRole().toString() : "USER"))
                        .put("socialLinks", member.getSocialLinks());
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
        System.out.println("Received Member Data: " + updatedMember.toString()); // 调试点

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

            // 新增：更新社群連結，如果有傳來 socialLinks 則更新
            if (updatedMember.getSocialLinks() != null) {
                existingMember.setSocialLinks(updatedMember.getSocialLinks());
            }

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
                    .put("socialLinks", existingMember.getSocialLinks()) // 回傳更新後的社群連結
                    .put("role", existingMember.getRole().toString());

            System.out.println("Response JSON: " + responseJson.toString());

            return ResponseEntity.ok(responseJson.toString());

        } catch (IllegalArgumentException e) {
            responseJson.put("success", false);
            responseJson.put("message", e.getMessage());
            return ResponseEntity.status(400).body(responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    // 處理圖片上傳
    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam MultipartFile file,
            @RequestParam String email) {
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

    @GetMapping("/members")
    public ResponseEntity<String> getPagedMembers(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page, // 分頁參數：默認第 0 頁
            @RequestParam(defaultValue = "10") int size // 分頁大小：默認每頁 10 筆)
    ) {
        JSONObject responseJson = new JSONObject();
        try {
            // 1. 權限檢查（確定當前使用者是管理員）
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

            MemberBean currentUser = memberService.findByEmail(email);
            if (currentUser == null || currentUser.getRole() != MemberBean.Role.ADMIN) {
                responseJson.put("success", false);
                responseJson.put("message", "只有管理員可查看所有會員");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            // 2. 獲取全部會員
            // List<MemberBean> allMembers = memberService.findAllMembers();

            // 防範無效參數
            if (page < 0 || size <= 0) {
                responseJson.put("success", false);
                responseJson.put("message", "分頁參數無效");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }
            Page<MemberBean> memberPage = memberService.getMembersWithPagination(page, size);

            // 3. 把會員資料轉成 JSON
            JSONArray usersArray = new JSONArray();
            for (MemberBean mb : memberPage.getContent()) {
                JSONObject userJson = new JSONObject();
                userJson.put("userid", mb.getUserid());
                userJson.put("name", mb.getName());
                userJson.put("email", mb.getEmail());
                userJson.put("phone_number", mb.getPhoneNumber());
                userJson.put("gender", mb.getGender());
                userJson.put("role", mb.getRole() != null ? mb.getRole().toString() : "USER");
                // 這裡可以再加上更多要顯示的欄位
                usersArray.put(userJson);
            }

            responseJson.put("success", true);
            responseJson.put("users", usersArray);
            responseJson.put("currentPage", memberPage.getNumber());
            responseJson.put("totalPages", memberPage.getTotalPages());
            responseJson.put("totalElements", memberPage.getTotalElements());

            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId,
            @RequestHeader("Authorization") String authorizationHeader) {
        JSONObject responseJson = new JSONObject();
        try {
            // 權限檢查，確認當前使用者是管理員，類似 updateMemberRole
            // ...

            // 呼叫 Service
            boolean deleted = memberService.deleteMemberById(memberId);
            if (deleted) {
                responseJson.put("success", true);
                responseJson.put("message", "會員已刪除");
            } else {
                responseJson.put("success", false);
                responseJson.put("message", "會員不存在，無法刪除");
            }
            return ResponseEntity.ok(responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

    @PutMapping("/members/{memberId}/email")
    public ResponseEntity<?> updateMemberEmail(
            @PathVariable Long memberId,
            @RequestBody String emailRequestBody,
            @RequestHeader("Authorization") String authorizationHeader) {
        JSONObject responseJson = new JSONObject();
        try {
            // 1. 驗證 Authorization Header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                responseJson.put("success", false);
                responseJson.put("message", "缺少或無效的 Authorization header");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }
            String token = authorizationHeader.substring(7);
            String currentUserEmail = jsonWebTokenUtility.validateToken(token);
            if (currentUserEmail == null) {
                responseJson.put("success", false);
                responseJson.put("message", "無效或過期的 Token");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            // 2. 驗證操作使用者是否為管理員（這裡假設只有管理員可以更新會員資料）
            MemberBean currentUser = memberService.findByEmail(currentUserEmail);
            if (currentUser == null || currentUser.getRole() != MemberBean.Role.ADMIN) {
                responseJson.put("success", false);
                responseJson.put("message", "只有管理員可以更新會員電子郵件");
                return ResponseEntity.status(403).body(responseJson.toString());
            }

            // 3. 從 RequestBody 中解析新的 email
            JSONObject requestJson = new JSONObject(emailRequestBody);
            String newEmail = requestJson.optString("email");
            if (newEmail == null || newEmail.trim().isEmpty()) {
                responseJson.put("success", false);
                responseJson.put("message", "請輸入新的電子郵件");
                return ResponseEntity.badRequest().body(responseJson.toString());
            }

            // 4. 呼叫 Service 層的方法更新會員電子郵件
            memberService.updateMemberEmail(memberId, newEmail);

            responseJson.put("success", true);
            responseJson.put("message", "會員電子郵件更新成功");
            return ResponseEntity.ok(responseJson.toString());
        } catch (IllegalArgumentException e) {
            responseJson.put("success", false);
            responseJson.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseJson.toString());
        } catch (EntityNotFoundException e) {
            responseJson.put("success", false);
            responseJson.put("message", e.getMessage());
            return ResponseEntity.status(404).body(responseJson.toString());
        } catch (Exception e) {
            responseJson.put("success", false);
            responseJson.put("message", "伺服器發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(responseJson.toString());
        }
    }

}
