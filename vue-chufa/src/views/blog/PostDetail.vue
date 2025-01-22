<template>
    <div v-if="post">
        <p>{{ id }}</p>
        <h1>{{ post.postTitle }}</h1>
        <p>{{ post.postContent }}</p>
        <p><strong>發布時間：</strong>{{ formatDate(post.postTime) }}</p>
        <p><strong>作者：</strong>{{ post.member.name }} ({{ post.member.nickname }})</p>
        <p><strong>文章狀態：</strong>{{ post.postStatus }}</p>
        <p><strong>連結：</strong><a :href="post.postLink" target="_blank">{{ post.postLink }}</a></p>
        <router-link to="/">返回文章列表</router-link>
    </div>
    <div v-else>
        <p>正在載入文章詳情...</p>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Swal from 'sweetalert2';
import axiosapi from '@/plugins/axios';

export default {
    props: ['id'], // 接收路由參數 id
    setup(props) {
        const post = ref(null); // 使用 ref 來儲存文章資料
        const id = props.id; // 獲取從路由傳遞過來的 id

        // 格式化日期的函數
        const formatDate = (date) => {
            if (!date) return '';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString('zh-TW', options);
        };

        // 根據 ID 獲取文章詳情
        const fetchPostDetail = async (id) => {
            try {
                const response = await axiosapi.post('/api/posts/post', {
                    postid: id,
                }, {
                    headers: {
                        'Content-Type': 'application/json',  // 设置请求体类型为 JSON
                    }
                });

                if (response.data.postdto && response.data.postdto.length > 0) {
                    post.value = response.data.postdto[0]; // 正確賦值
                } else {
                    Swal.fire('錯誤', '無法獲取文章詳情', 'error');
                }
            } catch (error) {
                console.error('取文章詳情失敗:', error.response?.data || error.message);
                Swal.fire('錯誤', '無法獲取文章詳情', 'error');
            }
        };

        // 在組件加載時根據 `id` 獲取文章詳情
        onMounted(() => {
            console.log('接收到的文章ID:', id); // 輸出接收到的文章ID
            if (id) {
                fetchPostDetail(id); // 根據 id 請求文章資料
            }
        });

        return {
            post,
            formatDate,
            id
        };
    }
};
</script>
