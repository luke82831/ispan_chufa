<template>
  <div class="forgot-password-container">
    <h2>忘記密碼</h2>
    <form @submit.prevent="sendVerificationCode">
      <label for="email">電子郵件：</label>
      <input type="email" v-model="email" required />
      <button type="submit">發送驗證碼</button>
    </form>
    <p v-if="message">{{ message }}</p>

    <!-- ✅ 修改這一行 -->
    <router-link to="/secure/Login">返回登入</router-link>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axiosapi from "@/plugins/axios.js";

const email = ref("");
const message = ref("");
const router = useRouter(); // 🔹 Vue Router 用於跳轉頁面

const sendVerificationCode = async () => {
  try {
    const response = await axiosapi.post("/ajax/secure/forgot-password", null, {
      params: { email: email.value },
    });

    message.value = response.data;

    // ✅ 成功發送驗證碼後，跳轉到輸入驗證碼的頁面
    setTimeout(() => {
      router.push({ path: "/secure/VerifyOtp", query: { email: email.value } });
    }, 2000);
  } catch (error) {
    message.value = error.response?.data || "發送失敗，請稍後再試";
  }
};
</script>

<style scoped>
.forgot-password-container {
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
.forgot-password-container h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
}

/* 表單樣式 */
.forgot-password-container form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;
}

/* 標籤 */
.forgot-password-container label {
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

/* 輸入框 */
.forgot-password-container input[type="email"] {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  transition: border-color 0.2s;
  outline: none;
  box-sizing: border-box;
}

.forgot-password-container input[type="email"]:focus {
  border-color: #0070c9;
}

/* 按鈕 */
.forgot-password-container button[type="submit"] {
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

.forgot-password-container button[type="submit"]:hover {
  background-color: #005fa3;
}

/* 提示訊息 */
.forgot-password-container p {
  margin-top: 20px;
  font-size: 14px;
  color: #333;
}

/* 返回登入連結 */
.forgot-password-container a {
  display: inline-block;
  margin-top: 20px;
  font-size: 14px;
  color: #0070c9;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-password-container a:hover {
  color: #005fa3;
}
</style>
