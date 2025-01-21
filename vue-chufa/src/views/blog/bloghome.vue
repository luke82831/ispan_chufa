<template>
    <div class="profile-page">
    <div v-if="member" class="profile-container">
        <h2 class="section-title">會員資料</h2>
        <div class="profile-details">
        <img :src="member.profile_picture" alt="Profile Picture" v-if="member.profile_picture" class="profile-picture" />
        <div class="info">
            <p><strong>姓名:</strong> {{ member.name }}</p>
            <p><strong>Email:</strong> {{ member.email }}</p>
            <p><strong>生日:</strong> {{ formatDate(member.birth) }}</p>
            <p><strong>角色:</strong> {{ isAdmin ? '管理員' : '普通會員' }}</p>
            <p><strong>ID:</strong> {{ member.userid }}</p>
        </div>
        </div>

        <h3 class="section-title">會員的貼文</h3>
        
        <ul class="post-list" v-if="posts.length > 0">
            <li v-for="post in posts" :key="post.id" class="post-item">
            <h4>{{ post.postTitle }}</h4>
            <p>{{ post.postContent }}</p>
            <h4>{{ post.member.name }}</h4>
            <p>{{ post.tags || '無標籤' }}</p>           
            </li>
        </ul>


        <p v-else>此會員尚無貼文。</p>
        </div>

        <div v-else>
        <p>載入中...</p>
        </div>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from '@/plugins/axios.js';
import Swal from 'sweetalert2';
import { useRouter } from 'vue-router';

export default {
    setup() {
        const router = useRouter();
        const member = ref({});
        const posts = ref([]);
        const isAdmin = ref(false);
        const profileLoaded = ref(false);
  
        const formatDate = (date) => {
        if (!date) return '';
        const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
        return new Date(date).toLocaleDateString('zh-TW', options);
        };
  
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
  
    const fetchPosts = async () => {
        try {
        const response = await axios.post('/api/posts/post', {
            userid: member.value.userid,
        });
  
        if (response.data.postdto && response.data.postdto.length > 0) {
            posts.value = response.data.postdto;
        } else {
            Swal.fire('沒有貼文', '此用戶暫無貼文。', 'info');
        }
        } catch (error) {
        console.error('Fetch posts failed:', error);
        Swal.fire('錯誤', '無法取得貼文', 'error');
        }
      };
  
    onMounted(async () => {
        await fetchProfile();
        if (member.value.userid) {
        await fetchPosts();
        }
    });
  
    return {
        member,
        posts,
        isAdmin,
        profileLoaded,
        formatDate,
        };
    },
};
</script>
  
<style scoped>
  .profile-page {
    font-family: Arial, sans-serif;
    padding: 20px;
    background-color: #f9f9f9;
    color: #333;
  }
  
  .section-title {
    font-size: 1.5em;
    color: #555;
    margin-bottom: 10px;
    border-bottom: 2px solid #ddd;
    padding-bottom: 5px;
  }
  
  .profile-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }
  
  .profile-details {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 20px;
  }
  
  .profile-picture {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .info p {
    margin: 5px 0;
  }
  
  .post-list {
    list-style: none;
    padding: 0;
  }
  
  .post-item {
    background: #fff;
    margin-bottom: 10px;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .post-item h4 {
    margin: 0 0 5px 0;
    color: #007BFF;
  }
  </style>
  