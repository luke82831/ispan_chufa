<template>
  <div class="admin-layout">
    <!-- 側邊選單 (未來擴展 Sidebar.vue) -->
    <aside class="sidebar">
      <nav>
        <router-link to="/admin/members">會員管理</router-link>
        <router-link to="/admin/places">地點管理</router-link>
        <router-link to="/admin/post">文章管理</router-link>
        <router-link to="/admin/calendar">日期管理</router-link>
      </nav>
    </aside>

    <!-- 主要內容區域 -->
    <div class="main-content">
      <!-- 頂部使用者資訊 (未來擴展 Navbar.vue) -->
      <div class="user-info">
        <span>管理者：</span>
        <span class="text">{{ member?.username || "訪客" }}</span>
      </div>

      <!-- 路由對應的內容 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const member = computed(() => userStore.member);
</script>

<style scoped>
/* 整個版面容器，使用 flex 並以 min-height 保證至少滿版 */
.admin-layout {
  display: flex;
  min-height: 100vh; /* 確保容器至少和瀏覽器視窗一樣高 */
}

/* === 側邊選單樣式 === */
.sidebar {
  width: 220px;
  background: #2973b2; /* 主要藍色 */
  color: white;
  padding: 20px;
  min-height: 100vh; /* 利用作法1：側邊選單至少延展至 100vh */
}

.sidebar h2 {
  font-size: 20px;
  margin-bottom: 15px;
}

.sidebar nav {
  display: flex;
  flex-direction: column;
}

.sidebar a {
  color: white;
  text-decoration: none;
  padding: 12px 15px;
  display: block;
  border-radius: 8px; /* 圓角設計 */
  transition: background 0.3s, transform 0.2s;
}

.sidebar a:hover {
  background: #48a6a7; /* 深藍色 */
  transform: translateX(4px); /* 滑鼠懸停時輕微位移 */
}

/* === 頂部使用者資訊樣式 === */
.user-info {
  background: #f8f8f8;
  padding: 10px 20px;
  border-bottom: 1px solid #ccc;
  font-size: 16px;
}

/* === 管理者資訊區塊 === */
.user-info {
  background: linear-gradient(90deg, #ffffff, #ddecf1);
  color: #160f0f;
  padding: 5px 10px;
  font-size: 16px;
  font-weight: bold;
  border-radius: 8px;
  display: flex; /* 讓內容能夠自然排列 */
  align-items: center;
  max-width: 200px; /* 限制整個區塊的寬度 */
  text-align: center;
  margin-bottom: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-info .text {
  display: inline-block; /* 確保能夠受 max-width 限制 */
  max-width: 120px; /* 限制文字部分的最大寬度 */
  white-space: nowrap; /* 不允許換行 */
  overflow: hidden; /* 隱藏超出部分 */
  text-overflow: ellipsis; /* 省略號顯示超出部分 */
}

/* === 主要內容區域 === */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 確保內容不會額外滾動 */
}
</style>
