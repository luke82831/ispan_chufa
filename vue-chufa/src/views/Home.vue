<template>
  <div class="main-container">
    <!-- 標籤切換 -->
    <div>
      <div class="tabs-container">
        <button
          class="tab"
          :class="{ active: selectedPlace === null }"
          @click="switchPlace(null)"
        >
          首頁
        </button>
        <button
          class="tab"
          :class="{ active: selectedPlace === 'follow' }"
          @click="switchPlace('follow')"
        >
          關注
        </button>
        <button
          v-for="place in places"
          :key="place.id"
          class="tab"
          :class="{ active: selectedPlace === place.name }"
          @click="switchPlace(place.name)"
        >
          {{ place.name }}
        </button>

        <div class="sort-select-container">
          <select
            id="sortSelect"
            v-model="sortBy"
            @change="fetchPosts"
            class="border p-2 rounded"
          >
            <option value="likes">熱度排序</option>
            <option value="time">時間排序</option>
          </select>
        </div>
      </div>
    </div>
    <!-- 貼文網格布局 -->
    <div class="posts-grid">
      <div
        v-for="post in posts"
        :key="post.postid"
        class="post-card"
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

        <!-- 作者信息 -->
        <div class="author-info">
          <div class="author-header">
            <div class="profile-picture-container">
              <router-link :to="`/blog/blogprofile/${post.member.userid}`" @click.stop>
                <img
                  v-if="
                    post.repostDTO
                      ? post.repostDTO.member?.profilePicture
                      : post.member?.profilePicture
                  "
                  :src="
                    'data:image/jpeg;base64,' +
                    (post.repostDTO?.member?.profilePicture ?? post.member.profilePicture)
                  "
                  alt="Author's Profile Picture"
                  class="profile-picture"
                />
                <div v-else class="default-profile"></div>
              </router-link>
            </div>
            <div class="author-name">
              <strong>
                {{
                  post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname
                }}
                ({{ post.repostDTO?.member?.name || post.member.name }})
              </strong>
            </div>
          </div>
          <h3>
            {{ post.repostDTO ? post.repostDTO.postTitle : post.postTitle || "無標題" }}
          </h3>
        </div>

        <!-- 顯示第一張圖片 -->
        <div
          v-if="
            getFirstImage(post.repostDTO ? post.repostDTO.postContent : post.postContent)
          "
          class="post-image-container"
        >
          <img
            :src="
              getFirstImage(
                post.repostDTO ? post.repostDTO.postContent : post.postContent
              )
            "
            class="post-image"
          />
        </div>
        <!-- 
        移除圖片後的內容
        <div v-html="getContentWithoutImages(post.postContent)" class="post-content"></div> -->
        <p class="post-content-preview">
          {{
            getTextPreview(
              post.repostDTO ? post.repostDTO.postContent : post.postContent || "無標題",
              30
            )
          }}
        </p>

        <!-- 貼文元信息 -->
        <div class="post-meta">
          <p>
            發佈時間:
            {{ formatDate(post.repost ? post.repostDTO.postTime : post.postTime) }}
          </p>
          <p v-if="post.repostDTO">互動時間: {{ formatDate(post.postTime) }}</p>
          <p>貼文類型: {{ post.repost ? "REPOST" : "原創" }}</p>
        </div>

        <!-- 貼文統計 -->
        <div class="post-stats">
          <p>轉發次數: {{ post.repostCount }}</p>
          <p>點讚數: {{ post.likeCount }}</p>
        </div>

        <!-- 互動按鈕 -->
        <div class="post-actions" @click.stop>
          <button
            @click.stop="likePost(post.postid)"
            class="action-btn like-btn"
            :class="{ active: post.likedByCurrentUser }"
          >
            <span class="heart-icon"></span>
            {{ post.likedByCurrentUser ? "已點讚" : "點讚" }}
          </button>
          <button @click.stop="repostPost(post.postid)" class="action-btn repost-btn">
            🔁 轉發
          </button>
          <button
            @click.stop="collectPost(post.postid)"
            class="action-btn collect-btn"
            :class="{ active: post.collectByCurrentUser }"
          >
            {{ post.collectByCurrentUser ? "已收藏" : "收藏" }}
          </button>
        </div>
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
import { ref, onMounted, watch, inject, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user.js";
import Swal from "sweetalert2";
import { useRoute } from "vue-router";
import { useSearchStore } from "@/stores/search.js";
import axiosapi from "@/plugins/axios.js";
import { usePostStore } from "@/stores/usePostStore";

export default {
  setup() {
    const router = useRouter();
    const profileLoaded = ref(false);
    const member = ref({});
    const posts = ref([]);
    const users = ref([]);
    const isAdmin = ref(false);
    const userId = ref(null);
    const currentPage = ref(1); // 當前頁數
    const noPosts = ref(false);
    const sortBy = ref("likes"); // 排序狀態
    const searchQuery = ref("");
    const isSearch = ref(false);
    const searchStore = useSearchStore();
    const selectedPlace = ref(null);

    //place
    //const selectedPlace = ref(null);
    const places = ref([
      { id: 1, name: "Los Angeles" },
      { id: 2, name: "New York" },
      { id: 3, name: "Chicago" },
    ]);

    watch(sortBy, () => {
      fetchPosts(); // 每次排序方式改變時重新抓取資料
    });

    const getFirstImage = (content) => {
      const match = content.match(/<img[^>]+src="([^">]+)"/);
      return match ? match[1] : null;
    };

    const getTextPreview = (content, length) => {
      // 移除圖片和其他 HTML 標籤
      const textContent = content.replace(/<img[^>]*>/g, "").replace(/<[^>]+>/g, "");
      return textContent.slice(0, length) + (textContent.length > length ? "..." : "");
    };
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
        const response = await axiosapi.get("/ajax/secure/profile", {
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

    const fetchPosts = async (query = "") => {
      try {
        const requestData = {
          page: currentPage.value,
          size: 5,
          checklike: member.value.userid,
          repost: true,
        };

        // 動態設定排序條件
        requestData[sortBy.value === "likes" ? "sortByLikes" : "sortByTime"] = true;

        if (query) {
          requestData.postTitle = query; // 加入搜尋條件
          isSearch.value = true;
        }
        if (selectedPlace.value === "follow") {
          //requestData.repost=true;
          requestData.followerId = member.value.userid;
        } else if (selectedPlace.value !== null || selectedPlace !== "users") {
          // 只有選擇地點時才加入 place
          requestData.place = selectedPlace.value;
        }

        const response = await axiosapi.post("/api/posts/post", requestData, {
          headers: {
            "Content-Type": "application/json",
            "Cache-Control": "no-cache",
          },
        });
        if (response.data.postdto && response.data.postdto.length > 0) {
          posts.value = response.data.postdto;
          // .filter(
          //   (post) => !post.repost && post.repostDTO === null
          // );
          noPosts.value = false;
        } else {
          //posts.value = [];
          currentPage.value = Math.max(1, currentPage.value - 1); // 返回有效的上一頁
          Swal.fire("已經到底啦!", "no post。", "info");
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", "無法取得貼文", "error");
      }
    };

    //tab
    const switchPlace = (placeName) => {
      if (placeName === "follow") {
        selectedPlace.value = "follow";
      } else {
        selectedPlace.value = placeName;
      }
      currentPage.value = 1;
      // const url = new URL(window.location.href);
      // url.search = ''; // 清空查詢參數
      // window.history.replaceState(null, '', url);

      searchStore.resetSearch(); // 清空搜索
      fetchPosts();
    };

    //分頁
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
        const data = {
          postid: postid,
          userid: member.value.userid,
        };

        const response = await axiosapi.post("/api/posts/repost", data, {
          headers: {
            "Content-Type": "application/json",
          },
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

    // 判断当前用户是否已经点赞
    const likePost = async (postid) => {
      try {
        // 查找当前操作的帖子
        const postToUpdate = posts.value.find((post) => post.postid === postid);
        const data = {
          postid: postid,
          userid: member.value.userid,
          interactionType: "LIKE", // 如果点赞则是 LIKE，取消点赞则是 DISLIKE
        };

        const response = await axiosapi.post("/api/posts/insertinteraction", data, {
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (response.data.success) {
          // 更新本地状态：更新点赞状态和点赞数量
          // 更新本地状态：根据操作更新点赞状态和点赞数量
          const updatedPosts = posts.value.map((post) => {
            if (post.postid === postid) {
              return {
                ...post,
                likedByCurrentUser: !post.likedByCurrentUser, // 反转点赞状态
                likeCount: post.likedByCurrentUser
                  ? post.likeCount - 1
                  : post.likeCount + 1, // 根据点赞状态增加或减少点赞数
              };
            }
            return post;
          });

          // 更新本地 posts 状态
          posts.value = updatedPosts;
        } else {
          Swal.fire("錯誤", "點讚操作失敗！", "error");
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

        const response = await axiosapi.post("/api/posts/insertinteraction", data, {
          headers: {
            "Content-Type": "application/json",
          },
        });
        if (response.data.success) {
          const updatedPosts = posts.value.map((post) => {
            if (post.postid === postid) {
              return {
                ...post,
                collectByCurrentUser: !post.collectByCurrentUser, // 反转点赞状态
              };
            }
            return post;
          });

          // 更新本地 posts 状态
          posts.value = updatedPosts;
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error");
      }
    };

    const setSort = (type) => {
      if (sortBy.value !== type) {
        sortBy.value = type;
        fetchPosts();
      }
    };
    const route = useRoute();
    const resetSearch = () => {
      searchStore.resetSearch(); // 调用 Pinia store 中的 resetSearch 方法
    };

    watch(
      () => route.query.title,
      (newQuery) => {
        if (newQuery) {
          fetchPosts(newQuery); // 如果有搜尋條件就請求搜尋
        } else {
          fetchPosts(); // 沒有搜尋條件則請求普通的 fetchPosts
        }
      },
      { immediate: true }
    );

    //watch
    // 監聽 sortBy 的變化，當選擇變更時請求 fetchPost

    onMounted(async () => {
      selectedPlace.value = null;
      //selectedPlace.value = places.value[0].id;
      await fetchPosts();
      await fetchProfile();
      const query = route.query.title || ""; // 如果 query.title 為 undefined，則使用空字串
      fetchPosts(query); // 根據查詢條件抓取貼文
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
      places,
      switchPlace,
      selectedPlace,
      getFirstImage,
      //getContentWithoutImages,
      getTextPreview,
      userStore,
      setSort,
      sortBy,
      searchQuery,
      isSearch,
      searchStore,
      users,
    };
  },
};
</script>
<style scoped>
.main-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.tabs-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.tab {
  padding: 10px 20px;
  border: none;
  background-color: #f0f0f0;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background-color 0.3s, color 0.3s;
  white-space: nowrap;
}

.tab:hover {
  background-color: #e0e0e0;
}

.tab.active {
  background-color: #005ab5;
  color: white;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.post-card {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  background-color: white;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
}

.post-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.post-image-container:hover .post-image {
  transform: scale(1.05);
}

.post-content-preview {
  padding: 16px;
  font-size: 14px;
  color: #555;
  line-height: 1.5;
}

.post-content h3 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.read-more {
  display: inline-block;
  margin: 10px 0;
  color: #ff4757;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.read-more:hover {
  color: #ff6b81;
}

.post-meta,
.post-stats {
  padding: 0 16px 10px;
  font-size: 12px;
  color: #888;
}

.post-meta p,
.post-stats p {
  margin: 5px 0;
}

.post-actions {
  display: flex;
  justify-content: space-around;
  padding: 10px;
  border-top: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.action-btn {
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: color 0.3s;
}

.action-btn:hover {
  color: #ff4757;
}

.action-btn.active {
  color: #ff4757;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 10px;
}

.pagination button {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background-color: #f0f0f0;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background-color 0.3s, color 0.3s;
}

.pagination button:hover {
  background-color: #ff4757;
  color: white;
}

.pagination button:disabled {
  background-color: #ccc;
  color: #666;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #333;
}

/* 作者信息樣式 */
.author-info {
  padding: 16px;
}

.author-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-picture-container {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.profile-picture {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-profile {
  width: 100%;
  height: 100%;
  background-color: #ccc;
  border-radius: 50%;
}

.author-name {
  font-size: 14px;
  color: #333;
}

/* REPOST 樣式 */
.repost-header {
  padding: 10px 16px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #e0e0e0;
}

.interaction-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.repost-profile-container {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  overflow: hidden;
}

.small-profile {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.interaction-name {
  font-size: 12px;
  color: #666;
  margin: 0;
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

.sort-select-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-grow: 1;
}
select {
  padding: 8px;
  margin: 5px;
  cursor: pointer;
  border-radius: 4px;
}

/* 基本的愛心按鈕樣式 */
.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: transform 0.2s, color 0.2s;
}

.like-btn.active {
  color: red; /* 爱心变为红色 */
}

.like-btn.active::before {
  content: "❤️"; /* 实心爱心 */
}

.like-btn:not(.active)::before {
  content: "🖤"; /* 空心爱心 */
}

/* 点击时的动画效果 */
.like-btn:active {
  transform: scale(1.2);
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: transform 0.2s, color 0.2s;
}

.collect-btn.active {
  color: #ffcc00; /* 书签变为黄色 */
}

.collect-btn.active::before {
  content: "⭐"; /* 实心书签 */
}

.collect-btn:not(.active)::before {
  content: "⭐"; /* 空心书签 */
}

/* 点击时的动画效果 */
.collect-btn:active {
  transform: scale(1.2);
}

@keyframes fillBookmark {
  0% {
    background: linear-gradient(90deg, #62605a 0%, #bab7a9 0%);
    -webkit-background-clip: text;
    color: transparent;
  }
  100% {
    background: linear-gradient(90deg, #ffcc00 100%, #ffcc00 100%);
    -webkit-background-clip: text;
    color: transparent;
  }
}

.collect-btn.active {
  animation: fillBookmark 0.5s ease-out forwards;
}
</style>
