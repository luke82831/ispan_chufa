<template>
  <p>{{ member.name }}</p>
  <div class="followlist-page">
    <h1 class="page-title">用戶 ID: {{ followid }}</h1>

    <!-- 分頁按鈕 -->
  
    <div class="tabs">
      <button
        :class="['tab-button', { active: activeTab === 'followed' }]"
        @click="switchTab('followed')"
      >
        關注列表
      </button>
      <button
        :class="['tab-button', { active: activeTab === 'follower' }]"
        @click="switchTab('follower')"
      >
        粉絲列表
      </button>
    </div>

    <!-- 加載中提示 -->
    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
      <p>加載中，請稍候...</p>
    </div>

    <!-- 錯誤提示 -->
    <div v-if="isError" class="error">
      <p>資料加載失敗，請稍後重試。</p>
      <button @click="fetchData" class="retry-button">重試</button>
    </div>

    <!-- 關注者/粉絲列表 -->
    <div v-if="!isLoading && !isError" class="followers-section">
      <div v-if="listData.length === 0" class="empty-state">
        <p v-if="activeTab === 'followed'">目前沒有任何關注者。</p>
        <p v-else>目前沒有任何粉絲。</p>
      </div>
      <ul v-else class="followers-list">
        <li v-for="item in listData" :key="item.userid" class="follower-item">
          <div class="profile-picture">
            <img
              :src="item.profilePicture ? 'data:image/jpeg;base64,' + item.profilePicture :  defaultProfilePicture"
              alt="profile picture"
              class="profile-image"
            />
          </div>
          <div class="follower-info">
            <h3 class="follower-nickname">{{ item.nickname }}</h3>
            <p class="follower-id">ID: {{ item.userid }}</p>
            <p class="follower-name">Name: {{ item.name }}</p>
          </div><p>{{ item.isFollowing }}</p>
          <!-- 關注按鈕 -->
          <button
            :class="['follow-button', { active: item.isFollowing }]"
            @click="toggleFollow(item)"
          >
            {{ item.isFollowing ? '已關注' : '未關注' }}
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
import { ref, onMounted, watch } from 'vue';
import axiosapi from '@/plugins/axios';
import defaultProfilePic from '@/assets/empty.png';
import Swal from 'sweetalert2';


