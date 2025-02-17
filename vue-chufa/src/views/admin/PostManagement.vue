<template>
  <div class="post-management" @wheel.prevent>
    <div>
      <h1 class="page-title">文章管理</h1>
    </div>
    <div class="container">
      <div v-if="loading" class="loading-text">載入中...</div>
      <div v-else-if="!posts.length" class="no-data">沒有文章可顯示</div>
      <div v-else class="table-wrapper">
        <table class="post-table">
          <thead>
            <tr>
              <th class="preview-col">貼文預覽</th>
              <th class="title-col">貼文標題</th>
              <th class="content-col">貼文內容</th>
              <th class="status-col">貼文狀態</th>
              <th class="time-col">貼文時間</th>
              <th class="action-col">操作</th>
            </tr>
          </thead>
          <tbody>
            <!-- 如果該貼文已被隱藏，就加上 hidden-row 類別 -->
            <tr
              v-for="post in paginatedPosts"
              :key="post.postid"
              :class="[
                'hover-effect',
                postStore.getHiddenReason(post.postid) ? 'hidden-row' : '',
              ]"
            >
              <td>
                <!-- 如果貼文被隱藏，在上方顯示「已隱藏」標籤 -->
                <div v-if="postStore.getHiddenReason(post.postid)" class="hidden-label">
                  已隱藏
                </div>
                <div class="post-item-preview">
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
                  <div class="author-info">
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
                      <div class="author-name">
                        <strong>
                          {{
                            post.repostDTO
                              ? post.repostDTO.member.nickname
                              : post.member.nickname
                          }}
                          ({{ post.repostDTO?.member?.name || post.member.name }})
                        </strong>
                      </div>
                    </div>
                    <h3>
                      {{
                        post.repostDTO
                          ? post.repostDTO.postTitle
                          : post.postTitle || "無標題"
                      }}
                    </h3>
                  </div>
                  <!-- 修改：用 v-html 渲染轉換後的內容 -->
                  <div
                    class="post-content"
                    v-html="transformContent(post.postContent)"
                  ></div>
                </div>
              </td>
              <td>{{ post.postTitle }}</td>
              <!-- 修改：用 v-html 渲染轉換後的內容 -->
              <td>
                <div v-html="transformContent(post.postContent)"></div>
              </td>
              <td>{{ post.postStatus }}</td>
              <td>{{ formatDate(post.postTime) }}</td>
              <td>
                <div class="action-buttons">
                  <!-- 如果該貼文已隱藏，顯示取消隱藏的綠色按鈕；否則顯示隱藏按鈕 -->
                  <template v-if="postStore.getHiddenReason(post.postid)">
                    <button class="unhide-btn" @click="unhidePost(post.postid)">
                      取消隱藏
                    </button>
                  </template>
                  <template v-else>
                    <button class="hide-btn" @click="hidePost(post.postid)">隱藏</button>
                  </template>
                  <button class="delete-btn" @click="deletePost(post.postid)">
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁控制 -->
      <div class="pagination">
        <button :disabled="currentPage === 0" @click="currentPage--">上一頁</button>
        <span>第 {{ currentPage + 1 }} 頁 / 共 {{ totalPages }} 頁</span>
        <button :disabled="currentPage >= totalPages - 1" @click="currentPage++">
          下一頁
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { usePostStore } from "@/stores/usePostStore";

const posts = ref([]);
const loading = ref(true);
const currentPage = ref(0);
const pageSize = 3; // 設定每頁顯示 3 筆資料
const postStore = usePostStore();

const fetchPosts = async () => {
  loading.value = true;
  try {
    const response = await axios.post("/api/posts/post", {
      sortByLikes: true,
      repost: true,
      page: 1,
    });
    posts.value = response.data.postdto || [];
  } catch (error) {
    Swal.fire("錯誤", "無法取得文章資料", "error");
  } finally {
    loading.value = false;
  }
};

