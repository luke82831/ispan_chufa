// stores/profile.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from 'axios';  // 確保你正確導入了 axios
import Swal from 'sweetalert2';

export const useProfileStore = defineStore('profile', () => {
    const member = ref({});  // 會員資料
    const isAdmin = ref(false);  // 是否是管理員
    const userId = ref(null);  // 用戶 ID
    const posts = ref([]);  // 用戶的貼文
    const profileLoaded = ref(false);  // 會員資料是否已加載

    // 取得會員資料
    const fetchProfile = async () => {
        try {
            const response = await axios.get('/ajax/secure/profile', {
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
            });
            if (response.data.success) {
                console.log("haha" + member.value.username);
                member.value = response.data.user || {};
                isAdmin.value = response.data.user.role === 'ADMIN';
                userId.value = member.value.userid;
            } else {
                Swal.fire('錯誤', response.data.message, 'error');
            }
            profileLoaded.value = true;
        } catch (error) {
            console.error('Fetch profile failed:', error);
            Swal.fire('錯誤', '無法取得會員資料', 'error');
        }
    };

    // 取得會員的貼文
    const fetchPosts = async () => {
        const currentUserId = member.value.userid;
        if (!currentUserId) {
            console.error('User ID not found');
            return;
        }
        try {
            const response = await axios.post('/api/posts/post', {
                followerId: currentUserId,
                repost: true,
                sortByTime: true,
            }, {
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (response.data.postdto && response.data.postdto.length > 0) {
                posts.value = response.data.postdto;  // 儲存貼文數據
            } else {
                Swal.fire('沒有貼文', '沒有關注。', 'info');
            }
        } catch (error) {
            console.error('Fetch posts failed:', error);
            Swal.fire('錯誤', '無法取得貼文', 'error');
        }
    };

    return {
        member,
        isAdmin,
        userId,
        posts,
        profileLoaded,
        fetchProfile,
        fetchPosts,
    };
});
