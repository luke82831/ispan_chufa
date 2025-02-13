<template>
    <div class="postCreateBox">
        <div class="postCreateBody">
            <div>
                <h1 class="postCreateText">創建文章</h1>
            </div>
            <div>
                <input class="postCreatetitle" type="text" placeholder="標題" v-model="title">
            </div>

            <!-- Quill 編輯器 -->
            <div ref="editorContainer" style="height: 200px;">
                        
            </div>
            <div>
                <button v-if="schedule==null" @click="Schedule" class="addSchedule">加入行程</button>
                <button v-else @click="Schedule" class="addSchedule">更改行程</button>
                <div v-if="buttonSchedule" class="inputSchedule">
                    <inputSchedule></inputSchedule>
                </div>
                <div v-if="schedule!=null" class="scheduleData">
                    <div class="scheduleDataText">
                        <button @click="schedule=null" class="recallSchedule">取消行程</button>
                        <p>行程名稱:{{ schedule.tripName }}</p>
                        <p>行程ID:{{ schedule.tripId }}</p>
                        <p>行程開始時間:{{ schedule.startDate }}</p>
                        <p>行程結束時間:{{ schedule.endDate }}</p>
                    </div>
                    <img :src="getImageSource(schedule.coverPhoto)" alt="行程封面" class="scheduleDataImg"/> 
                </div>
            </div>

            <div class="addTagsBox">
                <addTags :optTagsId="optTagsId"></addTags>
            </div>

            <!-- 提交按鈕 -->
            <div>
                <button @click="submitArticle" class="outputPost">提交文章</button>
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
import inputSchedule from "@/components/PostCreate/inputSchedule.vue";
import eventBus from "@/eventBus";
import addTags from "@/components/PostCreate/addTags.vue";

const buttonSchedule = ref(false)
const Schedule = () => {
    buttonSchedule.value = !buttonSchedule.value
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
const optTagsId = ref([])
onMounted(() => {
    quill = new Quill(editorContainer.value, {
        theme: "snow",
        placeholder: "請輸入內容...",
        modules: {
        toolbar: [
            [{ header: [1, 2, false] }], // 標題級別
            [{ 'size': ['small', false, 'large', 'huge'] }],
            ['bold', 'italic', 'underline'], // 字體樣式
            [{ list: 'ordered' }, { list: 'bullet' }], // 列表
            ['image', "video"], // 嵌入圖片和視頻
        ],
        },
    });

    eventBus.on("inputSchedule", (scheduleData) => {
        schedule.value = scheduleData
        buttonSchedule.value = false
    });
});
const schedule = ref(null)

const submitArticle= async () => {
    let body = {
        "postTitle":title.value,
        "postContent":quill.root.innerHTML,
        "userid":member.userid,
        "tripId":"",
        "tagId":[],
    }

    if(schedule.value != null){
        body.tripId=`${schedule.value.tripId}`
    }
    if(optTagsId.value.length!=0){
        body.tagId=optTagsId.value
    }
    console.log(body)
    const response = await axiosapi.post('/post/create',body);
    alert(response.data.message);
    if(response.data.successs){
        console.log(response.data)
        router.push("/");
    }
}

</script>

<style> /* 加入scoped後，只在這個.vue生效 */
    .postCreateBox{
        display: flex; /* 啟用Flexbox佈局 */
        justify-content: center; /* 主軸對齊 */
        align-items: flex-start; /* 交叉軸對齊 */
    }
    .postCreateBody{
        padding: 25px;
        margin: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        width: 800px;
        box-sizing: border-box;
    }
    .postCreateText{
        margin: 0;
        margin-bottom: 10px;
    }
    .postCreatetitle{
        margin: 0;
        margin-bottom: 10px;
        font-size: 20px;
        padding: 10px;
        border-radius: 6px;
        border: 1px solid #000000;
    }

    .ql-snow{ /* 輸入框 */
        border-radius: 0 0 8px 8px; /* 圆角 */
    }
    .ql-editor {
        font-size: 18px; /* 調整為你想要的大小 */
    }
    .ql-toolbar { /* 工具欄 */
        background-color: #e4f2f5; /* 修改背景颜色 */
        border-radius: 8px 8px 0 0; /* 圆角 */
        padding: 10px; /* 内边距 */
    }
    .ql-toolbar button {
        margin: 5px;
    }
    .ql-toolbar button svg {
        width: 32px;
        height: 32px;
        border-radius: 32px; /* 按钮圆角 */
        background-color: rgb(169, 217, 245);
        fill: #007bff; /* 修改图标颜色 */
        transition: fill 0.3s ease-in-out;
    }
    .ql-picker-label{
        border-radius: 32px; /* 按钮圆角 */
        background-color: rgb(169, 217, 245);
    }
    .ql-editing{
        top: 0px;
        right: 0px;
    }
    .addSchedule{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #17a2b8;
        color: white;
        margin-bottom: 10px; 
        margin-top: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .addSchedule:hover {
        transform: scale(1.05);
    }
    .recallSchedule{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(226, 66, 66);
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .recallSchedule:hover {
        transform: scale(1.05);
    }
    .inputSchedule{
        margin-bottom: 10px; 
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
    }
    .scheduleData{
        padding: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        display: flex;
        flex-direction: row-reverse;
        justify-content: space-around;
        align-items: flex-start;
    }
    .scheduleDataText{
        padding: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
    }
    .scheduleDataImg{
        width: 100px;
        height: 100%;
        object-fit: cover;
        flex-basis: 300px;
    }
    .outputPost{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #28a745;
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .outputPost:hover {
        transform: scale(1.05);
    }
</style>
