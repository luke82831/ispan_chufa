<template>
  <div class="admin-layout">
    <!-- 側邊選單 (未來擴展 Sidebar.vue) -->
    <aside class="sidebar">
      <nav>
        <router-link to="/admin/members">會員管理</router-link>
        <router-link to="/admin/places">地點管理</router-link>
        <router-link to="/admin/post">文章管理</router-link>
      </nav>
    </aside>

    <!-- 主要內容區域 -->
    <div class="main-content">
      <!-- 頂部導航 (未來擴展 Navbar.vue) -->
      <div class="user-info">
        <span>管理者：{{ member?.username || "訪客" }}</span>
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
.admin-layout {
  display: flex;
  height: calc(100vh - 80px);
  overflow: hidden; /* 防止溢出 */
  background: #e0ecff; /* 淡藍背景，讓視覺更柔和 */
}

/* === 側邊選單樣式 === */
.sidebar {
  width: 220px;
  background: #5a95d5; /* 主要藍色 */
  color: white;
  padding: 20px;
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
  background: #2563eb; /* 深藍色 */
  transform: translateX(4px); /* 滑鼠懸停時輕微位移 */
}

/* === 頂部導航欄樣式 === */
.navbar {
  background: #3b82f6; /* 與側邊欄相同 */
  color: white;
  padding: 15px 20px;
  text-align: right;
  font-size: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar span {
  font-weight: bold;
}

/* === 主要內容區域 === */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 確保內容不會額外滾動 */
}

.content {
  flex: 1;
  padding: 20px;
  background: white; /* 主要內容區域保持白色 */
  border-radius: 8px;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}
</style>
