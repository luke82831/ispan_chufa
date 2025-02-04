    <template>
        <div class="profile-page">
        <h1>會員資料</h1>
        <!-- 加載中提示 -->
        <div v-if="isLoading" class="loading">
            加載中，請稍候...
        </div>
    
        <!-- 錯誤提示 -->
        <div v-if="isError" class="error">
            資料加載失敗，請稍後重試。
        </div>
    
        <!-- 用戶資料 -->
        <div v-if="!isLoading && !isError">
            <div class="user-info">
            <h2>用戶基本資料</h2>
            <p><strong>ID:</strong> {{ userId }}</p>
            <p><strong>是否為管理員:</strong> {{ isAdmin ? '是' : '否' }}</p>
            </div>
    
            <!-- 關注者列表 -->
            <div class="followers-section">
            <h2>關注列表</h2>
            <div v-if="followers.length === 0">
                <p>目前沒有任何關注者。</p>
            </div>
            <ul v-else class="followers-list">
                <li v-for="follower in followers" :key="follower.userid" class="follower-item">
                <div class="profile-picture">
                    <img
                    :src="follower.profilePicture || defaultProfilePicture"
                    alt="profile picture"
                    />
                </div>
                <div class="follower-info">
                    <h3>{{ follower.nickname }}</h3>
                    <p>ID: {{ follower.userid }}</p>
                    <p>Name: {{ follower.name }}</p>
                </div>
                </li>
            </ul>
            </div>
        </div>
        </div>
    </template>
    <script>
    import { ref, onMounted } from 'vue';
        import { useRouter } from 'vue-router';
        import axiosapi from '@/plugins/axios';
        import Swal from 'sweetalert2';

        export default {
        setup() {
            const router = useRouter();
            const followers = ref([]); // 儲存關注者列表
            const isLoading = ref(false); // 是否正在加載
            const isError = ref(false); // 是否出現錯誤
            const userId = ref(null); // 當前用戶 ID
            const isAdmin = ref(false); // 判斷是否為管理員

            // 格式化日期
            const formatDate = (date) => {
            if (!date) return '';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString('zh-TW', options);
            };


            // 取得關注者列表
            const fetchFollowers = async () => {
            try {
                isLoading.value = true;
                const response = await axiosapi.get(`/follow/followedList/1`);

                if (response.data && Array.isArray(response.data)) {
                followers.value = response.data;
                } else {
                Swal.fire('提示', '目前沒有任何關注者', 'info');
                }
            } catch (error) {
                console.error('Fetch followers failed:', error);
                Swal.fire('錯誤', '無法取得關注者列表', 'error');
                isError.value = true;
            } finally {
                isLoading.value = false;
            }
            };

            // 組件加載時自動執行
            onMounted(async () => {
            await fetchUserProfile();
            if (userId.value) {
                await fetchFollowers();
            }
            });

            return {
            followers,
            isLoading,
            isError,
            formatDate,
            fetchUserProfile,
            fetchFollowers,
            };
        },
        };
    </script>