export default {
  props: {
    followid: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const listData = ref([]); // 儲存當前列表數據（關注者或粉絲）
    const isLoading = ref(false); // 是否正在加載
    const isError = ref(false); // 是否出現錯誤
    const activeTab = ref('followed'); // 當前選中的分頁
    const defaultProfilePicture = ref(defaultProfilePic); // 默認頭像路徑
    const userId = ref(null);
    const member = ref({});
    const profileLoaded = ref(false);



    const fetchProfile = async () => {
      try {
        const response = await axiosapi.get('/ajax/secure/profile', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        if (response.data.success) {
          member.value = response.data.user || {};
        } else {
          //Swal.fire('錯誤', response.data.message, 'error');
        }
      } catch (error) {
        console.error('Fetch profile failed:', error);
        //Swal.fire('錯誤', '無法獲取會員資料', 'error');
      }
    };

    // 根據當前選中的分頁獲取數據
    const fetchData = async () => {
      try {
        isLoading.value = true;
        isError.value = false;

        const endpoint =
          activeTab.value === 'followed'
            ? `/follow/followedList/${props.followid}`
            : `/follow/followerList/${props.followid}`;

        const response = await axiosapi.get(endpoint);

        if (response.data && Array.isArray(response.data)) {
          listData.value = response.data;
        } else {
          listData.value = [];
        }
      } catch (error) {
        console.error('Fetch data failed:', error);
        isError.value = true;
      } finally {
        isLoading.value = false;
      }
    };

  
    // 切換分頁
    const switchTab =  async(tab) => {
      activeTab.value = tab;
      await fetchData();
      await fetchDataWithStatus();
    };


    // 查詢是否關注
    const checkFollowingStatus = async (item) => {
      try {
        const response = await axiosapi.post('/follow/isFollowing', {
          followedid: item.userid,
          followerid: member.value.userid,
        });
        item.isFollowing = response.data;
        console.log(item.userid +" " +item.isFollowing);
      } catch (error) {
        console.error('Error checking following status:', error);
      }
    };

    // 切換關注狀態
    const toggleFollow = async (item) => {
    try {
      const action = item.isFollowing ? 'unfollow' : 'follow';
      const response = await axiosapi.post('/follow/verb', {
        followerid: member.value.userid,
        followedid: item.userid,
        action,
      });

      if (response.data.success) {
        // 切換狀態
        item.isFollowing = !item.isFollowing;
        console.log(`User ${item.userid} is now ${item.isFollowing ? 'following' : 'not following'}`);
        await checkFollowingStatus(item);
        await fetchData(); // 重新加載列表 
        
      } else {
        console.error('Failed to toggle follow status:', response.data.message);
      }
    } catch (error) {
      console.error('Failed to toggle follow status:', error);
      Swal.fire('錯誤', '無法更新關注狀態', 'error');
    }
  };

    // 初始化檢查列表中的每個用戶是否關注
    const fetchDataWithStatus = async () => {
    await fetchData(); // 先抓取列表資料
    await checkFollowingStatus();
    const checkPromises = listData.value.map(item => checkFollowingStatus(item)); // 并行检查每个用户的关注状态
    await Promise.all(checkPromises); // 等待所有的检查完成
    };

    

    // 組件加載時自動執行
    onMounted(async () => {
      await fetchProfile(); // 等待會員資料加載完成
      await fetchData();
      await fetchDataWithStatus(); // 確保 profile 已加載
    });
    // 監聽 followid 的變化
    watch(
      () => props.followid,
      () => {
        fetchData();
      }
    );

    return {
      listData,
      isLoading,
      isError,
      activeTab,
      defaultProfilePicture,
      fetchData,
      switchTab,
      profileLoaded,
      member,
      toggleFollow,
    };
  },
};
</script>
<style scoped>
/* 頁面基本設置 */
.followlist-page {
    font-family: 'Arial', sans-serif;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
}

/* 標題樣式 */
.page-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}

/* 分頁按鈕 */
.tabs {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
}

.tab-button {
    font-size: 16px;
    padding: 12px 20px;
    background-color: transparent;
    border: none;
    color: #333;
    cursor: pointer;
    transition: color 0.3s ease, border-bottom 0.3s ease;
}

.tab-button:hover {
    color: #007bff;
}

.tab-button.active {
    color: #007bff;
    border-bottom: 2px solid #007bff;
}

/* 加載中提示 */
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 50px;
}

.spinner {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #007bff;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 2s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.loading p {
    margin-top: 10px;
    font-size: 16px;
    color: #555;
}

/* 錯誤提示 */
.error {
    text-align: center;
    margin-top: 50px;
    font-size: 16px;
    color: #e74c3c;
}

.retry-button {
    padding: 10px 20px;
    background-color: #e74c3c;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 14px;
    margin-top: 10px;
}

.retry-button:hover {
    background-color: #c0392b;
}

/* 關注者列表 */
.followers-section {
    margin-top: 30px;
}

/* 空狀態提示 */
.empty-state {
    text-align: center;
    font-size: 16px;
    color: #777;
}

/* 關注者列表樣式 */
.followers-list {
    list-style-type: none;
    padding: 0;
    margin: 0;
}

.follower-item {
    display: flex;
    align-items: center;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    padding: 15px;
    margin-bottom: 20px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.follower-item:hover {
    transform: scale(1.02);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 會員頭像 */
.profile-picture {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 15px;
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

/* 關注者信息 */
.follower-info {
    flex-grow: 1;
}

.follower-nickname {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
}

.follower-id, .follower-name {
    font-size: 14px;
    color: #555;
}

/* 關注按鈕 */
.follow-button {
    padding: 8px 16px;
    background-color: #007bff;
    color: #fff;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.3s ease;
}

.follow-button:hover {
    background-color: #0056b3;
}

.follow-button.active {
    background-color: #28a745;
}

.follow-button.active:hover {
    background-color: #218838;
}


/* 其他樣式保持不變 */
</style>