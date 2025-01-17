<template>
    <div class="login-container">
        <div class="login-card">
            <h3 class="login-title">會員登入</h3>
            <form @submit.prevent="login">
                <div class="form-group">
                    <label for="email">電子郵件</label>
                    <input
                        type="text"
                        id="email"
                        v-model="email"
                        class="form-control"
                        placeholder="請輸入電子郵件"
                    />
                    <span v-if="emailError" class="error">{{ emailError }}</span>
                </div>
                <div class="form-group">
                    <label for="password">密碼</label>
                    <input
                        type="password"
                        id="password"
                        v-model="password"
                        class="form-control"
                        placeholder="請輸入密碼"
                    />
                    <span v-if="passwordError" class="error">{{ passwordError }}</span>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn-primary">登入</button>
                </div>
                <div v-if="serverError" class="server-error">
                    {{ serverError }}
                </div>
                <div class="login-footer">
                    <router-link to="/secure/register" class="register-link">尚未註冊？點此註冊</router-link>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import axiosapi from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const email = ref('');
const password = ref('');
const emailError = ref('');
const passwordError = ref('');
const serverError = ref('');

async function login() {
    emailError.value = '';
    passwordError.value = '';
    serverError.value = '';

    let valid = true;
    if (!/^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/.test(email.value)) {
        emailError.value = '請輸入有效的電子郵件地址';
        valid = false;
    }
    if (password.value.length < 6) {
        passwordError.value = '密碼長度至少為 6 個字元';
        valid = false;
    }
    if (!valid) return;

    const body = {
        email: email.value,
        password: password.value,
    };

    try {
        const response = await axiosapi.post('/ajax/secure/login', body);

        if (response.data.success) {
            const token = response.data.token;

            if (token) {
                // 存儲 Token 到 localStorage
                localStorage.setItem('token', token);
                console.log('Token successfully stored:', localStorage.getItem('token'));

                // 設置 Axios 預設的 Authorization Header
                axiosapi.defaults.headers.Authorization = `Bearer ${token}`;
                console.log('Authorization header set for axios:', axiosapi.defaults.headers.Authorization);

                await Swal.fire({
                    title: response.data.message,
                    icon: 'success',
                });

                // 跳轉到會員資料頁面
                router.push({ path: '/secure/Profile' });
            } else {
                console.error('Token is missing from the server response.');
                Swal.fire({
                    title: '登入失敗: 無法獲取 Token',
                    icon: 'error',
                });
            }
        } else {
            serverError.value = response.data.message;
            Swal.fire({
                title: response.data.message,
                icon: 'warning',
            });
        }
    } catch (error) {
        console.error('Error during login:', error);
        serverError.value = '伺服器錯誤，請稍後再試！';
        Swal.fire({
            title: '執行失敗:' + error.message,
            icon: 'error',
        });
    }
}
</script>

<style scoped>
/* 主容器 */
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(to bottom, rgba(246, 248, 252, 0.6), rgba(222, 228, 236, 0.6)),
    url('@/assets/0114-3.jpg');
    background-size: cover;
    background-position: center;
    font-family: 'Roboto', sans-serif;
}

/* 卡片樣式 */
.login-card {
    width: 360px;
    padding: 30px;
    border-radius: 10px;
    background: #ffffff;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    text-align: center;
}

/* 標題樣式 */
.login-title {
    font-size: 24px;
    font-weight: bold;
    color: #4a4a4a;
    margin-bottom: 20px;
}

/* 表單組樣式 */
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

.form-group .form-control {
    width: 100%;
    padding: 10px;
    border: 1px solid #cccccc;
    border-radius: 5px;
    font-size: 14px;
}

.form-group .form-control:focus {
    border-color: #86b7fe;
    outline: none;
    box-shadow: 0 0 5px rgba(134, 183, 254, 0.5);
}

/* 錯誤訊息樣式 */
.error {
    font-size: 12px;
    color: #ff4d4d;
    margin-top: 5px;
    display: block;
}

/* 登入按鈕 */
.btn-primary {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    font-weight: bold;
    color: #ffffff;
    background: linear-gradient(to bottom, #8cbef0, #5e99d9);
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background 0.3s;
}

.btn-primary:hover {
    background: linear-gradient(to bottom, #7faedf, #4a89c4);
}

/* 伺服器錯誤樣式 */
.server-error {
    margin-top: 10px;
    font-size: 14px;
    color: #ff4d4d;
}

/* 底部註冊連結 */
.login-footer {
    margin-top: 20px;
}

.register-link {
    font-size: 14px;
    color: #4a89c4;
    text-decoration: none;
    transition: color 0.3s;
}

.register-link:hover {
    color: #3a6ea1;
}
</style>
