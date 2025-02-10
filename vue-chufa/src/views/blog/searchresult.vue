    <template>
        <div class="search-results">
        <h2>搜尋結果</h2>
    
        <div v-if="loading" class="loading-spinner">載入中...</div>
    
        <div v-if="!loading && posts.length === 0" class="no-results">
            <p>找不到符合條件的貼文。</p>
        </div>
    
        <div v-if="!loading && posts.length > 0" class="post-list">
            <div v-for="post in posts" :key="post.postid" class="post-item" :class="{ 'repost': post.repost }">
            <!-- 轉發貼文處理 -->
            <div v-if="post.repost" class="repost-header">
                <div class="interaction-info">
                <div class="repost-profile-container">
                    <img v-if="post.member?.profilePicture" 
                        :src="'data:image/jpeg;base64,' + post.member.profilePicture" 
                        alt="Reposter Profile Picture" 
                        class="profile-picture small-profile">
                </div>
                <p class="interaction-name">{{ post.member.nickname }} ({{ post.member.name }}) 轉發貼文</p>
                </div>
            </div>
    
            <!-- 發文者資訊 -->
            <div class="author-info">
                <div class="author-header">
                <div class="profile-picture-container">
                    <router-link :to="`/blog/blogprofile/${post.member.userid}`">
                    <img v-if="post.repostDTO?.member?.profilePicture" 
                        :src="'data:image/jpeg;base64,' + post.repostDTO.member.profilePicture" 
                        alt="Author Profile Picture" 
                        class="profile-picture">
                    <div v-else class="default-profile"></div>
                    </router-link>
                </div>
                <div class="author-name">
                    <strong>{{ post.repostDTO ? post.repostDTO.member.nickname : post.member.nickname }} ({{ post.repostDTO?.member?.name || post.member.name }})</strong>
                </div>
                </div>
                <h3>{{ post.repostDTO ? post.repostDTO.postTitle : post.postTitle || '無標題' }}</h3>
            </div>
    
            <p class="post-content">{{ post.postContent }}</p>
            <a v-if="post.postLink" :href="post.postLink" target="_blank" class="read-more">閱讀更多</a>
    
            <div class="post-meta">
                <p>發佈時間: {{ formatDate(post.repost ? post.repostDTO.postTime : post.postTime) }}</p>
                <p v-if="post.repostDTO">互動時間: {{ formatDate(post.postTime) }}</p>
                <p>貼文類型: {{ post.repost ? 'REPOST' : '原創' }}</p>
            </div>
    
            <div class="post-stats">
                <p>轉發次數: {{ post.repostCount }}</p>
                <p>點讚數: {{ post.likeCount }}</p>
            </div>
    
            <div class="post-actions">
                <button @click="likePost(post.postid)" class="action-btn like-btn">👍 點讚</button>
                <button @click="repostPost(post.postid)" class="action-btn repost-btn">🔁 轉發</button>
                <button @click="collectPost(post.postid)" class="action-btn collect-btn">❤️ 收藏</button>
            </div>
            </div>
        </div>
        </div>
    </template>
    
    <script>
    import { ref, onMounted, watch } from 'vue';
    import axios from '@/plugins/axios.js';
    import Swal from 'sweetalert2';
    import { useRoute } from 'vue-router';
    
    export default {
        setup() {
        const posts = ref([]);
        const searchQuery = ref('');
        const loading = ref(true);
    
        // 格式化日期
        const formatDate = (date) => {
            if (!date) return '';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString('zh-TW', options);
        };
    
        // 獲取搜尋結果
        const fetchSearchResults = async (query) => {
            loading.value = true;
            try {
            const response = await axios.post('http://localhost:8080/api/posts/post', {
                postTitle: query,
            }, {
                headers: {
                'Content-Type': 'application/json',
                },
            });
    
            if (response.data.postdto && response.data.postdto.length > 0) {
                posts.value = response.data.postdto;
            } else {
                Swal.fire('沒有搜尋結果', '未找到相關貼文。', 'info');
            }
            } catch (error) {
            console.error('搜尋錯誤:', error);
            Swal.fire('錯誤', '無法搜尋貼文', 'error');
            } finally {
            loading.value = false;
            }
        };
    
        // 監聽路由參數變化，並重新獲取搜尋結果
        const route = useRoute();
        watch(
            () => route.query.title, 
            (newQuery) => {
            if (newQuery && newQuery !== searchQuery.value) {
                searchQuery.value = newQuery;
                fetchSearchResults(newQuery);
            }
            },
            { immediate: true }
        );
    
        // 頁面加載時獲取搜尋結果
        onMounted(() => {
            const query = route.query.title || ''; // 如果 query.title 為 undefined，則使用空字串
            searchQuery.value = query;
            fetchSearchResults(query);
        });
    
        return {
            posts,
            searchQuery,
            loading,
            formatDate,
            likePost: (postid) => {
            console.log(`點讚貼文 ID: ${postid}`);
            },
            repostPost: (postid) => {
            console.log(`轉發貼文 ID: ${postid}`);
            },
            collectPost: (postid) => {
            console.log(`收藏貼文 ID: ${postid}`);
            }
        };
        },
    };
    </script>
    
    <style scoped>
    /* 搜尋結果樣式 */
    .search-results {
        padding: 20px;
    }
    
    .loading-spinner {
        text-align: center;
    }
    
    .no-results {
        text-align: center;
        font-size: 18px;
    }
    
    .post-list {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr;
        gap: 20px;
    }
    
    .post-item {
        border: 1px solid #ddd;
        padding: 20px;
        border-radius: 8px;
        background-color: #f9f9f9;
    }
    
    .post-actions {
        display: flex;
        gap: 10px;
    }
    </style>
    