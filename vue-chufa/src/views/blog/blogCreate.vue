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


            <!-- Quill 編輯器 -->
            <div ref="editorContainer" style="height: 200px;">
                        
            </div>


            <div>
                <button v-if="schedule==null" @click="Schedule">加入行程</button>
                <button v-else @click="Schedule">更改行程</button>
                <div v-if="schedule!=null">
                    <p>行程名稱:{{ schedule.tripName }}</p>
                    <p>行程ID:{{ schedule.tripId }}</p>
                    <p>行程開始時間:{{ schedule.startDate }}</p>
                    <p>行程結束時間:{{ schedule.endDate }}</p>
                    <img :src="getImageSource(schedule.coverPhoto)" alt="行程封面" class="itinerary-image"/> 
                </div>
                <div v-if="buttonSchedule">
                    <inputSchedule></inputSchedule>
                </div>
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
import inputSchedule from "@/components/Post/inputSchedule.vue";
import eventBus from "@/eventBus";
const buttonSchedule = ref(false)
const Schedule = () => {
    if(buttonSchedule.value!=true){
        buttonSchedule.value=true
    }else{
        buttonSchedule.value=false
    }
}

const getImageSource = (coverPhoto) =>
coverPhoto
? coverPhoto.startsWith("data:image")
? coverPhoto
: `data:image/jpeg;base64,${coverPhoto}`
: "";

const title = ref()
const router = useRouter();

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

    eventBus.on("inputSchedule", (scheduleData) => {
        schedule.value = scheduleData
        console.log(schedule.value)
        buttonSchedule.value = false
    });
});
const schedule = ref(null)

const submitArticle= async () => {
    if(schedule==null){
        schedule.value=""
    }
    const body = {
        "postTitle":title.value,
        "postContent":quill.root.innerHTML,
        "userid":member.userid,
        "tripId":`${schedule.value.tripId}`,
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
