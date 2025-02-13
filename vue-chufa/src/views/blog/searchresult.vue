<template>
        <div class="main-container">
        <!-- 標籤切換 -->
    <div v-if="searchStore.isSearch">
        
        <div class="sort-select-container">
            <div class="tabs-container">
            <button class="tab" :class="{ active: selectedTab === 'posts' }" @click="switchTab('posts')">文章</button>
            <button class="tab" :class="{ active: selectedTab === 'users' }" @click="switchTab('users')">使用者</button>
            </div>
            <select  v-if="selectedTab === 'posts'" id="sortSelect" v-model="sortBy" @change="fetchPosts" class="border p-2 rounded">\
            
            <option value="likes">熱度排序</option>
            <option value="time">時間排序</option>
           
            </select>
        </div>
    </div>

    <div v-if="selectedTab === 'users'" class="users-list">
    <div v-for="user in users" :key="user.userid" class="user-item" @click="navigateToMember(user.userid, $event)">
        <!-- 用戶頭像 -->
        <div class="profile-picture-container">
            <img
                v-if="user.profilePicture"
                :src="'data:image/jpeg;base64,' + user.profilePicture"
                alt="Author's Profile Picture"
                class="profile-picture"
            />
            <img v-else :src="defaultProfilePic" alt="Default Profile Picture" class="profile-picture" />
        </div>

        <!-- 用戶信息 -->
        <div class="user-info">
            <h3 class="username">{{ user.name }}</h3>
            <h3 class="nickname">{{ user.nickname }}</h3>
        </div>

        <!-- 關注按鈕 -->
        <button
            :class="['follower-button', { active: user.isFollowing }]"
            @click.stop="toggleFollow(user)"
        >
            {{ user.isFollowing ? '已關注' : '未關注' }}
        </button>
    </div>
