<template>
  <div class="navbar">
    <!-- Logo -->
    <RouterLink to="/" class="nav-link logo" @click="resetSearch">
      <img src="./assets/LOGO.png" alt="Chufa首頁" class="logo-img" />
    </RouterLink>

    <div class="search-bar" v-if="showSearchBar">
      <input
        v-model="searchTitle"
        type="text"
        placeholder="搜尋文章或用戶..."
        class="p-2 border rounded w-full"
      />
      <button @click="onSearch" class="p-2 bg-blue-500 text-white rounded">搜索</button>
    </div>

    <div class="nav-links">
      <!-- 只有管理員才顯示後台管理按鈕 -->
      <RouterLink
        v-if="userStore.isLoggedIn && userStore.member.role === 'ADMIN'"
        to="/admin"
        class="admin-button"
      >
        <i class="fas fa-cog"></i> 後台管理
      </RouterLink>

      <!-- 登入後顯示大頭貼和下拉選單 -->
      <div v-if="userStore.isLoggedIn" class="member-section">
        <div class="avatar-container" @click="toggleDropdown">
          <img
            :src="computedAvatar"
            alt="會員大頭貼"
            class="avatar"
            @error="onAvatarError"
          />
          <span class="username">{{ userStore.member.name || "會員" }}</span>
          <i
            class="fas fa-chevron-down dropdown-icon"
            :class="{ 'icon-rotate': isDropdownVisible }"
          ></i>
        </div>
        <transition name="fade">
          <div v-if="isDropdownVisible" class="dropdown-menu">
            <RouterLink to="/secure/Profile" class="dropdown-item">
              <i class="fas fa-user-circle"></i> 會員資料
            </RouterLink>
            <RouterLink to="/myitineraries" class="dropdown-item">
              <i class="fas fa-user-circle"></i> 我的行程
            </RouterLink>
            <RouterLink to="/blog/bloghome" class="dropdown-item">
              <i class="fas fa-user-circle"></i> 我的文章
            </RouterLink>
            <!-- 下拉選單中的登出按鈕 -->
            <button @click="logout" class="dropdown-item logout-item">
              <i class="fas fa-sign-out-alt"></i> 登出
            </button>
          </div>
        </transition>
      </div>

      <!-- 未登入時顯示登入/註冊 -->
      <div v-else>
        <RouterLink to="/secure/Login" class="nav-link">會員登入</RouterLink>
      </div>
    </div>
  </div>
  <RouterView></RouterView>
</template>

<script setup>
import { ref, computed, onMounted, watch, inject } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user.js";
import { useSearchStore } from "./stores/search";

const userStore = useUserStore();
const router = useRouter();
const searchStore = useSearchStore();
const isDropdownVisible = ref(false);
const defaultAvatar = "/images/avatar.jpg"; // 預設大頭貼

// 計算用戶的大頭貼來源
const computedAvatar = computed(() => {
  return userStore.member.profile_picture || userStore.member.avatar || defaultAvatar;
});

// 當圖片載入錯誤時，自動替換為預設大頭貼
const onAvatarError = (event) => {
  event.target.src = defaultAvatar;
};

// 使用 vue-router 監聽路由變化，控制 search bar 是否顯示
const route = useRoute();
const showSearchBar = ref(false);
watch(
  () => route.name,
  (newRoute) => {
    showSearchBar.value = newRoute === "Home" || newRoute === "SearchResults";
  },
  { immediate: true }
);

// 控制下拉選單開關
// 定義 isSearch

const toggleDropdown = () => {
  isDropdownVisible.value = !isDropdownVisible.value;
};

const searchTitle = ref("");

// 點擊首頁時重設搜尋狀態
const resetSearch = () => {
  //searchStore.resetSearch();
  searchTitle.value = "";
  searchStore.isSearch = false;
  searchStore.searchResults.value = [];
  // 使用 Vue Router 跳转到首页，并清空查询参数
  router.push({ path: "/", query: {} }); // 清空查询参数
  //window.location.reload();
};

const onSearch = () => {
  if (searchTitle.value.trim()) {
    searchStore.setSearchTitle(searchTitle.value);
    searchStore.isSearch = true; // 設定搜尋狀態
    router.push({ path: "/search-results", query: { title: searchTitle.value } });
  }
};

// 監聽路由變化
watch(
  () => router.path,
  (newPath) => {
    if (newPath === "/") {
      isPlanningStarted.value = false;
    }
  }
);

// 登出
const logout = () => {
  localStorage.removeItem("token");
  userStore.logout();
  isDropdownVisible.value = false;
  router.push("/secure/Login");
};

// 初始化會員資料
onMounted(() => {
  userStore.fetchProfile().catch(() => {
    router.push("/secure/Login");
  });
});
</script>

<style scoped>
/* Navbar 相關 */
.navbar {
  background-color: #2973b2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 20%;
}

.nav-links {
  display: flex;
  align-items: center;
}

.nav-link {
  margin-left: 20px;
  margin-right: 50px;
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
}

/* 會員區域 */
.member-section {
  display: flex;
  align-items: center;
  margin-right: 50px;
}

.avatar-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid white;
  object-fit: cover;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.avatar-container:hover .avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.username {
  font-size: 16px;
  color: white;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.dropdown-icon {
  color: white;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.avatar-container:hover .dropdown-icon {
  transform: rotate(180deg);
}

.icon-rotate {
  transform: rotate(180deg);
}

/* 下拉選單 */
.dropdown-menu {
  position: absolute;
  top: 80px;
  right: 0px;
  background: linear-gradient(145deg, #ffffff, #f8f8f8);
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  padding: 15px;
  width: 180px;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 12px 10px;
  font-size: 15px;
  font-weight: 600;
  color: #444;
  text-decoration: none;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background-color: #5a95d5;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.dropdown-icon-left {
  font-size: 16px;
  color: #666;
  transition: color 0.3s;
}

.dropdown-item:hover .dropdown-icon-left {
  color: white;
}

/* 登出按鈕美化 */
.dropdown-menu .logout-item {
  text-align: right;
  padding-right: 20px;
  font-weight: bold;
  color: #dc3545;
  background-color: transparent;
  border: none;
  width: 100%;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.dropdown-menu .logout-item:hover {
  background-color: #f8d7da;
  color: #c82333;
}

/* 其他動畫 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.admin-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #e74c3c;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: bold;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: background 0.3s ease, box-shadow 0.3s ease;
  text-decoration: none;
  margin-right: 80px;
}

.admin-button i {
  font-size: 18px;
}

.admin-button:hover {
  background-color: #c0392b;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}
.search-bar {
  display: flex;
  align-items: center;
  background-color: #f8f8f8;
  padding: 8px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  max-width: 300px;
  width: 100%;
}

.search-bar input {
  flex: 1;
  padding: 10px;
  border: none;
  outline: none;
  font-size: 16px;
  border-radius: 6px;
}

.search-bar button {
  background-color: #2973b2;
  color: white;
  border: none;
  padding: 10px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: background-color 0.3s ease, box-shadow 0.2s;
}

.search-bar button:hover {
  background-color: #48a6a7;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.nav-link.logo {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.logo-img {
  width: 300px; /* 調整 Logo 大小 */
  height: auto;
  margin-left: 20px;
  margin-right: -20px;
}
</style>
