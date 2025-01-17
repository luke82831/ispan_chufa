<template>
    <div class="register-container">
      <div class="register-card">
        <h3 class="register-title">註冊帳號</h3>
        <form @submit.prevent="register">
          <div class="form-group">
            <label for="username">帳號:</label>
            <input
              type="text"
              id="username"
              v-model="form.username"
              class="form-control"
              placeholder="請輸入帳號"
            />
            <small v-if="errors.username" class="text-danger">{{ errors.username }}</small>
          </div>
          <div class="form-group">
            <label for="email">電子郵件:</label>
            <input
              type="email"
              id="email"
              v-model="form.email"
              class="form-control"
              placeholder="請輸入電子信箱"
            />
            <small v-if="errors.email" class="text-danger">{{ errors.email }}</small>
          </div>
          <div class="form-group">
            <label for="password">密碼:</label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              class="form-control"
              placeholder="請輸入密碼"
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">確認密碼:</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="form.confirmPassword"
              class="form-control"
              placeholder="請再次輸入密碼"
            />
            <small v-if="errors.confirmPassword" class="text-danger">{{ errors.confirmPassword }}</small>
          </div>
          <div class="form-group">
            <label for="name">姓名:</label>
            <input
              type="text"
              id="name"
              v-model="form.name"
              class="form-control"
              placeholder="請輸入姓名"
            />
          </div>
          <div class="form-group">
            <label for="birth">生日:</label>
            <input
              type="date"
              id="birth"
              v-model="form.birth"
              class="form-control"
            />
          </div>
          <div class="form-group">
            <button type="submit" class="btn-primary">註冊</button>
          </div>
        </form>
        <div class="register-footer">
          <router-link to="/secure/login" class="text-link">已有帳號？點此登入</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";

const router = useRouter();

const form = reactive({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
  name: "",
  birth: "",
});

const errors = reactive({
  username: "",
  email: "",
  confirmPassword: "",
});

const validateForm = () => {
  errors.username = "";
  errors.email = "";
  errors.confirmPassword = "";

  let valid = true;

  if (!form.username) {
    errors.username = "帳號為必填項";
    valid = false;
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = "請輸入有效的電子郵件";
    valid = false;
  }

  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = "密碼與確認密碼不一致";
    valid = false;
  }

  return valid;
};

const register = async () => {
  if (!validateForm()) {
    return;
  }

  try {
    // Base64 編碼密碼
    const encodedPassword = btoa(form.password);

    // 準備發送的數據
    const requestData = {
      username: form.username,
      email: form.email,
      password: encodedPassword, // 使用 Base64 編碼的密碼
      name: form.name,
      birth: form.birth || null, // 確保生日為合法格式或 null
    };

    // 發送請求
    const response = await axios.post("/ajax/secure/register", requestData);

    if (response.data.success) {
      Swal.fire("註冊成功", response.data.message, "success");
      router.push("/secure/login");
    } else {
      Swal.fire("註冊失敗", response.data.message, "warning");
    }
  } catch (error) {
    if (error.response && error.response.data) {
      // 後端返回的具體錯誤訊息
      Swal.fire("錯誤", error.response.data.message, "error");
    } else {
      // 通用錯誤處理
      Swal.fire("錯誤", "伺服器發生錯誤，請稍後再試", "error");
    }
    console.error("Registration failed:", error);
  }
};
</script>

  
  <style scoped>
  /* 主容器 */
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(to bottom, #e3f2fd, #bbdefb);
    font-family: "Roboto", sans-serif;
  }
  
  /* 卡片樣式 */
  .register-card {
    width: 400px;
    padding: 30px;
    border-radius: 10px;
    background: #ffffff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
  }
  
  /* 標題樣式 */
  .register-title {
    font-size: 24px;
    font-weight: bold;
    color: #4a4a4a;
    margin-bottom: 20px;
  }
  
  /* 表單樣式 */
  .form-group {
    margin-bottom: 20px;
    text-align: left;
  }
  
  .form-group label {
    font-size: 14px;
    font-weight: bold;
    color: #6b6b6b;
    display: block;
    margin-bottom: 5px;
  }
  
  .form-control {
    width: 100%;
    padding: 10px;
    border: 1px solid #cccccc;
    border-radius: 5px;
    font-size: 14px;
  }
  
  .form-control:focus {
    border-color: #90caf9;
    outline: none;
    box-shadow: 0 0 5px rgba(144, 202, 249, 0.5);
  }
  
  /* 按鈕樣式 */
  .btn-primary {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    font-weight: bold;
    color: #ffffff;
    background: linear-gradient(to right, #64b5f6, #42a5f5);
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .btn-primary:hover {
    background: linear-gradient(to right, #42a5f5, #2196f3);
  }
  
  /* 錯誤訊息樣式 */
  .text-danger {
    font-size: 12px;
    color: #ff4d4d;
  }
  
  /* 底部連結 */
  .register-footer {
    margin-top: 20px;
  }
  
  .text-link {
    font-size: 14px;
    color: #2196f3;
    text-decoration: none;
    transition: color 0.3s;
  }
  
  .text-link:hover {
    color: #1976d2;
  }
  </style>
  