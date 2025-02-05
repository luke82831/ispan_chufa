<template>
  <div class="navbar">
    <RouterLink to="/" class="nav-link logo">Chufa首頁</RouterLink>
    <div class="search-bar">
    <input
      v-model="searchTitle"
      type="text"
      placeholder="搜尋文章..."
      class="p-2 border rounded w-full"
    />
    <button @click="navigateToSearch" class="p-2 bg-blue-500 text-white rounded">搜索</button>
  </div>
    <div class="nav-links">
      <RouterLink to="/secure/Login" class="nav-link">登入功能</RouterLink>
      <RouterLink to="/secure/Profile" class="nav-link">會員資料</RouterLink>
      <RouterLink to="/blog/followblog" class="nav-link">關注</RouterLink>
    </div>
  </div>
  <div>
    <RouterLink
      v-if="!isPlanningStarted"
      to="/createPlanning"
      id="planningbutton"
      @click="hidePlanningButton"
      >開始規劃</RouterLink
    >
  </div>
  <RouterView></RouterView>

</template>


<script setup>
import { RouterLink, RouterView,useRouter } from "vue-router";
import { ref, watch,onMounted } from "vue";
import { useRoute } from "vue-router";

import axios from 'axios';
import Swal from 'sweetalert2';

// 使用 ref 來管理按鈕顯示狀態
const isPlanningStarted = ref(false);
const route = useRoute();


// 點擊後隱藏「開始規劃」按鈕
const hidePlanningButton = () => {
  isPlanningStarted.value = true;
};




// 导航到搜索结果页面
const searchTitle = ref('');
const router = useRouter();

const navigateToSearch = () => {
  if (searchTitle.value.trim()) {
    router.push({ path: '/search-results', query: { title: searchTitle.value } });
  }
};

// 監聽路由變化，當路由變為首頁時，顯示按鈕
watch(
  () => route.path,
  (newPath) => {
    if (newPath === "/") {
      isPlanningStarted.value = false; // 返回首頁後顯示按鈕
    }
  }
);

//onMounted(fetchPosts);
</script>

<style>
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
  font-size: 35px; /* 根據需要調整字體大小 */
  font-weight: bold;
  text-decoration: none;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center; /* 使文字在圓形按鈕中垂直與水平居中 */
  white-space: normal; /* 允許文字換行 */
  overflow-wrap: break-word; /* 防止文字溢出圓形範圍 */
  padding: 10px; /* 內邊距，讓文字不會貼到圓形的邊緣 */
  transition: transform 0.2s, background-color 0.2s;
}

#planningbutton:hover {
  transform: scale(1.1);
  background-color: #5a95d5; /* 深藍色 */
}

/* 導覽列容器 */
.navbar {
  background-color: #5a95d5; /* 背景色 */
  padding: 10px 20px; /* 上下和左右的內邊距 */
  display: flex; /* 啟用 Flexbox */
  justify-content: space-between; /* 讓兩個區域分開，左側是 logo 右側是導航 */
  align-items: center; /* 垂直置中 */
  height: 60px; /* 設定導覽列高度 */
}

/* Logo（Chufa首頁） */
.logo {
  color: white;
  text-decoration: none;
  font-size: 24px;
  font-weight: bold;
}

/* 設定導航鏈接樣式 */
.nav-links {
  display: flex;
  align-items: center; /* 垂直置中 */
}

.nav-link {
  margin-left: 20px; /* 讓鏈接之間有間隔 */
  color: white; /* 文字顏色 */
  text-decoration: none; /* 去掉下劃線 */
  font-size: 16px; /* 文字大小 */
  transition: color 0.3s; /* 設置平滑過渡 */
}

/* 滑鼠懸停時的樣式 */
.nav-link:hover {
  color: #ffd700; /* 滑鼠懸停時改變文字顏色 */
}

/* search bar */
.search-bar {
  display: flex;
  gap: 10px;
}

.search-input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-button {
  padding: 0.5rem 1rem;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
