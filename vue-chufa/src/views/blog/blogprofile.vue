    <template>
        <div class="profile-page">
        <div v-if="member" class="profile-container">
            <h2 class="section-title">會員資料</h2>
            <div class="profile-details">
            <img :src="'data:image/jpeg;base64,' + member.profilePicture" alt="Profile Picture" v-if="member.profilePicture" class="profile-picture" />
            <div class="info">
                <p><strong>姓名:</strong> {{ member.name }}</p>
                <p><strong>Email:</strong> {{ member.email }}</p>
                <p><strong>生日:</strong> {{ formatDate(member.birth) }}</p>
                <p><strong>角色:</strong> {{ isAdmin ? '管理員' : '普通會員' }}</p>
                <p><strong>ID:</strong> {{ member.userid }}</p>
            <router-link :to="`/blog/followlist/${member.userid}`" class="follow-link">
            <p><strong>關注人數:</strong> {{ followersCount }}</p>    
            <p><strong>粉絲:</strong> {{ followingCount }}</p>
            </router-link>
            </div>
            </div>

            <h3 class="section-title">博主的貼文</h3>
            
            <ul class="post-list" v-if="posts.length > 0">
                <li v-for="post in posts" :key="post.id" class="post-item">
                <router-link :to="{ name: 'PostDetail', params: { id: post.postid } }">
                <h4>{{ post.postTitle }}</h4>
                </router-link>
                <p>{{ post.postContent }}</p>
                <h4>{{ post.member.name }}</h4>
                <p>{{ post.tags || '無標籤' }}</p>           
                </li>
            </ul>


            <p v-else>此會員尚無貼文。</p>
            </div>

            <div v-else>
            <p>載入中...</p>
            </div>
        </div>
    </template>

    <script>
    import { ref, onMounted } from 'vue';
    import axios from '@/plugins/axios.js';
    import Swal from 'sweetalert2';
    import { useRouter } from 'vue-router';

    export default {
    props: ['bloghomeid'],
        setup(props) {
            const router = useRouter();
            const member = ref({});
            const posts = ref([]);
            const isAdmin = ref(false);
            const profileLoaded = ref(false);
            const bloghomeid = props.bloghomeid;

            const followersCount = ref(0); // 使用 ref 來定義自適應資料
            const followingCount = ref(0);

        // 用來從後端取得關注者和被關注者的人數
        const fetchCount = async (type) => {
        const url = type === 'followers'
            ? `/follow/followercount/${bloghomeid}`
            : `/follow/followingcount/${bloghomeid}`;

        try {
            const response = await axios.get(url);
            // 假設返回的資料是一個物件，包含 `count` 資料欄
            return response.data; // 如果沒有資料則返回0
        } catch (error) {
            console.error(`Error fetching ${type}:`, error);
            return 0;
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
    
        const fetchProfile = async () => {
            try {
                const response = await axios.get(`/api/posts/members/${bloghomeid}`);

                if (response.data) {
                    member.value = response.data; // 存储用户资料
                    } else {
                    Swal.fire('沒有用戶資料', '無法獲取該用戶的資料', 'info');
                    }
                } catch (error) {
                    console.error('Fetch user profile failed:', error);
                    Swal.fire('錯誤', '無法獲取用戶資料', 'error');
                }
            
        };
    
        const fetchPosts = async () => {
            try {
            const response = await axios.post('/api/posts/post', {
                userid: bloghomeid,
            });
    
            if (response.data.postdto && response.data.postdto.length > 0) {
                posts.value = response.data.postdto;
            } else {
                Swal.fire('沒有貼文', '此用戶暫無貼文。', 'info');
            }
            } catch (error) {
            console.error('Fetch posts failed:', error);
            Swal.fire('錯誤', '無法取得貼文', 'error');
            }
        };
    
        onMounted(async () => {
            await fetchProfile();
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
            };
        },
    };
    </script>
    
    <style>
    .profile-page {
    padding: 20px;
    background-color: #f4f7fc;
    min-height: 100vh;
    }

    .section-title {
    font-size: 1.8em;
    color: #333;
    margin-bottom: 15px;
    border-bottom: 3px solid #007bff;
    padding-bottom: 5px;
    }

    .profile-container {
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 30px;
    }

    .profile-details {
    display: flex;
    align-items: center;
    gap: 25px;
    margin-bottom: 25px;
    }

    .profile-picture {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .info p {
    font-size: 1.1em;
    color: #555;
    margin: 5px 0;
    }

    .follow-link {
    color: #007bff;
    text-decoration: none;
    }

    .post-list {
    list-style: none;
    padding: 0;
    }

    .post-item {
    background: #fff;
    margin-bottom: 15px;
    padding: 20px;
    border-radius: 12px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    }

    .post-item h4 {
    margin: 0 0 10px 0;
    color: #007bff;
    font-size: 1.4em;
    }

    .post-item h5 {
    font-size: 1.1em;
    color: #555;
    margin-top: 10px;
    }

    .post-item p {
    color: #777;
    font-size: 1.1em;
    }
    </style>
        