    <template>
        <div v-if="profileLoaded" class="main-container">
        <div v-for="post in posts" :key="post.postid" class="post-item" :class="{'repost': post.postType === 'REPOST'}">
            <!-- 發文者資料、內容區塊保持不變 -->
                <!-- REPOST 版型處理 -->
                <div v-if="post.postType === 'REPOST'" class="repost-header">
                    <div class="interaction-info">
                    <img v-if="post.interactionprofilePicture" :src="post.interactionprofilePicture" alt="Interaction Profile Picture" class="profile-picture">
                    <p class="interaction-name">{{ post.interactionNickname }} ({{ post.interactionName }})轉發貼文</p>
                    </div>
                </div>


            <div v-if="post.authorName" class="author-info">
            <div class="author-header">
                <div class="profile-picture-container">
                <img v-if="post.authorprofilePicture" :src="post.authorprofilePicture" alt="Author's Profile Picture" class="profile-picture">
                <div v-else class="default-profile"></div>
                </div>
                <div class="author-name">
                <strong>{{ post.authorNickname }} ({{ post.authorName }})</strong>
                </div>
            </div>
            <h3>{{ post.postTitle }}</h3>
            </div>
    
            <p>{{ post.postContent }}</p>
            <a :href="post.postlink" target="_blank">閱讀更多</a>
            <p>發佈時間: {{ formatDate(post.postTime) }}</p>
            <p>互動時間: {{ formatDate(post.interactionTime) }}</p>
            <p>貼文類型: {{ post.postType }}</p>
    
            <!-- 轉發次數和點讚數放到右下角 -->
            <div class="post-stats">
            <p>轉發次數: {{ post.repost }}</p>
            <p>點讚數: {{ post.like }}</p>
            </div>
    
            <!-- 按鈕區域 -->
            <div class="post-actions">
            <button @click="likePost(post.postid)">點讚</button>
            <button @click="repostPost(post.postid)">轉發</button>
            <button @click="collectPost(post.postid)">收藏</button>
            </div>
        </div>
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
   // 取得會員貼文
    const fetchPosts = async () => {
        const userId = member.value.userid;
        if (!userId) {
            console.error('User ID not found');
            return;
        }
        try {
            const response = await axios.get(`http://localhost:8080/api/posts/blog/${userId}`);
            
            // 列印返回的資料，查看資料結構
            console.log('API 返回資料:', response.data);
            
            // 檢查是否有 posts 資料欄並且它是一個陣列
            if (Array.isArray(response.data) && response.data.length > 0) {
            posts.value = response.data;  // 假設返回的 posts 直接是陣列
            } else {
            // 如果沒有返回帖子資料，或者資料不符合預期
            console.error('返回的資料不符合預期:', response.data);
            Swal.fire('錯誤', '未能取得貼文資料', 'error');
            }
        } catch (error) {
            console.error('請求錯誤:', error);
            Swal.fire('錯誤', '無法取得貼文資料', 'error');
        }
                };
   // 在組件加載時獲取資料
    onMounted(async () => {
        await fetchProfile();  // 先獲取會員資料
        if (member.value.userid) {
          await fetchPosts(member.value.userid);  // 根據會員ID獲取貼文
        }
        await fetchPosts();
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

<style scoped>
/* 使body和html使用flex布局，並垂直水平居中 */

body, html {
  height: 100%;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Arial, sans-serif;
}

/* 主要容器居中並設定最大寬度 */
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;  /* 設定最大寬度，不會佔滿整個螢幕 */
  padding: 20px;
  margin: 0 auto;
}

/* 每篇貼文加邊框並確保內容居中 */
.post-item {
  background-color: #f9f9f9;
  padding: 20px;
  margin-bottom: 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 100%;
  box-sizing: border-box;  /* 包含 padding 和 border 在內 */
}



.post-item h3 {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.post-item p {
  font-size: 14px;
  color: #555;
}

.post-item a {
  color: #007bff;
  text-decoration: none;
}

.post-item a:hover {
  text-decoration: underline;
}

/* REPOST 樣式 */
.post-item.repost {
  border-left: 5px solid #007bff; /* 強調 REPOST 文章 */
  padding-left: 15px;
}

.repost-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.interaction-info {
  display: flex;
  align-items: center;
}

.interaction-name {
  font-size: 14px;
  color: #555;
  margin-left: 10px;
}

/* 圓形圖片框 */
.profile-picture-container {
  display: inline-block;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #ddd;
  background-color: #f0f0f0;
  margin-top: 10px;
}

/* 頭像圖片 */
.profile-picture {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 預設頭像樣式 */
.default-profile {
  width: 100%;
  height: 100%;
  background-color: #ddd;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  color: #fff;
  font-weight: bold;
}

/* 發文者資料 */
.author-info {
  display: flex;
  flex-direction: column;
}

.author-header {
  display: flex;
  align-items: center;
}

.author-name {
  margin-left: 10px;
  font-size: 16px;
}

.author-name strong {
  font-weight: bold;
}

.author-info h3 {
  font-size: 18px;
  margin-top: 5px;
  color: #333;
}

/* 按鈕區域 */
.post-actions {
  display: flex;
  gap: 12px;
  margin-top: 15px;
}

.post-actions button {
  padding: 8px 15px;
  border: none;
  background-color: #007bff;
  color: white;
  font-size: 14px;
  cursor: pointer;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.post-actions button:hover {
  background-color: #0056b3;
}

/* 頁面底部按鈕 */
.post-actions button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.post-item {
  background-color: #f9f9f9;
  padding: 20px;
  margin-bottom: 15px;
  border-radius: 8px;
  border: 1px solid #ddd;  /* 加上邊框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
/* 轉發和點讚數 */
.post-item {
  position: relative;  /* 讓內部元素可以絕對定位 */
}

.post-stats {
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-size: 12px;
  color: #888;  /* 灰色文字 */
}

.post-stats p {
  margin: 0;
  line-height: 1.2;
}


</style>
