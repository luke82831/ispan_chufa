<template>
  <div class="profile-page">
    <div v-if="member" class="profile-container">
      <div class="profile-header">
        <img
          :src="
            member.profilePicture
              ? 'data:image/jpeg;base64,' + member.profilePicture
              : defaultProfilePicture
          "
          alt="Profile Picture"
          v-if="member.profilePicture || defaultProfilePicture"
          class="profile-picture"
        />
        <div class="info">
          <h2 class="section-title">
            {{ member.nickname ? member.nickname : member.name }}
          </h2>
          <p><strong></strong>{{ member.bio }}</p>
          <p><strong>Email:</strong> {{ member.email }}</p>
          <p><strong>生日:</strong> {{ formatDate(member.birth) }}</p>
          <div class="follow-button-container">
            <button
              :class="['follow-button', { active: member.isFollowing }]"
              @click.stop="toggleFollow(member)"
            >
              {{ member.isFollowing ? "已關注" : "關注" }}
            </button>
          </div>
          <div class="follow-section">
            <router-link :to="`/blog/followlist/${member.userid}`" class="follow-link">
              <p class="follow-count">
                <strong>關注:</strong> {{ followersCount }} <strong>粉絲:</strong>
                {{ followingCount }}
              </p>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <div class="tabs-container">
      <button
        :class="{ active: activeTab === 'myPosts' }"
        @click="setActiveTab('myPosts')"
      >
        他的貼文
      </button>
      <button
        :class="{ active: activeTab === 'likedPosts' }"
        @click="setActiveTab('likedPosts')"
      >
        點讚貼文
      </button>
      <button
        :class="{ active: activeTab === 'sharedPosts' }"
        @click="setActiveTab('sharedPosts')"
      >
        轉發貼文
      </button>
      <button
        :class="{ active: activeTab === 'savedPosts' }"
        @click="setActiveTab('savedPosts')"
      >
        收藏貼文
      </button>
    </div>

    <div class="posts-grid" v-if="posts.length > 0">
      <PostCard
        v-for="post in posts"
        :key="post.postid"
        :post="post"
        :member="member"
        :formatDate="formatDate"
        @update-posts="fetchPosts"
      />
    </div>

    <p v-else>沒有貼文。</p>

    <div v-else>
      <p>載入中...</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user.js";
import PostCard from "@/components/Postcard.vue";
import axiosapi from "@/plugins/axios.js";
import defaultProfilePic from "@/assets/empty.png";

