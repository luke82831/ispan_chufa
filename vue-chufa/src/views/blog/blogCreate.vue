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

<script>
import Quill from "quill";
import "quill/dist/quill.snow.css"; // Quill 的樣式文件
import axiosapi from '@/plugins/axios.js';

export default {
    data() {
        return {
        quill: null, // Quill 編輯器實例
        };
    },
    mounted() {
        // 初始化 Quill
        this.quill = new Quill("#editor", {
        theme: "snow",
        modules: {
            toolbar: {
            container: [
                ["bold", "italic", "underline"], // 字體樣式
                [{ header: [1, 2, 3, false] }], // 標題級別
                [{ list: "ordered" }, { list: "bullet" }], // 列表
                ["image", "video"], // 嵌入圖片和視頻
            ],
            handlers: {
                image: this.imageHandler, // 自定義圖片上傳處理器
            },
            },
        },
        });
    },
    methods: {
        async submitArticle() {
        try {
            const body = {
            "postTitle":title.value,
            "postContent":this.quill.root.innerHTML,
            "userid":"1" //登入功能完成後從user取
            };
            const response = await axiosapi.post('/post/create',body);
            alert(response.data.message);
        } catch (error) {
        alert(error);
        }
        },
    },
    };
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
