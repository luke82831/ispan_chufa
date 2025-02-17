<template>
  <div class="hero-text">
    <h1>出發！發現台灣之美，規劃每一刻的精彩！</h1>
    <p class="subtext">專屬你的台灣之旅，從規劃開始</p>
  </div>
  <div class="carousel">
    <div
      class="carousel-item"
      v-for="(post, index) in posts"
      :key="post.postid || index"
      :class="{ active: index === currentIndex }"
      @click="navigateToDetail(post.postid, $event)"
    >
      <img :src="getFirstImage(post.postContent)" class="carousel-image" />
      <div class="carousel-content">
        <h2>{{ post.postTitle }}</h2>
        <p>{{ getTextPreview(post.postContent,20) }}</p>
      </div>
    </div>

    <!-- 輪播控制按鈕 -->
    <div v-if="posts.length > 1" class="carousel-controls">
      <button @click="prevSlide">←</button>
      <button @click="nextSlide">→</button>
    </div>

    <div v-else>
      <p>沒有更多文章可顯示。</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { useRouter } from "vue-router";
import axiosapi from "@/plugins/axios"; // 根據你的專案路徑修改
// 接收 postIds 作為 prop
const props = defineProps({
  postIds: {
    type: Array,
    default: () => [],
  },
});

// 狀態管理
const posts = ref([]);
const currentIndex = ref(0);
let autoSlideInterval = null;

const postIds = ref([104,105,106,107,108,109,110,111,112,113,114,115]); // 在內部管理 postIds
const postIdsInput = ref(postIds.value.join(",")); // 輸入框預設值
const router = useRouter();

const getFirstImage = (content) => {
  const match = content.match(/<img[^>]+src="([^">]+)"/);
  return match ? match[1] : null;
};
const getTextPreview = (content, length) => {
  // 移除圖片和其他 HTML 標籤
  const textContent = content.replace(/<img[^>]*>/g, "").replace(/<[^>]+>/g, "");
  return textContent.slice(0, length) + (textContent.length > length ? "..." : "");
};

// 跳轉到文章詳情頁
const navigateToDetail = (postid, event) => {
  const excludedElements = [".post-actions", ".action-btn", "a", "button"];
  for (let selector of excludedElements) {
    if (event.target.closest(selector)) {
      return;
    }
  }
  router.push(`/blog/find/${postid}`);
};

// 逐一傳入 postid 並抓取資料
//const postIds = [20067, 20068, 20069,20070];
const fetchPosts = async () => {
  try {
    posts.value = []; // 清空 posts
    for (let postid of postIds.value) {
      const requestData = {
        postid: postid,
      };

      const response = await axiosapi.post("/api/posts/post", requestData, {
        headers: {
          "Content-Type": "application/json",
          "Cache-Control": "no-cache",
        },
      });
      console.log("回傳資料:", response.data); // 檢查 API 回傳的資料

      if (response.data.postdto && response.data.postdto.length > 0) {
        posts.value.push(response.data.postdto[0]);
      }
      console.log("posts 陣列:", posts.value); // 最終的 posts 陣列
    }
  } catch (error) {
    console.error("Fetch posts failed:", error);
  }
};

watch(
  () => props.postIds,
  () => {
    fetchPosts();
  },
  { immediate: true }
);

// 下一張
const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % posts.value.length;
};

// 上一張
const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + posts.value.length) % posts.value.length;
};

// 自動輪播
const startAutoSlide = () => {
  autoSlideInterval = setInterval(() => {
    nextSlide();
  }, 3000); // 每 3 秒切換一張
};

// 停止自動輪播
const stopAutoSlide = () => {
  clearInterval(autoSlideInterval);
};

// 當元件載入完成時，抓取資料並開始自動輪播
onMounted(() => {
  fetchPosts();
  startAutoSlide();
});

// 當元件解除安裝時，清除自動輪播
onBeforeUnmount(() => {
  stopAutoSlide();
});
</script>

<style scoped>
.hero-text {
  text-align: left; /* 使標語靠左 */
  margin-top: 20px;
  padding: 10px 20px;
}

h1 {
  font-size: 2rem;
  margin: 0;
  text-align: left; /* 確保主標題靠左 */
}

.subtext {
  font-size: 1.1rem;
  margin-top: 8px;
  opacity: 0.8;
  text-align: left; /* 確保副標題靠左 */
}

.carousel {
  position: relative;
  width: 100%;
  max-width: 1200px; /* 调整轮播容器的最大宽度 */
  margin: 0 auto;
  overflow: hidden;
}

.carousel-item {
  display: none;
  position: relative;
  width: 100%;
  transition: opacity 0.5s ease-in-out;
}

.carousel-item.active {
  display: block;
}

.carousel-image {
  width: 100%; /* 图片宽度占满容器 */
  height: 400px; /* 固定图片高度 */
  object-fit: cover; /* 确保图片覆盖整个区域，保持比例 */
  border-radius: 8px;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.2); /* 更透明的背景 */
  color: white;
  padding: 20px;
  border-radius: 0 0 8px 8px;
}

.carousel-content h2 {
  margin: 0;
  font-size: 24px;
}

.carousel-content p {
  margin: 10px 0 0;
  font-size: 16px;
}

.carousel-controls {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  transform: translateY(-50%);
  z-index: 10; /* 确保按钮在图片上方 */
}

.carousel-controls button {
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  padding: 12px; /* 调整按钮大小 */
  width: 40px; /* 固定按钮宽度 */
  height: 40px; /* 固定按钮高度 */
  border-radius: 50%; /* 圆形按钮 */
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.carousel-controls button:hover {
  background: rgba(0, 0, 0, 0.8);
}

.carousel-controls button:focus {
  outline: none;
}

.no-posts-message {
  text-align: center;
  font-size: 18px;
  color: #666;
  padding: 20px;
}
</style>
