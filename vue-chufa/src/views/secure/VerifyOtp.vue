<template>
  <div class="verify-otp-container">
    <h2>驗證 OTP</h2>
    <form @submit.prevent="verifyOtp">
      <label for="code">驗證碼：</label>
      <input type="text" v-model="otpCode" required />
      <button type="submit">提交</button>
    </form>
    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axiosapi from "@/plugins/axios.js";

const router = useRouter();
const route = useRoute();
const otpCode = ref("");
const email = ref("");
const message = ref("");

// 🔹 確保 `email` 來自 `route.query` 或 `localStorage`
onMounted(() => {
  email.value = route.query.email || localStorage.getItem("resetEmail") || "";
  if (!email.value) {
    message.value = "錯誤：未提供 Email，請重新申請驗證碼";
  }
});

const verifyOtp = async () => {
  if (!email.value) {
    message.value = "錯誤：未提供 Email";
    return;
  }

  try {
    const response = await axiosapi.post("/ajax/secure/verify-code", null, {
      params: { email: email.value, code: otpCode.value },
    });

    // ✅ 存儲 `resetToken`
    localStorage.setItem("resetToken", response.data.resetToken);

    // ✅ 正確跳轉到 `ResetPassword.vue`
    router.push({
      path: "/secure/ResetPassword",
      query: { email: email.value, resetToken: response.data.resetToken },
    });
  } catch (error) {
    message.value = error.response?.data || "驗證碼錯誤或已過期";
  }
};
</script>

<style scoped>
.verify-otp-container {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 40px;
  max-width: 400px;
  margin: 80px auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial,
    sans-serif;
  text-align: center;
}

/* 標題 */
.verify-otp-container h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
}

/* 表單樣式 */
.verify-otp-container form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;
}

/* 標籤 */
.verify-otp-container label {
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

/* 輸入框 */
.verify-otp-container input[type="text"] {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.2s;
  outline: none;
  box-sizing: border-box;
}

.verify-otp-container input[type="text"]:focus {
  border-color: #0070c9;
}

/* 提交按鈕 */
.verify-otp-container button[type="submit"] {
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  border-radius: 8px;
  background-color: #0070c9;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.2s;
}

.verify-otp-container button[type="submit"]:hover {
  background-color: #005fa3;
}

/* 提示訊息 */
.verify-otp-container p {
  margin-top: 20px;
  font-size: 14px;
  color: #333;
}
</style>
