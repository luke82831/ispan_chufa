package com.ispan.chufa.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 發送驗證碼 Email
     * 
     * @param email 使用者 Email
     */
    public void sendVerificationCode(String email) {
        // 產生 6 位數驗證碼
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // 建立 Email 內容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("【CHUFA】忘記密碼驗證碼");
        message.setText("您的驗證碼是：" + otp + "\n請在 5 分鐘內使用，勿洩漏給他人。");

        // 發送 Email
        mailSender.send(message);

        // 存入 Redis，5 分鐘內有效
        redisTemplate.opsForValue().set("EMAIL_OTP_" + email, otp, 5, TimeUnit.MINUTES);
    }

    /**
     * 驗證 OTP 是否正確
     * 
     * @param email     使用者 Email
     * @param inputCode 使用者輸入的驗證碼
     * @return 是否匹配
     */
    public boolean verifyCode(String email, String inputCode) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL_OTP_" + email);
        return storedCode != null && storedCode.equals(inputCode);
    }
}
