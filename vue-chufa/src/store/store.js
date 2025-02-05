import { defineStore } from 'pinia';
import axios from '@/plugins/axios.js';

export const useUserStore = defineStore('user', {
state: () => ({
member: {}, // 儲存會員資料
isLoggedIn: false, // 是否已登入
profileLoaded: false, // 加載完成標記
}),
actions: {
// 初始化會員資料
async fetchProfile() {
try {
const token = localStorage.getItem('token');
if (!token) {
this.logout();
return;
}
const response = await axios.get('/ajax/secure/profile', {
headers: { Authorization: `Bearer ${token}` },
});
if (response.data.success) {
this.member = response.data.user;
this.isLoggedIn = true;
} else {
this.logout();
}
} catch (error) {
console.error('Failed to fetch user profile:', error);
this.logout();
} finally {
this.profileLoaded = true; // 標記為已加載
}

},
// 登出
logout() {
this.$reset(); // 使用 Pinia 提供的 $reset 方法，清空所有狀態
localStorage.removeItem('token'); // 移除 Token
}

},
});