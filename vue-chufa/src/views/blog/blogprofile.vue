    <template>
        <div class="profile-page">
        <div v-if="member" class="profile-container">
            <h2 class="section-title">會員資料</h2>
            <div class="profile-details">
            <!-- <img :src="'data:image/jpeg;base64,' + member.profilePicture" alt="Profile Picture" v-if="member.profilePicture" class="profile-picture" />
            -->
            <img 
                :src="member.profilePicture ? 'data:image/jpeg;base64,' + member.profilePicture :  defaultProfilePicture"
                alt="Profile Picture" 
                v-if="member.profilePicture || defaultProfilePicture" 
                class="profile-picture" />
            <div class="info"> 
                <p><strong>姓名:</strong> {{ member.name }}</p>
                <p><strong>Email:</strong> {{ member.email }}</p>
                <p><strong>生日:</strong> {{ formatDate(member.birth) }}</p>
                <p> {{ member.bio }}</p>
                <div class="follow-stats">
                        <router-link :to="`/blog/followlist/${member.userid}`" class="follow-link">
                            <span><strong>關注:</strong> {{ followersCount }}</span>
                            <span><strong>粉絲:</strong> {{ followingCount }}</span>
                        </router-link>
                </div>
            <button
            :class="['follow-button', { active: member.isFollowing }]"
            @click.stop="toggleFollow(member)"
            >
            {{ member.isFollowing ? '已關注' : '未關注' }}
            </button>
            </div>
            </div>

        <div class="tabs-container">
        <button :class="{ active: activeTab === 'myPosts' }" @click="setActiveTab('myPosts')">我的貼文</button>
        <button :class="{ active: activeTab === 'likedPosts' }" @click="setActiveTab('likedPosts')">點讚貼文</button>
        <button :class="{ active: activeTab === 'sharedPosts' }" @click="setActiveTab('sharedPosts')">轉發貼文</button>
        <button :class="{ active: activeTab === 'savedPosts' }" @click="setActiveTab('savedPosts')">收藏貼文</button>
        </div>
            
            <div class="posts-grid" v-if="posts.length > 0">
                    <PostCard
                    v-for="post in posts"
                    :key="post.postid"
                    :post="post"
                    :member="member"
                    :formatDate="formatDate"
                    @update-posts="fetchPosts()"
                    />
            </div>


            <p v-else>沒有貼文。</p>
            </div>

            <div v-else>
            <p>載入中...</p>
            </div>
        </div>
    </template>

    <script>
    import { ref, onMounted,computed } from 'vue';
    import axios from '@/plugins/axios.js';
    import Swal from 'sweetalert2';
    import { useRouter } from 'vue-router';
    import { useUserStore } from "@/stores/user.js";
    import PostCard from '@/components/Postcard.vue';
    import axiosapi from '@/plugins/axios.js';
    import defaultProfilePic from '@/assets/empty.png';
    

    export default {
    props: ['bloghomeid'],
    components: {
        PostCard // 註冊 PostCard 元件
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
            const activeTab = ref('myPosts');

        // 用來從後端取得關注者和被關注者的人數
        const fetchCount = async (type) => {
        const url = type === 'followers'
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
                    console.log('Member data:', member.value);
                    } else {
                    Swal.fire('沒有用戶資料', '無法獲取該用戶的資料', 'info');
                    }
                } catch (error) {
                    console.error('Fetch user profile failed:', error);
                    Swal.fire('錯誤', '無法獲取用戶資料', 'error');
                }
            
        };
    

// 检查当前用户是否已关注该用户
const checkFollowingStatus = async (member) => {
    if (!member || !member.userid) {
        console.error('Member object or userid is missing:', member);
        return;
    }

    try {
        // 获取该用户是否被当前用户关注的状态
        const response = await axiosapi.post('/follow/isFollowing', {
            followedid: userStore.member.userid,
            followerid: member.userid, // 使用传递过来的普通对象
        });
        // 更新用户的关注状态
        member.isFollowing = response.data;
        console.log(bloghomeid + " " + member.isFollowing);
    } catch (error) {
        console.error('Error checking following status:', error);
    }
};
 
 