export default {
  props: ["bloghomeid"],
  components: {
    PostCard, // 註冊 PostCard 元件
  },
  setup(props) {
    const router = useRouter();
    const member = ref({});
    const posts = ref([]);
    const isAdmin = ref(false);
    const profileLoaded = ref(false);
    const bloghomeid = props.bloghomeid;
    const defaultProfilePicture = ref(defaultProfilePic);
    //const isFollowing=ref(false);

    const followersCount = ref(0); // 使用 ref 來定義自適應資料
    const followingCount = ref(0);
    const userStore = useUserStore();
    const userId = computed(() => userStore.member.userid);
    const activeTab = ref("myPosts");

    // 用來從後端取得關注者和被關注者的人數
    const fetchCount = async (type) => {
      const url =
        type === "followers"
          ? `/follow/followercount/${bloghomeid}`
          : `/follow/followingcount/${bloghomeid}`;

      try {
        const response = await axiosapi.get(url);
        // 假設返回的資料是一個物件，包含 `count` 資料欄
        return response.data; // 如果沒有資料則返回0
      } catch (error) {
        console.error(`Error fetching ${type}:`, error);
        return 0;
      }
    };

    const fetchProfiles = async () => {
      try {
        const response = await axiosapi.get(`/api/posts/members/${bloghomeid}`);

        if (response.data) {
          member.value = response.data; // 存储用户资料
          // 获取关注状态
          const memberData = response.data;
          member.value = memberData;
          await checkFollowingStatus(memberData);
          console.log("Member data:", member.value);
        } else {
          Swal.fire("沒有用戶資料", "無法獲取該用戶的資料", "info");
        }
      } catch (error) {
        console.error("Fetch user profile failed:", error);
        Swal.fire("錯誤", "無法獲取用戶資料", "error");
      }
    };

    // 检查当前用户是否已关注该用户
    const checkFollowingStatus = async (member) => {
      if (!member || !member.userid) {
        console.error("Member object or userid is missing:", member);
        return;
      }

      try {
        // 获取该用户是否被当前用户关注的状态
        const response = await axiosapi.post("/follow/isFollowing", {
          followedid: userStore.member.userid,
          followerid: member.userid, // 使用传递过来的普通对象
        });
        // 更新用户的关注状态
        member.isFollowing = response.data;
        console.log(bloghomeid + " " + member.isFollowing);
      } catch (error) {
        console.error("Error checking following status:", error);
      }
    };

    // 切换关注状态
    const toggleFollow = async (member) => {
      try {
        const action = member.isFollowing ? "unfollow" : "follow";
        const response = await axiosapi.post("/follow/verb", {
          followerid: userStore.member.userid,
          followedid: member.userid,
          action,
        });

        if (response.data.success) {
          // 切换关注状态
          member.isFollowing = !member.isFollowing;
          console.log(
            `User ${member.userid} is now ${
              member.isFollowing ? "following" : "not following"
            }`
          );
        } else {
          console.error("Failed to toggle follow status:", response.data.message);
        }
      } catch (error) {
        console.error("Failed to toggle follow status:", error);
        Swal.fire("錯誤", "無法更新關注狀態", "error");
      }
    };

    // 載入關注人數和被關注人數
    const loadCounts = async () => {
      followersCount.value = await fetchCount("followers");
      followingCount.value = await fetchCount("followed");
      console.log(followersCount.value);
      console.log(followingCount.value);
    };
    console.log(followersCount.value);
    console.log(followingCount.value);

    const formatDate = (date) => {
      if (!date) return "";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    const fetchPosts = async (filterType) => {
      try {
        const payload = { checklike: member.value.userid };
        if (filterType === "likedPosts") {
          payload.likedBy = member.value.userid;
          payload.repost = true;
        }
        if (filterType === "savedPosts") {
          payload.collectBy = member.value.userid;
          payload.repost = true;
        }
        if (filterType === "myPosts") {
          payload.repost = false; // 不要 repost 的貼文\
          payload.userid = member.value.userid;
        } else if (filterType === "sharedPosts") {
          payload.repost = true; // 只要 repost 的貼文
          payload.userid = member.value.userid;
        }
        //userid: member.value.userid
        const response = await axiosapi.post("/api/posts/post", payload);
        let postData = response.data.postdto || [];

        // 過濾條件
        if (filterType === "myPosts") {
          posts.value = postData.filter(
            (post) => !post.repost && post.repostDTO === null
          );
        } else if (filterType === "sharedPosts") {
          posts.value = postData.filter((post) => post.repost || post.repostDTO !== null);
        } else {
          posts.value = postData;
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", `無法取得${filterType}的貼文`, "error");
      }
    };

    const setActiveTab = async (tab) => {
      activeTab.value = tab;
      await fetchPosts(tab);
    };

    onMounted(async () => {
      await fetchProfiles();
      if (bloghomeid) {
        await fetchPosts();
      }
      await loadCounts();
    });

    return {
      member,
      posts,
      isAdmin,
      profileLoaded,
      formatDate,
      bloghomeid,
      followersCount,
      followingCount,
      toggleFollow,
      setActiveTab,
      activeTab,
      defaultProfilePicture,
      fetchPosts,
    };
  },
};
</script>

<style scoped>
/* 基本頁面布局 */
.profile-page {
  font-family: "Arial", sans-serif;
  background-color: rgba(74, 149, 207, 0.001);
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 會員資料區域 */
.profile-container {
  background-color: rgba(11, 144, 238, 0.2);
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}
.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
/* 標題 */
.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}
.profile-info {
  flex: 1;
}

.nickname {
  font-size: 24px;
  margin: 0;
}

.email,
.birthday,
.bio {
  margin: 0px 0;
  font-size: 14px;
  color: #666;
}

.follow-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.follow-count {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.follow-button.active {
  background-color: #ccc;
}

.content-section {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.post-image {
  width: 100%;
  height: auto;
  display: block;
}

.post-caption {
  padding: 10px;
  font-size: 14px;
  color: #333;
  margin: 0;
}

/* 會員圖片 */
.profile-picture {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #ddd;
  margin-right: 20px;
}

/* 會員資訊區域 */
.info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

/* 單行資訊樣式 */
.info p {
  font-size: 16px;
  color: #333;
  margin: 10px 0;
}

/* 強調的文字 */
.info strong {
  color: #007bff; /* Instagram 風格的藍色 */
}

/* 去除連結的下劃線 */
.follow-link {
  text-decoration: none;
  color: inherit; /* 保持文字顏色繼承父元素的顏色 */
  transition: transform 0.3s ease, margin 0.3s ease; /* 添加過渡效果 */
}

/* 讓關注和粉絲顯示在同一行 */
.follow-link p {
  display: inline;
  margin: 0;
  padding: 0;
}

/* 強調文字顏色 */
.follow-link p strong {
  color: #007bff; /* 可以根據需要調整顏色 */
}

/* 懸停時放大並靠近 */
.follow-link:hover {
  transform: scale(1.1); /* 放大 1.1 倍 */
  margin-left: 5px; /* 讓元素稍微向右移動，達到“靠近”的效果 */
}
/* 關注按鈕 */

.follow-button-container {
  position: absolute; /* 絕對定位 */
  top: 200px; /* 距離頂部 20px */
  right: 600px; /* 距離右側 20px */
}

.follow-button {
  padding: 12px 30px;
  border: none;
  border-radius: 40px;
  background-color: #0d5ad7;
  color: white;
  cursor: pointer;
  font-size: 16px;
}

.follow-button.active {
  background-color: #ccc;
}

.follow-button:hover {
  background-color: #2980b9;
}

.follow-section {
  margin-bottom: 20px;
}

.follow-count {
  margin: 0;
  font-size: 14px;
  color: #666;
}
/* 分頁按鈕 */
.tabs-container {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  border-bottom: 2px solid #ddd;
  padding-bottom: 10px;
}

.tabs-container button {
  font-size: 16px;
  padding: 10px 20px;
  background-color: transparent;
  border: none;
  color: #333;
  cursor: pointer;
  transition: color 0.3s ease, border-bottom 0.3s ease;
}

.tabs-container button:hover {
  color: #007bff;
}

.tabs-container button.active {
  color: #007bff;
  border-bottom: 2px solid #007bff;
}

/* 貼文區域 */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

/* 沒有貼文的提示文字 */
p {
  font-size: 16px;
  color: #777;
  text-align: center;
  margin-top: 20px;
}

/* 貼文卡片 (PostCard) */
.post-card {
  background-color: #fff;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.post-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
