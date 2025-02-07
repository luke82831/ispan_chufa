<template>
    <div>
        <div class="form-group">
        <button @click="hahaha">重置文章</button>
            <label for="title">標題</label>
            <input type="text" id="title" name="title" placeholder="請輸入文章標題" v-model="title">
        </div>
        <!-- Quill 編輯器 -->
        <div ref="editorContainer" style="height: 200px;">
        
        </div>
        <!-- 提交按鈕 -->
        <button @click="outputEditPost">確認編輯</button>
    </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import Quill from "quill";
import "quill/dist/quill.snow.css";
import axiosapi from '@/plugins/axios.js';

const props = defineProps(['postData'])
const title = ref()

const editorContainer = ref(null);
let quill;

const hahaha = () => {
    console.log("重置文章")
    title.value = props.postData.postTitle
    quill.root.innerHTML = props.postData.postContent
}

onMounted( () => {
    quill = new Quill(editorContainer.value, {
        theme: "snow",
        placeholder: "請輸入內容...",
        modules: {
        toolbar: [
            [{ header: [1, 2, false] }], // 標題級別
            ['bold', 'italic', 'underline'], // 字體樣式
            [{ list: 'ordered' }, { list: 'bullet' }], // 列表
            ['link', 'image', "video"], // 嵌入超連接和圖片和視頻
        ],
        },
    });

    hahaha()
})

const outputEditPost = async () => {
    const body = {
        "postid":props.postData.postid,
        "postTitle":title.value,
        "postContent":quill.root.innerHTML,
    }
    console.log(body)
    const response = await axiosapi.put('/post/update',body);
    console.log(response.data)

    alert(response.data.message);
    if(response.data.successs){
        console.log(response.data)
        window.location.reload();
    }
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
