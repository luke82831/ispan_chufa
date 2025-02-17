<template>
  <div v-if="profileLoaded" class="main-container">
    <!-- //v-if="postStore.isLoggedIn"  -->
    <div
      v-for="post in posts"
      :key="post.postid"
      class="post-item"
      :class="{ repost: post.repost }"
    >
      <!-- 發文者資料、內容區塊保持不變 -->
      <!-- REPOST 版型處理 -->
      <div v-if="post.repost" class="repost-header">
        <div class="interaction-info">
          <div class="repost-profile-container">
            <!-- 顯示轉發者的頭像 (較小) -->
            <img
              v-if="post.repostDTO?.member?.profilePicture"
              :src="'data:image/jpeg;base64,' + post.member.profilePicture"
              alt="Interaction Profile Picture"
              class="profile-picture small-profile"
            />
          </div>
          <p class="interaction-name">
            {{ post.member.nickname }} ({{ post.member.name }})轉發貼文
          </p>
        </div>
      </div>

      <div v-if="post.member.name" class="author-info">
        <div class="author-header">
          <div class="profile-picture-container">
            <router-link :to="`/blog/blogprofile/${post.member.userid}`">
              <!-- 顯示發文者的頭像 -->
              <img
                v-if="post.repostDTO?.member?.profilePicture"
                :src="'data:image/jpeg;base64,' + post.repostDTO?.member.profilePicture"
                alt="Author's Profile Picture"
                class="profile-picture"
              />
              <div v-else class="default-profile"></div>
            </router-link>
          </div>
          <div class="author-name">
            <strong
              >{{
                post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname
              }}
              ({{ post.repostDTO?.member?.name || post.member.name }})</strong
            >
          </div>
        </div>
        <h3>
          {{ post.repostDTO ? post.repostDTO.postTitle : post.postTitle || "無標題" }}
        </h3>
      </div>

      <p>{{ post.postContent }}</p>
      <a v-if="post.postLink" :href="post.postLink" target="_blank">閱讀更多</a>
      <p>
        發佈時間: {{ formatDate(post.repost ? post.repostDTO.postTime : post.postTime) }}
      </p>
      <p v-if="post.repostDTO">互動時間: {{ formatDate(post.postTime) }}</p>
      <p>貼文類型: {{ post.repost ? "REPOST" : "原創" }}</p>

      <!-- 轉發次數和點讚數放到右下角 -->
      <div class="post-stats">
        <p>轉發次數: {{ post.repostCount }}</p>
        <p>點讚數: {{ post.likeCount }}</p>
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
import { ref, onMounted, computed } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
import axiosapi from "@/plugins/axios.js";

export default {
  setup() {
    const router = useRouter();
    const isEditing = ref(false);
    const profileLoaded = ref(false);
    const member = ref({}); // 用來儲存會員資料
    const posts = ref([]); // 用來儲存會員的貼文
    const isAdmin = ref(false);
    const userId = ref(null);
    //const postStore = usePostsStore();

    //const member = computed(() => postStore.member);

    const formatDate = (date) => {
      if (!date) return "";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    // 取得會員資料
    const fetchProfile = async () => {
      try {
        const response = await axiosapi.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (response.data.success) {
          console.log("haha" + member.value.username);
          member.value = response.data.user || {};
          isAdmin.value = response.data.user.role === "ADMIN";
          userId.value = member.value.userid;
        } else {
          Swal.fire("錯誤", response.data.message, "error");
        }
        profileLoaded.value = true;
      } catch (error) {
        console.error("Fetch profile failed:", error);
        Swal.fire("錯誤", "無法取得會員資料", "error");
      }
    };

    // 取得會員的貼文
    const fetchPosts = async () => {
      const userId = member.value.userid;
      if (!userId) {
        console.error("User ID not found");
        return;
      }
      try {
        const response = await axiosapi.post(
          "/api/posts/post",
          {
            sortByLikes: true,
          },
          {
            headers: {
              "Content-Type": "application/json", // 設置請求體類型爲 JSON
            },
          }
        );

        if (response.data.postdto && response.data.postdto.length > 0) {
          posts.value = response.data.postdto; // 儲存帖子數據
        } else {
          Swal.fire("沒有貼文", "沒有關注。", "info");
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", "無法取得貼文", "error");
      }
    };

    // 轉發方法
    const repostPost = async (postid) => {
      try {
        // 構建要發送的 JSON 資料
        const data = {
          postid: postid,
          userid: member.value.userid,
        };

        // 發送 POST 請求到 API
        const response = await axiosapi.post("/api/posts/repost/forward", data, {
          headers: {
            "Content-Type": "application/json",
          },
        });

        // 檢查 API 回應
        if (response.data.repost) {
          // 如果成功，可以做一些 UI 更新或提示用戶
          console.log("轉發成功");
          Swal.fire("成功", "轉發成功！", "success"); // 顯示成功提示
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "轉發失敗！", "error"); // 顯示錯誤提示
        }
      } catch (error) {
        console.error("轉發請求失敗:", error);
        Swal.fire("錯誤", "無法執行轉發操作", "error"); // 顯示錯誤提示
      }
    };

    //點讚
    const likePost = async (postid) => {
      try {
        // 構建要發送的 JSON 資料
        const data = {
          postid: postid,
          userid: member.value.userid,
          interactionType: "LIKE",
        };

        // 發送 POST 請求到 API
        const response = await axiosapi.post("/api/posts/insertinteraction", data, {
          headers: {
            "Content-Type": "application/json",
          },
        });

        // 檢查 API 回應
        if (response.data.success) {
          // 如果成功，可以做一些 UI 更新或提示用戶
          console.log("點讚成功");
          await fetchPosts();
          //Swal.fire('成功', '點讚成功！', 'success');  // 顯示成功提示
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error"); // 顯示錯誤提示
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error"); // 顯示錯誤提示
      }
    };

    // 在元件載入時取得資料
    onMounted(async () => {
      await fetchProfile(); // 先取得會員資料

      //postStore.fetchProfile();
      await fetchPosts(); // 根據會員ID取得貼文
    });

    return {
      isEditing,
      //isLoggedIn,
      member,
      isAdmin,
      formatDate,
      likePost,
      repostPost,
      posts,
      profileLoaded,
      //posts: postStore.posts,
      //postStore,
    };
  },
};
</script>

<style scoped>
/* 使body和html使用flex布局，並垂直水平居中 */

body,
html {
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
  max-width: 800px; /* 設定最大寬度，不會佔滿整個螢幕 */
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
  box-sizing: border-box; /* 包含 padding 和 border 在內 */
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
  position: relative;
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
  border: 1px solid #ddd; /* 加上邊框 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
/* 轉發和點讚數 */
.post-item {
  position: relative; /* 讓內部元素可以絕對定位 */
}

.post-stats {
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-size: 12px;
  color: #888; /* 灰色文字 */
}

.post-stats p {
  margin: 0;
  line-height: 1.2;
}

.small-profile {
  width: 10px; /* 可根據需要調整大小 */
  height: 10px;
  border-radius: 50%;
}
.repost-profile-container {
  display: inline-block;
  width: 30px; /* 小頭像容器的寬度 */
  height: 30px; /* 小頭像容器的高度 */
  border-radius: 50%;
  overflow: hidden;
}
.repost-profile-container img.small-profile {
  width: 30px;
  height: 30px;
  object-fit: cover;
}
</style>