// 切换关注状态
const toggleFollow = async (member) => {
    try {
        const action = member.isFollowing ? 'unfollow' : 'follow';
        const response = await axiosapi.post('/follow/verb', {
            followerid: userStore.member.userid,
            followedid: member.userid,
            action,
        });

        if (response.data.success) {
            // 切换关注状态
            member.isFollowing = !member.isFollowing;
            console.log(`User ${member.userid} is now ${member.isFollowing ? 'following' : 'not following'}`);
        } else {
            console.error('Failed to toggle follow status:', response.data.message);
        }
    } catch (error) {
        console.error('Failed to toggle follow status:', error);
        Swal.fire('錯誤', '無法更新關注狀態', 'error');
    }
};
   


        

        // 載入關注人數和被關注人數
        const loadCounts = async () => {
        followersCount.value = await fetchCount('followers');
        followingCount.value = await fetchCount('followed');
        console.log(followersCount.value);
        console.log(followingCount.value);
        };
        console.log(followersCount.value);
        console.log(followingCount.value);
    
        const formatDate = (date) => {
            if (!date) return '';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString('zh-TW', options);
            };
    
    
        const fetchPosts = async (filterType) => {
            try {
            const payload = { };
            if (filterType === 'likedPosts') { payload.likedBy = member.value.userid;payload.repost = true; }
            if (filterType === 'savedPosts') { payload.collectBy = member.value.userid;payload.repost = true; }
            if (filterType === 'myPosts') {
            payload.repost = false;  // 不要 repost 的貼文\
            payload.userid=member.value.userid;
            } else if (filterType === 'sharedPosts') {
            payload.repost = true;  // 只要 repost 的貼文
            payload.userid=member.value.userid;
            }
            //userid: member.value.userid 
            const response = await axiosapi.post('/api/posts/post', payload);
            let postData = response.data.postdto || [];

            // 過濾條件
            if (filterType === 'myPosts') {
            posts.value = postData.filter(post => !post.repost && post.repostDTO === null);
            } else if (filterType === 'sharedPosts') {
            posts.value = postData.filter(post => post.repost || post.repostDTO !== null);
            } else {
            posts.value = postData;
            }
        } catch (error) {
            console.error('Fetch posts failed:', error);
            Swal.fire('錯誤', `無法取得${filterType}的貼文`, 'error');
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
            };
        },
    };
    </script>
    
    <style scoped>
  /* 整體頁面樣式 */
.profile-page {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    font-family: 'Arial', sans-serif;
    color: #333;
}

/* 會員資料容器 */
.profile-container {
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin-bottom: 20px;
}

/* 標題樣式 */
.section-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
}

/* 會員資料區塊 */
.profile-details {
    display: flex;
    align-items: flex-start; /* 讓內容靠上對齊 */
    gap: 20px;
    position: relative; /* 讓關注按鈕可以定位 */
}

/* 大頭照樣式 */
.profile-picture {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #ddd;
}

/* 會員資訊樣式 */
.info {
    flex: 1;
}

.info p {
    margin: 8px 0;
    font-size: 14px;
    color: #555;
}

.info strong {
    color: #333;
    font-weight: bold;
}

/* 關注和粉絲樣式 */
.follow-stats {
    display: flex;
    gap: 16px; /* 關注和粉絲之間的間距 */
    margin-top: 8px;
}

.follow-stats span {
    font-size: 14px;
    color: #333;
}

.follow-stats strong {
    color: #333;
    font-weight: bold;
}

/* 關注按鈕樣式 */
.follow-button {
    position: absolute; /* 絕對定位 */
    top: 0; /* 置頂 */
    right: 0; /* 置右 */
    padding: 8px 16px;
    border: none;
    border-radius: 20px;
    background-color: #46a1e2;
    color: #fff;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.follow-button.active {
    background-color: #ccc;
}

.follow-button:hover {
    background-color: #4a97e0;
}

/* 標籤按鈕容器 */
.tabs-container {
    display: flex;
    gap: 10px;
    margin-top: 20px;
    border-bottom: 1px solid #ddd;
    padding-bottom: 10px;
}

/* 標籤按鈕樣式 */
.tabs-container button {
    padding: 8px 16px;
    border: none;
    border-radius: 20px;
    background-color: #f0f0f0;
    color: #333;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.tabs-container button.active {
    background-color: #4179ea;
    color: #fff;
}

.tabs-container button:hover {
    background-color: #ddd;
}

/* 貼文網格樣式 */
.posts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

/* 無貼文提示樣式 */
p {
    text-align: center;
    color: #888;
    font-size: 16px;
    margin-top: 20px;
}
    </style>
        