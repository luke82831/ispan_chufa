<template>
    <div>
    <h3>關注</h3>
        <!-- <ul>
        <li v-for="post in posts" :key="post.id">
            <h4>{{ post.title }}</h4>
            <p>{{ post.content }}</p>
        </li>
        </ul> -->
    </div>

    <div v-if="posts.length > 0">
    <div v-for="post in posts" :key="post.postid" class="post">
    <router-link :to="{ name: 'PostDetail', params: { id: post.postid } }">
    <h2>{{ post.postTitle }}</h2>
    </router-link>
    <p>{{ post.postid }}</p>
    <p>{{ post.postContent }}</p>
    <p>作者名:{{ post.member.name }}</p>
    <p>發佈時間: {{ formatDate(post.postTime) }}</p>
    <a :href="post.postLink" target="_blank">查看詳情</a>
    <div>喜歡數: {{ post.likeCount }}</div>
    <div>轉發數: {{ post.repostCount }}</div>

</div>
</div>
<div v-else>
<p>沒有找到貼文。</p>
</div>
<div v-else>
    <p>載入中...</p>
</div>


</template>
<script>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router';
import axiosapi from '@/plugins/axios.js';

export default {


    setup() {
    const router = useRouter();
    const isEditing = ref(false);
    const profileLoaded = ref(false);
    const member = ref({});
    const posts = ref([]);  // 用來儲存會員的貼文
    const isAdmin = ref(false);
    const userId=member.value.userid;

    const formatDate = (date) => {
        if (!date) return '';
        const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
        return new Date(date).toLocaleDateString('zh-TW', options);
    };

    // 獲取會員資料
    const fetchProfile = async () => {
        try {
            const response = await axios.get('/ajax/secure/profile', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
            });
            if (response.data.success) {
            member.value = response.data.user || {};
            isAdmin.value = response.data.user.role === 'ADMIN';
            } else {
            Swal.fire('錯誤', response.data.message, 'error');
            }
            profileLoaded.value = true;
        } catch (error) {
            console.error('Fetch profile failed:', error);
            Swal.fire('錯誤', '無法獲取會員資料', 'error');
        }
    };

    // 獲取會員的貼文
    // 向後端發送請求
    const fetchPosts = async () => {
    try {
    const response = await axiosapi.post('/api/posts/post', {
        followerId:member.value.userid, // 確保使用正確的字段名  
    //   }, {
    //   withCredentials: true,  // 確保發送憑證（如 cookies）
    // }
    }, {
    headers: {
        'Content-Type': 'application/json',  // 设置请求体类型为 JSON
    }
    });

    if (response.data.postdto && response.data.postdto.length > 0) {
      // 如果返回的 postdto 非空，更新 posts 變數
    posts.value = response.data.postdto;
    } else {
      // 如果沒有帖子資料，顯示提示訊息
    Swal.fire('沒有貼文', '沒有關注。', 'info');
    }
} catch (error) {
    console.error('Fetch posts failed:', error);
    Swal.fire('錯誤', '無法取得貼文', 'error');
};
};   
   // 在組件加載時獲取資料
    onMounted(async () => {
        await fetchProfile();  // 先獲取會員資料
        if (member.value.userid) {
          await fetchPosts(member.value.userid);  // 根據會員ID獲取貼文
        }
    });

        return {
        isEditing,
        profileLoaded,
        member,
        posts,
        isAdmin,
        formatDate,
        };
    },
};</script>