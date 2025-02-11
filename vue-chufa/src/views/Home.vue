<template>
  <div class="main-container">
    <!-- 只顯示未被隱藏的貼文 -->
    <div
      v-for="post in visiblePosts"
      :key="post.postid"
      class="post-item"
      @click="navigateToDetail(post.postid, $event)"
    >
      <!-- REPOST 版型處理 -->
      <div v-if="post.repost" class="repost-header">
        <div class="interaction-info">
          <div class="repost-profile-container">
            <img
              v-if="post.member?.profilePicture"
              :src="'data:image/jpeg;base64,' + post.member.profilePicture"
              alt="Interaction Profile Picture"
              class="profile-picture small-profile"
            />
          </div>
          <p class="interaction-name">
            {{ post.member.nickname }} ({{ post.member.name }}) 轉發貼文
          </p>
        </div>
      </div>

      <div class="author-info" @click.stop>
        <div class="author-header">
          <div class="profile-picture-container">
            <router-link :to="`/blog/blogprofile/${post.member.userid}`">
              <img
                v-if="post.member.profilePicture"
                :src="'data:image/jpeg;base64,' + post.member.profilePicture"
                alt="Author's Profile Picture"
                class="profile-picture"
              />
              <div v-else class="default-profile"></div>
            </router-link>
          </div>
          <div class="author-name" @click.stop>
            <strong>
              {{ post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname }}
              ({{ post.repostDTO?.member?.name || post.member.name }})
            </strong>
          </div>
        </div>
        <h3>
          {{ post.repostDTO ? post.repostDTO.postTitle : post.postTitle || "無標題" }}
        </h3>
      </div>

      <p class="post-content">{{ post.postContent }}</p>
      <a v-if="post.postLink" :href="post.postLink" target="_blank" class="read-more">
        閱讀更多
      </a>
      <div class="post-meta">
        <p>
          發佈時間:
          {{ formatDate(post.repost ? post.repostDTO.postTime : post.postTime) }}
        </p>
        <p v-if="post.repostDTO">互動時間: {{ formatDate(post.postTime) }}</p>
        <p>貼文類型: {{ post.repost ? "REPOST" : "原創" }}</p>
      </div>

      <div class="post-stats">
        <p>轉發次數: {{ post.repostCount }}</p>
        <p>點讚數: {{ post.likeCount }}</p>
      </div>

      <!-- 按鈕區域 -->
      <div class="post-actions" @click.stop>
        <button @click.stop="likePost(post.postid)" class="action-btn like-btn">
          👍 點讚
        </button>
        <button @click.stop="repostPost(post.postid)" class="action-btn repost-btn">
          🔁 轉發
        </button>
        <button @click.stop="collectPost(post.postid)" class="action-btn collect-btn">
          ❤️ 收藏
        </button>
      </div>
    </div>
    <!-- 分頁控制 -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
      <span>第 {{ currentPage }} 頁</span>
      <button @click="nextPage">下一頁</button>
    </div>

    <!-- 發文按鈕 -->
    <div v-if="userStore.isLoggedIn">
      <RouterLink to="/blog/create" id="blogbutton">發文</RouterLink>
    </div>

    <!-- 開始規劃按鈕 -->
    <div v-if="userStore.isLoggedIn">
      <RouterLink to="/myitineraries" id="planningbutton">開始規劃</RouterLink>
    </div>
    <RouterView></RouterView>
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
import { usePostStore } from "@/stores/usePostStore";
import { useUserStore } from "@/stores/user";

