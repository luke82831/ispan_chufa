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
          <p><strong>ID:</strong> {{ member.userid }}</p>
          <router-link :to="`/blog/followlist/${member.userid}`" class="follow-link">
            <p><strong>關注人數:</strong> {{ member.followersCount || 0 }}</p>
          </router-link>
        </div>
      </div>

      <!-- Tab Navigation -->
      <div class="tabs-container">
        <button :class="{ active: activeTab === 'myPosts' }" @click="setActiveTab('myPosts')">我的貼文</button>
        <button :class="{ active: activeTab === 'likedPosts' }" @click="setActiveTab('likedPosts')">點讚貼文</button>
        <button :class="{ active: activeTab === 'sharedPosts' }" @click="setActiveTab('sharedPosts')">轉發貼文</button>
        <button :class="{ active: activeTab === 'savedPosts' }" @click="setActiveTab('savedPosts')">收藏貼文</button>
      </div>

      <h3 class="section-title">貼文內容</h3>
      <div v-if="activeTab === 'myPosts'" class="tab-content">
        <ul class="post-list">
          <li v-for="post in posts" :key="post.id" class="post-item">
            <div class="post-author">
              <img 
                v-if="post.repostDTO?.member?.profilePicture" 
                :src="'data:image/jpeg;base64,' + post.member.profile_picture" 
                alt="Repost Author's Profile Picture" 
                class="profile-picture" 
              />
              <div v-else class="default-profile"></div>
              <div class="post-author-name">{{ post.member.name }}</div>
            </div>
            <router-link :to="{ name: 'PostDetail', params: { id: post.postid } }" class="post-link">
              <h4>{{ post.postTitle }}</h4>
            </router-link>
            <p class="post-content">{{ post.postContent }}</p>
            <div class="post-tags">{{ post.tags || '無標籤' }}</div>
          </li>
        </ul>
      </div>

      <!-- Similar sections for liked, shared, and saved posts -->
      <div v-if="activeTab === 'likedPosts'" class="tab-content">
        <div v-if="activeTab === 'likedPosts'" class="tab-content">
  <ul class="post-list">
    <li v-for="post in posts" :key="post.id" class="post-item">
      <div class="post-author">
        <img 
          v-if="post.repostDTO?.member?.profilePicture" 
          :src="'data:image/jpeg;base64,' + post.member.profile_picture" 
          alt="Repost Author's Profile Picture" 
          class="profile-picture" 
        />
        <div v-else class="default-profile"></div>
        <div class="post-author-name">{{ post.member.name }}</div>
      </div>
      <router-link :to="{ name: 'PostDetail', params: { id: post.postid } }" class="post-link">
        <h4>{{ post.postTitle }}</h4>
      </router-link>
      <p class="post-content">{{ post.postContent }}</p>
      <div class="post-tags">{{ post.tags || '無標籤' }}</div>
    </li>
  </ul>
</div>
      </div>
      <div v-if="activeTab === 'sharedPosts'" class="tab-content">
        <p>這裡會顯示你轉發的貼文</p>
      </div>
      <div v-if="activeTab === 'savedPosts'" class="tab-content">
        <p>這裡會顯示你收藏的貼文</p>
      </div>
    </div>
    <div v-else class="loading">
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
    const activeTab = ref('myPosts');  // 用來切換頁籤

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

    const fetchLikedPosts = async () => {
      try {
        const response = await axios.post('/api/posts/post', {
          "likedBy": member.value.userid, // 傳遞當前用戶的 ID
          "sortByTime":true,
        });

        if (response.data.postdto && response.data.postdto.length > 0) {
          posts.value = response.data.postdto; // 更新貼文列表
        } else {
          Swal.fire('沒有點讚的貼文', '你還沒有點讚任何貼文。', 'info');
        }
      } catch (error) {
        console.error('Fetch liked posts failed:', error);
        Swal.fire('錯誤', '無法取得點讚的貼文', 'error');
      }
    };

    const setActiveTab = async (tab) => {
      activeTab.value = tab;

      if (tab === 'likedPosts') {
        await fetchLikedPosts(); // 獲取點讚的貼文
      } else if (tab === 'myPosts') {
        await fetchPosts(); // 獲取我的貼文
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
      setActiveTab,
      activeTab,
    };
  },
};
</script>

<style scoped>
.profile-page {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
  color: #333;
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 1.8em;
  color: #444;
  margin-bottom: 20px;
  border-bottom: 2px solid #007bff;
  padding-bottom: 10px;
  font-weight: 600;
}

.profile-container {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.profile-details {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
}

.profile-picture {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #007bff;
}

.info p {
  margin: 8px 0;
  font-size: 1.1em;
  color: #555;
}

.follow-link {
  text-decoration: none;
  color: #007bff;
  font-weight: 500;
}

.follow-link:hover {
  text-decoration: underline;
}

/* Tab Navigation */
.tabs-container {
  display: flex;
  justify-content: space-between;
  background-color: #f1f1f1;
  border-radius: 30px;
  padding: 10px;
  margin-bottom: 30px;
}

.tabs-container button {
  background: transparent;
  border: none;
  padding: 12px 25px;
  font-size: 1.1em;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #555;
  border-radius: 30px;
}

.tabs-container button:hover {
  background: rgba(0, 123, 255, 0.1);
  color: #007bff;
}

.tabs-container button.active {
  background: #007bff;
  color: white;
  font-weight: 600;
}

/* Post List Styling */
.post-list {
  list-style: none;
  padding: 0;
}

.post-item {
  background: #fff;
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.post-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.default-profile {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #ddd;
}

.post-author-name {
  font-weight: 600;
  color: #007bff;
}

.post-link {
  text-decoration: none;
  color: #333;
}

.post-link h4 {
  margin: 10px 0;
  font-size: 1.4em;
  color: #007bff;
}

.post-content {
  margin: 10px 0;
  font-size: 1.1em;
  color: #555;
  line-height: 1.6;
}

.post-tags {
  font-size: 0.9em;
  color: #777;
  margin-top: 10px;
}

.loading {
  text-align: center;
  font-size: 1.2em;
  color: #777;
  margin-top: 50px;
}
</style>