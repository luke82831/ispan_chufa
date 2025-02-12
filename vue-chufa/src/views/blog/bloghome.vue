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
            <p><strong>關注人數:</strong> {{ followersCount }}</p>    
            <p><strong>粉絲:</strong> {{ followingCount }}</p>
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
      <div class="posts-grid">
        <div
          v-for="post in posts"
          :key="post.postid"
          :class="{'hidden-post': postStore.getHiddenReason(post.postid)}"
          style="position: relative;"
        >
          <PostCard
            :post="post"
            :member="member"
            :formatDate="formatDate"
            @update-posts="fetchPosts()"
          />
          <div
            v-if="postStore.getHiddenReason(post.postid)"
            class="hidden-message"
          >
            此文章已被隱藏，原因：{{ postStore.getHiddenReason(post.postid) }}，
            請聯繫客服信箱：Chufa@service.com
          </div>
        </div>
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
import axiosapi from '@/plugins/axios.js';
import PostCard from "@/components/Postcard.vue";
import { useUserStore } from "@/stores/user.js";
import { useRoute } from 'vue-router';
import { usePostStore } from '@/stores/usePostStore';


export default {
  components: {
    PostCard // 註冊 PostCard 元件
  },
  
  setup() {
    const router = useRouter();
    const member = ref({});
    const posts = ref([]);
    const activeTab = ref('myPosts');
    //const postStore = usePostStore();

    const followersCount = ref(0); // 使用 ref 來定義自適應資料
    const followingCount = ref(0);

    const postStore = usePostStore();

    // 用來從後端取得關注者和被關注者的人數
    const fetchCount = async (type) => {
      const url = type === 'followers'
        ? `/follow/followercount/${member.value.userid}`
        : `/follow/followingcount/${member.value.userid}`;

      try {
        const response = await axiosapi.get(url);
        console.log("jdgfbdis"+followersCount)
        // 假設返回的資料是一個物件，包含 `count` 資料欄
        return response.data || 0; // 如果沒有資料則返回0
      } catch (error) {
        console.error(`Error fetching ${type}:`, error);
        return 0;
      }
    }; 

    // 載入關注人數和被關注人數
    const loadCounts = async () => {
      followersCount.value = await fetchCount('followers');
      followingCount.value = await fetchCount('followed');
    };

    const formatDate = (date) => {
      if (!date) return '';
      const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
      return new Date(date).toLocaleDateString('zh-TW', options);
    };

    const fetchProfile = async () => {
      try {
        const response = await axiosapi.get('/ajax/secure/profile', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        if (response.data.success) {
          member.value = response.data.user || {};
        } else {
          Swal.fire('錯誤', response.data.message, 'error');
        }
      } catch (error) {
        console.error('Fetch profile failed:', error);
        Swal.fire('錯誤', '無法獲取會員資料', 'error');
      }
    };

    const fetchPosts = async (filterType) => {
      try {
    const payload = { };
    if (filterType === 'likedPosts') { payload.likedBy = member.value.userid;payload.repost = true; }
    if (filterType === 'savedPosts') { payload.collectBy = member.value.userid;payload.repost = true; }
    if (filterType === 'myPosts') {
      payload.repost = false;  // 不要 repost 的貼文\
      payload.userid=member.value.userid;
    } else if (filterType === 'sharedPosts') {
      payload.repost = true;  // 只要 repost 的貼文
      payload.userid=member.value.userid;
    }
    //userid: member.value.userid 
    const response = await axiosapi.post('/api/posts/post', payload);
    let postData = response.data.postdto || [];

    // 過濾條件
    if (filterType === 'myPosts') {
      posts.value = postData.filter(post => !post.repost && post.repostDTO === null);
    } else if (filterType === 'sharedPosts') {
      posts.value = postData.filter(post => post.repost || post.repostDTO !== null);
    } else {
      posts.value = postData;
    }
  } catch (error) {
    console.error('Fetch posts failed:', error);
    Swal.fire('錯誤', `無法取得${filterType}的貼文`, 'error');
  }
    };

    const setActiveTab = async (tab) => {
      activeTab.value = tab;
      await fetchPosts(tab);
    };

    const getFirstImage = (content) => {
      const match = content.match(/<img[^>]+src="([^">]+)"/);
      return match ? match[1] : null;
    };

    const getTextPreview = (content, length) => {
      // 移除圖片和其他 HTML 標籤
      const textContent = content.replace(/<img[^>]*>/g, "").replace(/<[^>]+>/g, "");
      return textContent.slice(0, length) + (textContent.length > length ? "..." : "");
    };
    const userStore = useUserStore(); // 使用 Pinia 的狀態

    const navigateToDetail = (postid, event) => {
      const excludedElements = [".post-actions", ".action-btn", "a", "button"];
      for (let selector of excludedElements) {
        if (event.target.closest(selector)) {
          return; // 如果點擊的是按鈕、連結，就不觸發跳轉
        }
      }
      router.push(`/blog/find/${postid}`);
    };


    const route = useRoute();


    onMounted(async () => {
      await fetchProfile();
      await loadCounts();
      if (member.value.userid) {
        await fetchPosts('myPosts');
      }
    });

    return {
      member,
      posts,
      formatDate,
      setActiveTab,
      activeTab,
      followersCount,
      followingCount,
      postStore,
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
/* 轉發貼文的樣式 */
.repost-container {
  background-color: #f9f9f9;
  border-left: 4px solid #007bff;
  padding: 10px;
  margin-top: 10px;
  border-radius: 8px;
}

.repost-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.repost-author img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.repost-author-name {
  font-weight: 500;
  color: #007bff;
}

.repost-content {
  font-size: 0.9em;
  color: #555;
  line-height: 1.4;
  margin: 0;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}


.hidden-post {
  background-color: rgba(245, 245, 245, 0.3) !important;  /* ★★★★★ 修改：使用 RGBA 設定背景透明度 0.3 ★★★★★ */
  color: #888 !important;
  pointer-events: none; /* 禁止任何互動 */
  /* 移除了 opacity 屬性 */
  position: relative; /* 確保 .hidden-message 的絕對定位是相對於 .hidden-post */
}

/* ★★★★★【MODIFIED】：隱藏提示訊息設定，並新增 min-width 讓提示訊息更寬 ★★★★★ */
.hidden-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: rgba(179, 164, 164, 0.9);
  text-align: center;
  font-size: 14px;
  padding: 10px 20px;
  color: #ff0000;
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 1; /* 保持100%不透明 */
  min-width: 300px;  /* ★★★★★ 新增：設定最小寬度 300px ★★★★★ */
}

</style>