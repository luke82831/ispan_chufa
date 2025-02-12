<template>
  <div class="reset-password-container">
    <h2>重設密碼</h2>
    <form @submit.prevent="resetPassword">
      <label for="newPassword">新密碼：</label>
      <input type="password" v-model="newPassword" required />
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
const newPassword = ref("");
const email = ref("");
const resetToken = ref("");
const message = ref("");

// ✅ 確保 `email` 和 `resetToken` 來自 `route.query` 或 `localStorage`
onMounted(() => {
  email.value = route.query.email || localStorage.getItem("resetEmail") || "";
  resetToken.value = route.query.resetToken || localStorage.getItem("resetToken") || "";

  if (!email.value || !resetToken.value) {
    message.value = "錯誤：缺少必要資訊，請重新申請驗證碼";
  }
});

const resetPassword = async () => {
  if (!email.value || !resetToken.value) {
    message.value = "錯誤：缺少 Email 或驗證 Token";
    return;
  }

  try {
    await axiosapi.post("/ajax/secure/reset-password", null, {
      params: {
        email: email.value,
        resetToken: resetToken.value,
        newPassword: newPassword.value,
      },
    });

    message.value = "密碼已更新，請使用新密碼登入";

    // ✅ 正確跳轉到 `Login.vue`
    setTimeout(() => router.push("/secure/Login"), 2000);
  } catch (error) {
    message.value = error.response?.data || "重設密碼失敗，請稍後再試";
  }
};
</script>

<style scoped>
.reset-password-container {
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

.reset-password-container h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
}

.reset-password-container form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;
}

.reset-password-container label {
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

.reset-password-container input[type="password"] {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.2s;
  outline: none;
  box-sizing: border-box;
}

.reset-password-container input[type="password"]:focus {
  border-color: #0070c9;
}

.reset-password-container button[type="submit"] {
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

.reset-password-container button[type="submit"]:hover {
  background-color: #005fa3;
}

.reset-password-container p {
  margin-top: 20px;
  font-size: 14px;
  color: #333;
}
</style>
