<template>
  <div class="navbar">
    <!-- Logo -->
    <RouterLink to="/" class="nav-link logo">Chufa首頁</RouterLink>

    <!-- 右側導航區域 -->
    <div class="nav-links">
      <!-- 登入後顯示大頭貼和下拉選單 -->
      <div v-if="userStore.isLoggedIn" class="member-section">
        <div class="avatar-container" @click="toggleDropdown">
          <img
            :src="userStore.member.profile_picture || '/path/to/default-avatar.png'"
            alt="會員大頭貼"
            class="avatar"
            @error="onAvatarError"
          />
          <span class="username">{{ userStore.member.name || '會員' }}</span>
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
            <button @click="logout" class="dropdown-item">
              <i class="fas fa-sign-out-alt"></i> 登出
            </button>
          </div>
        </transition>
      </div>

      <!-- 未登入時顯示登入/註冊 -->
      <div v-else>
        <RouterLink to="/secure/Login" class="nav-link">登入功能</RouterLink>
        <RouterLink to="/secure/Register" class="nav-link">註冊功能</RouterLink>
      </div>
    </div>
  </div>

  <!-- 開始規劃按鈕 -->
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
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user.js'; // 引入 Pinia Store

const userStore = useUserStore(); // 使用 Pinia 的狀態
const router = useRouter();
const isDropdownVisible = ref(false);
const isPlanningStarted = ref(false);
const route = useRoute();

const hidePlanningButton = () => {
  isPlanningStarted.value = true;
};

// 監聽路由變化，動態顯示規劃按鈕
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/') {
      isPlanningStarted.value = false;
    }
  }
);

// 下拉選單行為
const toggleDropdown = () => {
  isDropdownVisible.value = !isDropdownVisible.value;
};

// 登出行為
const logout = () => {
  localStorage.removeItem('token');
  userStore.logout(); // 清空 Pinia 狀態
  router.push('/secure/Login');
};

// 處理頭像加載錯誤
const onAvatarError = () => {
  userStore.member.profile_picture = '/path/to/default-avatar.png';
};

// 初始化會員資料
onMounted(() => {
  userStore.fetchProfile().catch(() => {
    router.push('/secure/Login');
  });
});
</script>



<style scoped>
/* 全局樣式 */
.navbar {
  background-color: #5a95d5;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  color: white;
  text-decoration: none;
  font-size: 24px;
  font-weight: bold;
}

.nav-links {
  display: flex;
  align-items: center;
}

.nav-link {
  margin-left: 20px;
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #ffd700;
}

/* 會員區域樣式 */
.member-section {
  position: relative;
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

/* 整體下拉選單容器樣式 */
.dropdown-menu {
  position: absolute;
  top: 60px;
  right: 0;
  background: linear-gradient(145deg, #ffffff, #f8f8f8);
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  padding: 15px;
  width: 220px;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

/* 個別選項樣式 */
.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
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

/* 左側圖標樣式 */
.dropdown-icon-left {
  font-size: 16px;
  color: #666;
  transition: color 0.3s;
}

.dropdown-item:hover .dropdown-icon-left {
  color: white;
}

/* 動畫效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>