export default {
  setup() {
    const router = useRouter();
    const profileLoaded = ref(false);
    const member = ref({});
    const posts = ref([]);
    const isAdmin = ref(false);
    const userId = ref(null);
    const currentPage = ref(1); // 當前頁數
    const postStore = usePostStore();

    // computed 屬性：只回傳未被隱藏的貼文
    const visiblePosts = computed(() => {
      return posts.value.filter((post) => !postStore.getHiddenReason(post.postid));
    });
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

    const formatDate = (date) => {
      if (!date) return "";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    const fetchProfile = async () => {
      try {
        const response = await axios.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (response.data.success) {
          member.value = response.data.user || {};
          isAdmin.value = response.data.user.role === "ADMIN";
          userId.value = member.value.userid;
        }
        profileLoaded.value = true;
      } catch (error) {
        console.error("Fetch profile failed:", error);
        Swal.fire("錯誤", "無法取得會員資料", "error");
      }
    };

    const fetchPosts = async () => {
      try {
        const response = await axios.post(
          "/api/posts/post",
          {
            sortByLikes: true,
            repost: true,
            page: currentPage.value, // 加入分頁參數
          },
          {
            headers: { "Content-Type": "application/json" },
          }
        );
        if (response.data.postdto && response.data.postdto.length > 0) {
          // 只取出原創貼文（排除 repost）
          posts.value = response.data.postdto.filter(
            (post) => !post.repost && post.repostDTO === null
          );
        } else {
          currentPage.value = Math.max(1, currentPage.value - 1);
          Swal.fire("已經到底啦!", "no post。", "info");
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", "無法取得貼文", "error");
      }
    };

    // 分頁控制
    const nextPage = () => {
      currentPage.value++;
      fetchPosts();
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
        fetchPosts();
      }
    };

    const repostPost = async (postid) => {
      try {
        const data = { postid, userid: member.value.userid };
        const response = await axios.post("/api/posts/repost/forward", data, {
          headers: { "Content-Type": "application/json" },
        });
        if (response.data.repost) {
          Swal.fire("成功", "轉發成功！", "success");
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "轉發失敗！", "error");
        }
      } catch (error) {
        console.error("轉發請求失敗:", error);
        Swal.fire("錯誤", "無法執行轉發操作", "error");
      }
    };

    const likePost = async (postid) => {
      try {
        const data = {
          postid,
          userid: member.value.userid,
          interactionType: "LIKE",
        };
        const response = await axios.post("/api/posts/insertinteraction", data, {
          headers: { "Content-Type": "application/json" },
        });
        if (response.data.success) {
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error");
      }
    };

    const collectPost = async (postid) => {
      try {
        const data = {
          postid,
          userid: member.value.userid,
          interactionType: "COLLECT",
        };
        const response = await axios.post("/api/posts/insertinteraction", data, {
          headers: { "Content-Type": "application/json" },
        });
        if (response.data.success) {
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error");
      }
    };

    onMounted(async () => {
      await fetchPosts();
      await fetchProfile();
    });

    return {
      profileLoaded,
      member,
      isAdmin,
      formatDate,
      likePost,
      collectPost,
      repostPost,
      posts,
      visiblePosts, // 將 visiblePosts 回傳給模板
      navigateToDetail,
      currentPage,
      nextPage,
      prevPage,
      userStore,
    };
  },
};
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 500px;
  padding: 20px;
  margin: 0 auto;
}

.post-item {
  background: linear-gradient(145deg, #ffffff, #f9f9f9);
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 100%;
  box-sizing: border-box;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.post-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.post-item h3 {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.post-content {
  font-size: 16px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
}

.read-more {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.read-more:hover {
  color: #0056b3;
}

.post-meta {
  font-size: 14px;
  color: #777;
  margin-bottom: 15px;
}

.post-stats {
  position: absolute;
  bottom: 15px;
  right: 15px;
  font-size: 12px;
  color: #888;
}

.post-stats p {
  margin: 0;
  line-height: 1.2;
}

.post-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.action-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.action-btn:hover {
  transform: scale(1.05);
}

.like-btn {
  background-color: #28a745;
  color: white;
}

.repost-btn {
  background-color: #17a2b8;
  color: white;
}

.collect-btn {
  background-color: #dc3545;
  color: white;
}

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

.profile-picture {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

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

.repost-profile-container {
  display: inline-block;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
}

.repost-profile-container img.small-profile {
  width: 30px;
  height: 30px;
  object-fit: cover;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}

.pagination button {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin: 0 8px;
  transition: background 0.3s;
}

.pagination button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background: #0056b3;
}

.pagination span {
  font-size: 1.1rem;
  color: #333;
}

/* 發文/規劃按鈕 */
#planningbutton {
  position: fixed;
  bottom: 50px;
  right: 50px;
  width: 100px;
  height: 100px;
  background-color: #84baf5;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-size: 35px;
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: normal;
  overflow-wrap: break-word;
  padding: 10px;
  transition: transform 0.2s, background-color 0.2s;
}

#planningbutton:hover {
  transform: scale(1.1);
  background-color: #5a95d5;
}

#blogbutton {
  position: fixed;
  bottom: 200px;
  right: 50px;
  width: 100px;
  height: 100px;
  background-color: #85a98f;
  color: #fff;
  border-radius: 50%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-size: 35px;
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  white-space: normal;
  overflow-wrap: break-word;
  padding: 10px;
  transition: transform 0.2s, background-color 0.2s;
}

#blogbutton:hover {
  transform: scale(1.1);
  background-color: #5a6c57;
}
</style>
