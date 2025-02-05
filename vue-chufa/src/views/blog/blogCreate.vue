<template>
    <div>
        <h1>文章編輯器</h1>
        <div class="form-group">
        <label for="title">標題</label>
        <input type="text" id="title" name="title" placeholder="請輸入文章標題" v-model="title">
        </div>
        <!-- Quill 編輯器 -->
        <div id="editor">
        <p>請輸入內容</p>
        </div>
        <!-- 提交按鈕 -->
        <button @click="submitArticle">提交文章</button>
    </div>
</template>

<script >
import Quill from "quill";
import "quill/dist/quill.snow.css"; // Quill 的樣式文件
import axiosapi from '@/plugins/axios.js';
import Swal from "sweetalert2";
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/user'
import { useRouter } from "vue-router";

export default {
    setup(){
        const quill = ref()
        const title = ref()
        const router = useRouter();

        const toolbarOptions = [
            [{ header: [1, 2, false] }], // 標題級別
            ['bold', 'italic', 'underline'], // 字體樣式
            [{ list: 'ordered' }, { list: 'bullet' }], // 列表
            ['link', 'image', "video"], // 嵌入超連接和圖片和視頻
        ]

        const CheckLogin = async () => {
            if (!isLoggedIn) {
                router.push("/");
                Swal.fire({
                    title: '錯誤,請先登入再創建貼文',
                    text: '是否跳到登入畫面',
                    icon: 'error', // success, error, warning, info, question
                    confirmButtonText: '是的',
                    confirmButtonColor: 'blue',
                    cancelButtonText: '取消',
                    cancelButtonColor: 'red',
                    showCancelButton: true,
                }).then((result) => {
                    if (result.isConfirmed) {
                        router.push("/secure/Login");
                    }
                })
                return;
            }
        }

        onMounted(()=>{
            CheckLogin()
            // 初始化 Quill
            quill.value = new Quill("#editor", {
                theme: "snow",
                modules: {
                    toolbar: toolbarOptions 
                },
            });
        })

        const user = useUserStore()
        const { member, isLoggedIn } = user
        const submitArticle= async () => {
            if(isLoggedIn){
                try {
                    const body = {
                    "postTitle":title.value,
                    "postContent":quill.value.root.innerHTML,
                    "userid":member.userid,
                    };
                    const response = await axiosapi.post('/post/create',body);
                    alert(response.data.message);
                } catch (error) {
                alert(error);
                }
            }else{
                alert("請登入帳號");
            }
        }

        return{
            quill, title, submitArticle
        }
    },

}
</script>

<style>
    .form-group {
        margin-bottom: 15px;
    }
    .form-group label {
        display: block;
        margin-bottom: 10px;
        font-weight: bold;
    }
    .form-group input {
        width: 50%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        font-size: 1em;
    }
</style>
