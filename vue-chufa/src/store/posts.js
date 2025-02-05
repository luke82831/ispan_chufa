import { defineStore } from 'pinia';
import axios from '@/plugins/axios.js';
import axiosapi from '@/plugins/axios'; // 用於抓取貼文的 axios 實例
import { ref } from 'vue';

export const usePostsStore = defineStore('user', {
    
    state: () => ({
        member: {}, // 儲存會員資料
        isLoggedIn: false, // 是否已登入
        profileLoaded: false, // 加載完成標記
        posts: [], // 儲存用戶的貼文資料
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
            console.log("whywhywhy")
            const response = await axiosapi.get('/ajax/secure/profile', {
            headers: { Authorization: `Bearer ${token}` },
            });
            if (response.data.success) {
            this.member = response.data.user;
            console.log("hihi"+this.member.username)
            this.isLoggedIn = true;
            console.log(this.isLoggedIn)
            } else {
            this.logout();
            }
        } catch (error) {
            console.error('Failed to fetch user profile:', error);
            this.logout();
        } finally {
            this.profileLoaded = true; // 標記為已加載
            console.log(this.profileLoaded)
        }
        },

    

        // 登出
        logout() {
        this.$reset(); // 清空所有狀態
        localStorage.removeItem('token'); // 移除 Token
        },
    },
});