</div>

        <!-- 貼文網格布局 -->
        <div v-else class="posts-grid" v-if="posts.length > 0">
            <PostCard
                    v-for="post in posts"
                    :key="post.postid"
                    :post="post"
                    :member="member"
                    :formatDate="formatDate"
                    @update-posts="fetchPosts"
                    />
        </div>
    
        <!-- 分頁控制 -->
        <div class="pagination">
            <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
            <span>第 {{ currentPage }} 頁</span>
            <button @click="nextPage">下一頁</button>
        </div>
    
        <!-- 發文按鈕 -->
        <div v-if="userStore.isLoggedIn">
            <RouterLink to="/blog/create" id="blogbutton">發文</RouterLink>
        </div>
    
        <!-- 開始規劃按鈕 -->
        <div v-if="userStore.isLoggedIn">
            <RouterLink to="/myitineraries" id="planningbutton">開始規劃</RouterLink>
        </div>
        <RouterView></RouterView>
        </div>
    </template>
    <script>
    import { ref, onMounted,watch,inject,computed} from "vue";
    import { useRouter } from "vue-router";
    import { useUserStore } from "@/stores/user.js";
    import axios from "@/plugins/axios.js";
    import Swal from "sweetalert2";
    import { useRoute } from 'vue-router';
    import { useSearchStore } from '@/stores/search.js';
    import axiosapi from "@/plugins/axios.js";
    import PostCard from "@/components/Postcard.vue";
    import  defaultProfilePicture from '@/assets/empty.png'
    export default {
    components: {
    PostCard // 註冊 PostCard 元件
    },
        setup() {
        const router = useRouter();
        const profileLoaded = ref(false);
        const member = ref({});
        const posts = ref([]);
        const users = ref([]);
        const isAdmin = ref(false);
        const userId = ref(null);
        const currentPage = ref(1); // 當前頁數
        const noPosts = ref(false);
        const sortBy = ref("likes"); // 排序狀態
        const searchQuery = ref('');
        const isSearch = ref(false);
        const searchStore = useSearchStore();
        const selectedTab = ref("posts"); // 當前選擇的 Tab
        const listData = ref([]);
        const defaultProfilePic=ref(defaultProfilePicture)
        
        const selectedPlace = ref(null); 
        
        // 搜尋用戶
        const fetchUsers = async (query = "") => {
            try {
            const response = await axiosapi.post("/api/posts/searchByName", {
                username: query, 
            });
            users.value = response.data; // 將結果儲存到 store
            await Promise.all(users.value.map(user => checkFollowingStatus(user)));
            console.log(users.userid);
            } catch (error) {
            console.error("搜尋失敗:", error);
            }
        };
    
    const checkFollowingStatus = async (user) => {
        try {
            // 获取该用户是否被当前用户关注的状态
            const response = await axiosapi.post('/follow/isFollowing', {
                followedid: user.userid,
                followerid: member.value.userid,
            });
            // 更新用户的关注状态
            user.isFollowing = response.data; 
            console.log(user.userid + " " + user.isFollowing);
        } catch (error) {
            console.error('Error checking following status:', error);
        }
    };
 

// 切换关注状态
const toggleFollow = async (user) => {
    try {
        const action = user.isFollowing ? 'unfollow' : 'follow';
        const response = await axiosapi.post('/follow/verb', {
            followerid: member.value.userid,
            followedid: user.userid,
            action,
        });

        if (response.data.success) {
            // 切换关注状态
            user.isFollowing = !user.isFollowing;
            console.log(`User ${user.userid} is now ${user.isFollowing ? 'following' : 'not following'}`);
        } else {
            console.error('Failed to toggle follow status:', response.data.message);
        }
    } catch (error) {
        console.error('Failed to toggle follow status:', error);
        Swal.fire('錯誤', '無法更新關注狀態', 'error');
    }
};
    
        watch(sortBy, () => {
            fetchPosts();  // 每次排序方式改變時重新抓取資料
        });
    
        const getFirstImage = (content) => {
            const match = content.match(/<img[^>]+src="([^">]+)"/);
            return match ? match[1] : null;
        };
    
        // const getContentWithoutImages = (content) => {
        //   return content.replace(/<img[^>]*>/g, "");
        // };
    
        const getTextPreview = (content, length) => {
            // 移除圖片和其他 HTML 標籤
            const textContent = content.replace(/<img[^>]*>/g, "").replace(/<[^>]+>/g, "");
            return textContent.slice(0, length) + (textContent.length > length ? "..." : "");
        };
        const userStore = useUserStore(); // 使用 Pinia 的狀態
    
        const navigateToDetail = (postid, event) => {
            const excludedElements = [".post-actions", ".action-btn", "a", "button"];
            for (let selector of excludedElements) {
            if (event.target.closest(selector)) {
                return; // 如果點擊的是按鈕、連結，就不觸發跳轉
            }
            }
            router.push(`/blog/find/${postid}`);
        };
        const navigateToMember = (userid, event) => {
            const excludedElements = ['.follow-button', 'button'];  // 排除的元素，如关注按钮
            for (let selector of excludedElements) {
                if (event.target.closest(selector)) {
                return;  // 如果点击的是排除的元素，跳过跳转
                }
            }
            // 跳转到用户个人页面
            router.push(`/blog/blogprofile/${userid}`);
            };
    
        const formatDate = (date) => {
            if (!date) return "";
            const options = { year: "numeric", month: "2-digit", day: "2-digit" };
            return new Date(date).toLocaleDateString("zh-TW", options);
        };
    
        const fetchProfile = async () => {
            try {
            const response = await axiosapi.get("/ajax/secure/profile", {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
            });
            if (response.data.success) {
                member.value = response.data.user || {};
                isAdmin.value = response.data.user.role === "ADMIN";
                userId.value = member.value.userid;
            } else {
                // Swal.fire("味登入", "登入體驗更好");
            }
            profileLoaded.value = true;
            } catch (error) {
            console.error("Fetch profile failed:", error);
            Swal.fire("錯誤", "無法取得會員資料", "error");
            }
        };
    
        const fetchPosts = async (query = "") => {
            try {
            const requestData = {
                page: currentPage.value,
                size: 100,
                checklike:member.value.userid,
                repost:true,
            };
    
            // 動態設定排序條件
            requestData[sortBy.value === "likes" ? "sortByLikes" : "sortByTime"] = true;
    
            if (query) {
            requestData.postTitle = query; // 加入搜尋條件
            isSearch.value=true;
            } 
            if (selectedPlace.value === 'follow') {
            requestData.repost=true;
            requestData.followerId = member.value.userid;  
            } else if (selectedPlace.value !== null||selectedPlace!=='users') {
            // 只有選擇地點時才加入 place
            requestData.place = selectedPlace.value;
            }
        
    
            const response = await axiosapi.post(
                "/api/posts/post",requestData,
                {
                headers: {
                    "Content-Type": "application/json",
                    "Cache-Control": "no-cache",
                },
                }
            );
    
            if (response.data.postdto && response.data.postdto.length > 0) {
                posts.value = response.data.postdto.filter(
                (post) => !post.repost && post.repostDTO === null      
                );
                noPosts.value = false; 
            } else {
                posts.value = [];
                Swal.fire("沒有結果喔", "沒有貼文", );     
            }
            } catch (error) {
            console.error("Fetch posts failed:", error);
            Swal.fire("錯誤", "無法取得貼文", "error");
            }
        };
    
        //tab
        const switchPlace = (placeName) => {
            //searchStore.setselectedPlace(placeName);
            if (placeName === 'follow') {
            selectedPlace.value = 'follow';
        } else{
            selectedPlace.value = placeName; 
        }
        currentPage.value = 1;
        // const url = new URL(window.location.href);
        // url.search = ''; // 清空查詢參數
        // window.history.replaceState(null, '', url);
    
        searchStore.resetSearch(); // 清空搜索
        fetchPosts();
        };
    
        const switchTab = (tabName) => {
        selectedTab.value = tabName; // 更新 selectedTab 為傳入的 tabName
        //searchStore.setselectedPlace(null);
        if (tabName === 'users') {
        fetchUsers(searchStore.searchTitle); // 搜索用戶
         //fetchDataWithStatus();
        } else if (tabName === 'posts') {
        fetchPosts(searchStore.searchTitle); // 搜索貼文
        }
    };
    
    
        //分頁
        const nextPage = () => {
            currentPage.value++;
            fetchPosts();
        };
    
        const prevPage = () => {
            if (currentPage.value > 1) {
            currentPage.value--;
            fetchPosts();
            }
        };
    
        const repostPost = async (postid) => {
            try {
            const data = {
                postid: postid,
                userid: member.value.userid,
            };
    
            const response = await axiosapi.post("/api/posts/repost/forward", data, {
                headers: {
                "Content-Type": "application/json",
                },
            });
    
            if (response.data.repost) {
                Swal.fire("成功", "轉發成功！", "success");
                await fetchPosts();
            } else {
                Swal.fire("錯誤", "轉發失敗！", "error");
            }
            } catch (error) {
            console.error("轉發請求失敗:", error);
            Swal.fire("錯誤", "無法執行轉發操作", "error");
            }
        };
    
        const likePost = async (postid) => {
            try {
            const data = {
                postid: postid,
                userid: member.value.userid,
                interactionType: "LIKE",
            };
    
            const response = await axiosapi.post("/api/posts/insertinteraction", data, {
                headers: {
                "Content-Type": "application/json",
                },
            });
    
            if (response.data.success) {
                await fetchPosts();
            } else {
                Swal.fire("錯誤", "點讚失敗！", "error");
            }
            } catch (error) {
            console.error("點讚請求失敗:", error);
            Swal.fire("錯誤", "無法執行點讚操作", "error");
            }
        };
    
        const collectPost = async (postid) => {
            try {
            const data = {
                postid: postid,
                userid: member.value.userid,
                interactionType: "COLLECT",
            };
    
            const response = await axiosapi.post("/api/posts/insertinteraction", data, {
                headers: {
                "Content-Type": "application/json",
                },
            });
    
            if (response.data.success) {
                await fetchPosts();
            } else {
                Swal.fire("錯誤", "點讚失敗！", "error");
            }
            } catch (error) {
            console.error("點讚請求失敗:", error);
            Swal.fire("錯誤", "無法執行點讚操作", "error");
            }
        };
        
        const setSort = (type) => {
            if (sortBy.value !== type) {
            sortBy.value = type;
            fetchPosts();
            }
        };
        const route = useRoute();
        const resetSearch = () => {
        searchStore.resetSearch();  // 调用 Pinia store 中的 resetSearch 方法
    };
    
        watch(
            () => route.query.title,
            (newQuery) => {
            if (newQuery) {
                fetchPosts(newQuery); // 如果有搜尋條件就請求搜尋
            } else {
                fetchPosts(); // 沒有搜尋條件則請求普通的 fetchPosts
            }
            },
            { immediate: true }
            
        );
    
        //watch
        // 監聽 sortBy 的變化，當選擇變更時請求 fetchPost
    
        onMounted(async () => {
            selectedPlace.value = null;
            //selectedPlace.value = places.value[0].id;
            await fetchPosts();
            await fetchProfile();
            //await fetchDataWithStatus();
            const query = route.query.title || ''; // 如果 query.title 為 undefined，則使用空字串
            fetchPosts(query); // 根據查詢條件抓取貼文
        });
    
        return {
            profileLoaded,
            member,
            isAdmin,
            formatDate,
            likePost,
            collectPost,
            repostPost,
            posts,
            navigateToDetail,
            navigateToMember,
            currentPage,
            nextPage,
            prevPage,
            switchPlace,
            selectedPlace,
            getFirstImage,
            //getContentWithoutImages,
            getTextPreview,
            userStore,
            setSort,
            sortBy,
            searchQuery,
            isSearch,
            switchTab,
            searchStore,
            selectedTab,
            users,
            toggleFollow,
            defaultProfilePic,
            fetchPosts,
        };
        },
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
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
  justify-content: flex-start; /* 将内容靠左 */
  width: 100%;
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
  background-color: #005AB5;
  color: white;
}

    .posts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    }

    .post-card {
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    overflow: hidden;
    background-color: white;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .post-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
    }

    .post-image-container {
    width: 100%;
    height: 200px;
    overflow: hidden;
    position: relative;
    }

    .post-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
    }

    .post-image-container:hover .post-image {
    transform: scale(1.05);
    }

    .post-content-preview{
    padding: 16px;
    font-size: 14px;
    color: #555;
    line-height: 1.5;
    }

    .post-content h3 {
    margin: 0 0 10px;
    font-size: 18px;
    color: #333;
    }

    .read-more {
    display: inline-block;
    margin: 10px 0;
    color: #ff4757;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s;
    }

    .read-more:hover {
    color: #ff6b81;
    }

    .post-meta,
    .post-stats {
    padding: 0 16px 10px;
    font-size: 12px;
    color: #888;
    }

    .post-meta p,
    .post-stats p {
    margin: 5px 0;
    }

    .post-actions {
    display: flex;
    justify-content: space-around;
    padding: 10px;
    border-top: 1px solid #e0e0e0;
    background-color: #f9f9f9;
    }

    .action-btn {
    border: none;
    background: none;
    cursor: pointer;
    font-size: 14px;
    color: #666;
    display: flex;
    align-items: center;
    gap: 5px;
    transition: color 0.3s;
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

    /* 作者信息樣式 */
    .author-info {
    padding: 16px;
    }

    .author-header {
    display: flex;
    align-items: center;
    gap: 10px;
    }

    .profile-picture-container {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    }

    .profile-picture {
    width: 100%;
    height: 100%;
    object-fit: cover;
    }

    .default-profile {
    width: 100%;
    height: 100%;
    background-color: #ccc;
    border-radius: 50%;
    }

    .author-name {
    font-size: 14px;
    color: #333;
    }

    /* REPOST 樣式 */
    .repost-header {
    padding: 10px 16px;
    background-color: #f9f9f9;
    border-bottom: 1px solid #e0e0e0;
    }

    .interaction-info {
    display: flex;
    align-items: center;
    gap: 8px;
    }

    .repost-profile-container {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    overflow: hidden;
    }

    .small-profile {
    width: 100%;
    height: 100%;
    object-fit: cover;
    }

    .interaction-name {
    font-size: 12px;
    color: #666;
    margin: 0;
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

  /* 用戶列表容器 */
.users-list {
  display: flex;
  flex-direction: column; /* 垂直排列 */
  gap: 12px; /* 用戶之間的間距 */
  padding: 16px;
}

/* 每個用戶的容器 */
.user-item {
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: space-between; /* 頭像、信息和按鈕之間分散排列 */
  padding: 8px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.user-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 用戶頭像 */
.profile-picture-container {
  width: 50px;
  height: 50px;
  border-radius: 50%; /* 圓形頭像 */
  overflow: hidden;
  flex-shrink: 0; /* 防止頭像被壓縮 */
}

.profile-picture {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 圖片填充 */
}

/* 用戶信息 */
.user-info {
  flex-grow: 1; /* 佔用剩餘空間 */
  margin-left: 12px;
}

.username {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.nickname {
  font-size: 14px;
  margin: 4px 0 0;
  color: #666;
}

/* 關注按鈕 */
.follower-button {
  padding: 8px 16px;
  border: none;
  border-radius: 20px; /* 圓角按鈕 */
  background-color: #ff5a5f; /* 小紅書風格紅色 */
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
  flex-shrink: 0; /* 防止按鈕被壓縮 */
}

.follower-button.active {
  background-color: #ccc; /* 已關注狀態 */
}

.follower-button:hover {
  background-color: #e04a50; /* 懸停效果 */
}

/* 確保用戶卡片內部的排版不會有額外的元素 */
.user-item > * {
  margin-right: 12px; /* 頭像、信息和按鈕之間的間距 */
}

    </style>
