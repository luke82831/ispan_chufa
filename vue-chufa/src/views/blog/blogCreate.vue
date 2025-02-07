<template>
    <div class="postBox">
        <div>
            <div>
                <h1>發文</h1>
            </div>
            <div class="form-group">
                <h3 for="title">標題</h3>
                <input type="text" id="title" name="title" placeholder="請輸入文章標題" v-model="title">
            </div>

            <!-- <div class="upload-container">
                <h2>上傳封面圖片 / 影片</h2>
                <input type="file" accept="image/*,video/*" @change="handleFileChange" />
                <br />
                <div v-if="preview">
                <img v-if="file.type.startsWith('image')" :src="preview" alt="預覽" width="300" />
                <video v-else :src="preview" width="300" controls />
                </div>
                <br />
                <button @click="handleUpload">上傳</button>
            </div> -->

            <div>
                行程
            </div>
            <!-- Quill 編輯器 -->
            <div ref="editorContainer" style="height: 200px;">
            
            </div>
            <!-- 提交按鈕 -->
            <div>
                <button @click="submitArticle">提交文章</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import Quill from "quill";
import "quill/dist/quill.snow.css";
import axiosapi from '@/plugins/axios.js';
import { useRouter } from "vue-router";
import { useUserStore } from '@/stores/user'

const title = ref()
const router = useRouter();


// const file = ref(null);
// const preview = ref(null);
// // 當選擇檔案後，預覽圖片
// const handleFileChange = (event) => {
//     const selectedFile = event.target.files[0];
//     if (selectedFile) {
//         file.value = selectedFile;
//         preview.value = URL.createObjectURL(selectedFile); // 預覽圖片或影片
//     }
//     console.log(preview.value)
// };
// 上傳檔案
// const handleUpload = async () => {
//     if (!file.value) {
//         alert("請選擇檔案！");
//         return;
//     }
//     console.log(preview.value)

//     const formData = new FormData();
//     formData.append("file", file.value);
    // try {
    //     const response = await fetch("http://localhost:5000/upload", {
    //     method: "POST",
    //     body: formData,
    //     });

    //     const result = await response.json();
    //     alert(result.message);
    // } catch (error) {
    //     console.error("上傳失敗:", error);
    // }
// };

const user = useUserStore()
const { member, isLoggedIn } = user
const editorContainer = ref(null);
let quill;

onMounted(() => {
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
});

const submitArticle= async () => {
    
    const body = {
        "postTitle":title.value,
        "postContent":quill.root.innerHTML,
        "userid":member.userid,
    }
    const response = await axiosapi.post('/post/create',body);
    alert(response.data.message);
    if(response.data.successs){
        console.log(response.data)
        router.push("/");
    }
}

</script>

<style>
    .postBox{
        display: flex;
        justify-content: center; /* 水平居中 */
        align-items: center; /* 垂直居中 */
        flex-direction: column; /* items從上到下 */
        align-items: center;
        width: 100%;
        max-width: 1000px;
        padding: 0px;
        margin: 0 auto;
    }
</style>
