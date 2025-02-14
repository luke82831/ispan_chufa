<template>
  <!-- 貼文網格布局 -->
  <div class="posts-grid">
    <div class="post-card" @click="navigateToDetail(post, $event)">
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

            <div v-else>
              <img
                :src="defaultProfilePic"
                alt="Default Profile Picture"
                class="profile-picture"
              />
            </div>
          </div>
          <p class="interaction-name">
            {{ post.member.nickname ? post.member.nickname : post.member.name }} 轉發貼文
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
              <div v-else>
                <img
                  :src="defaultProfilePicture"
                  alt="Default Profile Picture"
                  class="profile-picture"
                />
              </div>
            </router-link>
          </div>
          <div class="author-name">
            <strong
              v-if="
                post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname
              "
            >
              {{ post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname }}
            </strong>
            <strong v-else>
              {{ post.repostDTO?.member?.name || post.member.name }}
            </strong>
            <p class="post-time">{{ formatDate(post.postTime) }}</p>
          </div>
        </div>
        <!-- <div class="post-meta">
              <p>
                發佈時間:
                {{ formatDate(post.repost ? post.repostDTO.postTime : post.postTime) }}
              </p>
          </div> -->
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
            getFirstImage(post.repostDTO ? post.repostDTO.postContent : post.postContent)
          "
          class="post-image"
        />
      </div>
      <div v-else class="post-image-container">
        <img :src="defaultpicture" class="post-image" />
      </div>
      <!-- 
        移除圖片後的內容
        <div v-html="getContentWithoutImages(post.postContent)" class="post-content"></div> -->
      <div class="post-content-preview">
        <p>
          {{ post.repostDTO ? post.repostDTO.postTitle : post.postTitle || "無標題" }}
        </p>
        {{
          getTextPreview(
            post.repostDTO ? post.repostDTO.postContent : post.postContent || "無標題",
            30
          )
        }}
      </div>

      <!-- 互動按鈕 -->
      <div class="post-actions" @click.stop>
        <button
          @click.stop="likePost(post.postid)"
          class="action-btn like-btn"
          :class="{ active: post.likedByCurrentUser }"
        >
          <span class="heart-icon"></span>
          {{ post.likeCount }}
        </button>
        <button @click.stop="repostPost(post.postid)" class="action-btn repost-btn">
          🔁 {{ post.repostCount }}
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
</template>

<script setup>
import { defineProps, defineEmits, ref } from "vue";
import Swal from "sweetalert2";
import axiosapi from "@/plugins/axios";
import { useRouter } from "vue-router";
import defaultProfilePicture from "@/assets/empty.png";
import { useUserStore } from "@/stores/user.js";
import defaultpic from "@/assets/default.jpg";
const posts = ref([]);
const router = useRouter();
const defaultProfilePic = ref(defaultProfilePicture);
const userStore = useUserStore();
const defaultpicture = ref(defaultpic);

// 接收從父組件傳入的 `post` 資料和 `member`
const props = defineProps({
  post: Object,
  member: Object,
  formatDate: Function,
});
// props.fetchPosts();

const getFirstImage = (content) => {
  const match = content.match(/<img[^>]+src="([^">]+)"/);
  return match ? match[1] : null;
};

const getTextPreview = (content, length) => {
  // 移除圖片和其他 HTML 標籤
  const textContent = content.replace(/<img[^>]*>/g, "").replace(/<[^>]+>/g, "");
  return textContent.slice(0, length) + (textContent.length > length ? "..." : "");
};
// 定義事件發射器
const emit = defineEmits(["update-posts"]);

// const navigateToDetail = (postid, event) => {
//       const excludedElements = [".post-actions", ".action-btn", "a", "button"];
//       for (let selector of excludedElements) {
//         if (event.target.closest(selector)) {
//           return; // 如果點擊的是按鈕、連結，就不觸發跳轉
//         }
//       }
//       router.push(`/blog/find/${postid}`);
//     };