const paginatedPosts = computed(() => {
  // 計算分頁資料
  const start = currentPage.value * pageSize;
  return posts.value.slice(start, start + pageSize);
});

const totalPages = computed(() => Math.ceil(posts.value.length / pageSize)); // 計算總頁數

const formatDate = (date) => {
  if (!date) return "";
  const options = { year: "numeric", month: "2-digit", day: "2-digit" };
  return new Date(date).toLocaleDateString("zh-TW", options);
};

// 新增轉換函式：移除 <p> 標籤，並為 <img> 標籤加上大小限制
const transformContent = (content) => {
  if (!content) return "";
  // 移除 <p> 與 </p> 標籤
  let result = content.replace(/<\/?p>/g, "");
  // 針對 img 標籤，加入 inline style 限制大小為 100px
  result = result.replace(/<img([^>]*?)>/g, (match, p1) => {
    // 移除原有的 style 屬性後再加入新 style
    let cleaned = p1.replace(/\s*style="[^"]*"/, "");
    return `<img${cleaned} style="max-width:100px; max-height:100px;" />`;
  });
  return result;
};

const hidePost = async (postId) => {
  const { value: reason } = await Swal.fire({
    title: "選擇隱藏原因",
    input: "select",
    inputOptions: {
      不當言詞: "不當言詞",
      敏感內容: "敏感內容",
      隱私問題: "隱私問題",
      垃圾內容: "垃圾內容",
      /*========測試========*/
      // "骯髒邪惡又噁心的內容": "骯髒邪惡又噁心的內容",
      // "救命呀~~~救命呀~~~": "救命呀~~~救命呀~~~",
      // "管理員有點鬱悶": "管理員有點鬱悶",
      // "管理員想封鎖你hahaha笑你": "管理員想封鎖你hahaha笑你",
      // "你又騙我": "你又騙我",
      // "我不想看你的程式碼": "我不想看你的程式碼",
      /*========測試========*/
    },
    inputPlaceholder: "請選擇隱藏原因",
    showCancelButton: true,
    confirmButtonText: "確認隱藏",
    cancelButtonText: "取消",
  });

  if (reason) {
    Swal.fire("已隱藏該文章", `原因：${reason}`, "success");

    // 更新 Pinia 狀態
    postStore.setHiddenPost(postId, reason);

    // 更新本地文章狀態
    const post = posts.value.find((p) => p.postid === postId);
    if (post) {
      post.postStatus = "hidden";
    }
  }
};

const unhidePost = async (postId) => {
  const result = await Swal.fire({
    title: "取消隱藏確認",
    text: "請確認貼文內容已更改",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "確認",
    cancelButtonText: "取消",
  });
  if (result.isConfirmed) {
    // 移除 Pinia 隱藏狀態
    postStore.removeHiddenPost(postId);
    // 更新本地文章狀態（例如回復成 "active"）
    const post = posts.value.find((p) => p.postid === postId);
    if (post) {
      post.postStatus = "active";
    }
    Swal.fire("已取消隱藏", "該文章現已恢復", "success");
  }
};

const deletePost = async (postId) => {
  const result = await Swal.fire({
    title: "確定刪除嗎？",
    text: "刪除後無法復原！",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "刪除",
    cancelButtonText: "取消",
  });
  if (result.isConfirmed) {
    try {
      await axios.delete(`/api/posts/${postId}`);
      posts.value = posts.value.filter((p) => p.postid !== postId);
      Swal.fire("已刪除", "文章已成功刪除", "success");
    } catch (error) {
      Swal.fire("錯誤", "刪除文章失敗", "error");
    }
  }
};

onMounted(fetchPosts);
</script>

<style scoped>
/* 與 PlaceManagement 類似的版面設定 */
/* 外層容器 */
.post-management {
  width: 100%;
  max-width: 1500px;
  padding: 30px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2), 0 5px 15px rgba(0, 0, 0, 0.15);
  font-family: "Microsoft JhengHei", sans-serif;
}