const navigateToDetail = (post, event) => {
  const excludedElements = [".post-actions", ".action-btn", "a", "button"];
  for (let selector of excludedElements) {
    if (event.target.closest(selector)) {
      return; // 如果點擊的是按鈕、連結，就不觸發跳轉
    }
  }
  // 如果是轉發的貼文，跳轉到原貼文的詳細頁
  if (post.repost && post.repostDTO) {
    router.push(`/blog/find/${post.repostDTO.postid}`);
  } else {
    // 否則跳轉到當前貼文的詳細頁
    router.push(`/blog/find/${post.postid}`);
  }
};

// 轉發貼文
const repostPost = async (postid) => {
  try {
    const data = {
      postid: postid,
      userid: userStore.member.userid,
    };

    const response = await axiosapi.post("/api/posts/repost/forward", data, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.data.repost) {
      Swal.fire("成功", "轉發成功！", "success");
      emit("update-posts"); // 通知父組件更新貼文
    } else {
      Swal.fire("錯誤", "轉發失敗！", "error");
    }
  } catch (error) {
    console.error("轉發請求失敗:", error);
    Swal.fire("錯誤", "請先登入", "error");
  }
};

// 點讚貼文
const likePost = async (postid) => {
  try {
    const data = {
      postid: postid,
      userid: userStore.member.userid,
      interactionType: "LIKE",
    };

    const response = await axiosapi.post("/api/posts/insertinteraction", data, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.data.success) {
      props.post.likedByCurrentUser = !props.post.likedByCurrentUser;
      props.post.likeCount = props.post.likedByCurrentUser
        ? props.post.likeCount + 1
        : props.post.likeCount - 1;
    } else {
      Swal.fire("錯誤", "點讚操作失敗！", "error");
    }
  } catch (error) {
    console.error("點讚請求失敗:", error);
    Swal.fire("錯誤", "請先登入", "error");
  }
};

// 收藏貼文
const collectPost = async (postid) => {
  try {
    const data = {
      postid: postid,
      userid: userStore.member.userid,
      interactionType: "COLLECT",
    };

    const response = await axiosapi.post("/api/posts/insertinteraction", data, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.data.success) {
      props.post.collectByCurrentUser = !props.post.collectByCurrentUser;
    } else {
      Swal.fire("錯誤", "收藏操作失敗！", "error");
    }
  } catch (error) {
    console.error("收藏請求失敗:", error);
    Swal.fire("錯誤", "請先登入", "error");
  }
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
  margin-bottom: 0px;
  overflow-x: auto;
  padding-bottom: 0px;
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

/* 帖子网格布局 */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

/* 帖子卡片样式 */
.post-card {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

/* 转发布局样式 */
.repost-header {
  padding: 10px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.interaction-info {
  display: flex;
  align-items: center;
}

.repost-profile-container {
  margin-right: 10px;
}

.profile-picture.small-profile {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
}

.interaction-name {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 作者信息样式 */
.author-info {
  padding: 15px;
}

.author-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.profile-picture-container {
  margin-right: 10px;
}

.profile-picture {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 16px;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin: 5px 0;
  text-align: left;
}

h3 {
  font-size: 18px;
  color: #333;
  margin: 10px 0;
}

/* 帖子图片样式 */
.post-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 帖子内容预览样式 */
.post-content-preview {
  font-size: 14px;
  color: #666;
  padding: 0 12px 12px;
  margin: 0;
  line-height: 1.5;
  text-align: left;
}

/* 互动按钮容器样式 */
.post-actions {
  display: flex;
  justify-content: space-around;
  padding: 10px;
  border-top: 1px solid #eee;
}

/* 没有文章时的提示样式 */
.posts-grid + div {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #666;
}

.default-profile {
  width: 100%;
  height: 100%;
  background-color: #ccc;
  border-radius: 50%;
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