.page-title {
  text-align: center;
  font-size: 28px;
  color: #343a40;
  border-bottom: 2px solid #ccc;
  margin-bottom: 0px;
}

/* 主要容器 */
.container {
  max-width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* 讓內容靠右 */
  padding: 20px;
  overflow-x: hidden;
  padding-top: 20px;
  width: 100%;
}

/* 表格外的滾動容器 */
.table-wrapper {
  width: 100%;
  max-width: 100vw;
  overflow-x: auto;
  display: block;
  margin-bottom: 20px;
  border-radius: 15px;
}

/* 表格本身 */
.post-table {
  width: 100%;
  min-width: 1200px;
  border-collapse: collapse;
  table-layout: fixed;
}

.post-table th,
.post-table td {
  border-top: 1px solid #ddd; /* 只保留橫向邊框 */
  border-bottom: 1px solid #ddd; /* 只保留橫向邊框 */
  padding: 8px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 去掉縱向邊框 */
.post-table th:first-child,
.post-table td:first-child {
  border-left: none;
}

.post-table th:last-child,
.post-table td:last-child {
  border-right: none;
}

.post-table th {
  background-color: #f4f4f4;
}

/* 無資料、載入中文字體 */
.loading-text,
.no-data {
  text-align: center;
  font-size: 18px;
}

/* 各欄位寬度調整 */
.preview-col {
  width: 400px;
}
.title-col {
  width: 150px;
}
.content-col {
  width: 200px;
}
.status-col {
  width: 70px;
}
.time-col {
  width: 100px;
}
.action-col {
  width: 150px;
  text-align: center;
}

.hover-effect {
  transition: background-color 0.3s ease-in-out;
}
.hover-effect:hover {
  background-color: #f0f8e6;
}

/* 操作按鈕 */
.action-buttons button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  min-width: 80px;
  text-align: center;
  font-size: 14px;
}

/* 確保按鈕留在操作欄 */
.action-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  white-space: nowrap;
}

.hide-btn {
  background-color: #007bff;
}
.delete-btn {
  position: relative !important; /* 確保它不會被移動到最右上角 */
  background-color: #dc3545;
  margin-top: -20px;
}

.hide-btn:hover {
  background-color: #0056b3;
}

/* 新增隱藏文章的反灰效果 */
.hidden-row {
  background-color: #f5f5f5 !important;
  color: #888;
}

/* 隱藏狀態的標籤 */
.hidden-label {
  background-color: rgba(0, 0, 0, 0.1);
  color: #333;
  font-weight: bold;
  padding: 3px 6px;
  border-radius: 4px;
  margin-bottom: 5px;
  display: inline-block;
}

/* 取消隱藏的按鈕樣式 */
.unhide-btn {
  background-color: #28a745;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  min-width: 80px;
  text-align: center;
  font-size: 14px;
  margin-top: 0px;
}

.unhide-btn:hover {
  background-color: #218838;
}

.delete-btn:hover {
  background-color: #c82333;
}

/* 分頁控制 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-top: 20px;
  gap: 10px;
  font-family: "Microsoft JhengHei", sans-serif;
  text-align: center;
}

.pagination button {
  padding: 8px 16px;
  border: 2px solid #ccc;
  border-radius: 50px;
  background: white;
  color: #333;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}
.pagination button:hover {
  border-color: #007bff;
  color: #007bff;
  transform: scale(1.1);
}
.pagination button:disabled {
  background: #f0f0f0;
  color: #999;
  border: 2px solid #e0e0e0;
  cursor: not-allowed;
  transform: none;
}
.pagination span {
  font-size: 14px;
  color: #555;
  font-weight: bold;
}

/* 貼文預覽區塊 */
.post-item-preview {
  text-align: left;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fafafa;
}

/* 作者大頭照 */
.profile-picture-container {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  display: inline-block;
  vertical-align: middle;
  margin-right: 8px;
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
}

/* 作者資訊標題 */
.author-info h3 {
  margin: 0;
  font-size: 16px;
}
</style>
