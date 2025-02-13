<template>

    <div class="postBox">
        <div class="postBody">
            <div class="title-editPost">
                <h1 class="title">{{ postData.postTitle }}</h1><!-- 標題 -->
                <EditPost v-if="member.userid == memberId" :postData="postData" :tags="tags"></EditPost>
            </div>
            <div class="memberPost">
                <img v-if="memberPicture!=null" :src="'data:image/jpeg;base64,' + memberPicture" class="memberPicture">
                <img v-else src="@/assets/empty.png" class="memberPicture"/>
                <p class="memberName">{{ memberName }}</p><!-- Post作者 -->
                <p class="postTime"><strong>發布時間:</strong>{{ postData.postTime }}</p>
                <p class="postStatus">文章狀態：{{ postData.postStatus }}</p>
            </div>

            <div v-if="schedule!=null" class="openSchedule">
                <button v-if="!openSchedule" class="openScheduleButton" @click="openSchedule = !openSchedule">打開行程</button>
                <button v-else class="closeScheduleButton" @click="openSchedule = !openSchedule">關閉行程</button>
                行程名稱:{{ schedule.tripName }}
            </div>
            <div v-if="schedule!=null && openSchedule" class="schedule">
                <div class="scheduleDataText">
                    <h2>行程資料</h2>
                    <p>行程名稱:{{ schedule.tripName }}</p>
                    <p>行程ID:{{ schedule.tripId }}</p>
                    <p>行程開始時間:{{ schedule.startDate }}</p>
                    <p>行程結束時間:{{ schedule.endDate }}</p>
                </div>
                <img class="scheduleDataImg" :src="img" alt="Base64 圖片">
            </div>

            <div v-html="htmlContent"></div>
            <p v-if="postData.postLink!=null">連結：<a :href="postData.postLink" target="_blank">{{ postData.postLink }}</a></p>
            <div v-if="tags.length!=0" class="TagsBox">
                <div v-for="tagData in tags">
                    <tagBrowse :tagData="tagData"></tagBrowse>
                </div>
            </div>
        </div>
    </div>
    
    <!-- <div>
        <button @click="outputImg">提取圖片</button>
        <div v-html="imgText"></div>
    </div> -->
    
    <div class="commentBox">
        <div class="commentBody">
            <InputComment></InputComment>
            <Comment></Comment>
        </div>
    </div>
</template>

<script setup>
    import tagBrowse from "@/components/Post/tagBrowse.vue";
    import EditPost from "@/components/Post/EditPost.vue";
    import InputComment from "@/components/Comment/InputComment.vue";
    import Comment from "@/components/Comment/Comment.vue";

    import axiosapi from "@/plugins/axios.js";
    import { ref, onMounted } from "vue";
    import { useRoute, useRouter } from "vue-router";
    import Swal from "sweetalert2";

    import { useUserStore } from '@/stores/user'
    const user = useUserStore()
    const { member, isLoggedIn } = user

    import { useScheduleStore } from "@/stores/ScheduleStore";
    const scheduleStore = useScheduleStore();

    const openSchedule = ref(false)

    const router = useRouter();
    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    const memberName=ref('')
    const memberPicture=ref(null)
    const memberId=ref('')
    const postData = ref('')
    const htmlContent = ref('')

    const Check = async () => {
    Swal.fire({
        title: "錯誤",
        text: "搜尋不到這篇文章",
        icon: "error", // success, error, warning, info, question
        confirmButtonText: "是的",
        confirmButtonColor: "blue",
    }).then((result) => {
        if (result.isConfirmed) {
        router.push("/");
        }
    });
    };

    const tags = ref([])
    const img = ref(null)
    const schedule = ref(null)
    const findPost=async ()=>{
        console.log("搜尋Post文章")
        const response =await axiosapi.get(`/post/findById/${postid.value}`);
        if (response.data.successs) {
            console.log(response.data)
            postData.value = response.data.list[0]
            memberName.value = postData.value.member.name
            for(let tag in postData.value.tagsBeans){
                tags.value.push(postData.value.tagsBeans[tag])
            }
            if(postData.value.member.profilePicture!=null){
                memberPicture.value = postData.value.member.profilePicture
            }
            memberId.value = postData.value.member.userid
            htmlContent.value = response.data.list[0].postContent
            if(response.data.list[0].scheduleBean!=null){
                schedule.value = response.data.list[0].scheduleBean
                img.value = `data:image/png;base64,${schedule.value.coverPhoto}`
            }
        } else {
            Check()
        }
    }
    onMounted(()=>{
        findPost()
    })

//   // 宣告HTML字串
//     const imgText = ref('')
//   // 從html標籤提取<img>標籤
//     const outputImg = () => {
//     console.log("提取圖片")
//     // 用DOMParser將 內容 從text解析為html文件
//     const parser = new DOMParser();
//     const doc = parser.parseFromString(/*放入內容*/postData.value.postContent, "text/html");
//     // 提取 html文件 中的 <img> 標籤
//     const imgHtml = doc.querySelector("img")
//     // 確認 imgHtml 是否存在
//     if(imgHtml!=null){
//       // 將 HTML文檔 轉字串
//         imgText.value = imgHtml.outerHTML
//         console.log(imgText)
//     }
//     }
</script>

<style scoped>
    .commentBox{
        display: flex; /* 啟用Flexbox佈局 */
        justify-content: center; /* 主軸對齊 */
    }
    .commentBody{
        width: 1000px;
        padding: 25px;
        margin: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
    }
    .postBox{
        display: flex; /* 啟用Flexbox佈局 */
        justify-content: center; /* 主軸對齊 */
    }
    .postBody{
        width: 1000px;
        padding: 25px;
        margin: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
    }
    .title-editPost{
        display: flex; /* 啟用Flexbox佈局 */
        justify-content: center;
        align-items: center;
    }
    .title{
        margin: 10px;
        font-size: 50px;
    }
    .memberPost{
        display: flex; /* 啟用Flexbox佈局 */
        align-items: center;
        flex-grow: 1;
    }
    .memberPicture{
        width: 40px;
        height: 40px;
        border-radius: 50%;
        border: 2px solid #ddd;
        margin: 5px;
    }
    .memberName{
        margin: 5px;
        font-size: 20px;
    }
    .postTime{
        margin: 5px;
        font-size: 14px;
        color: rgb(160, 160, 160);
    }
    .postStatus{
        margin: 5px;
        font-size: 14px;
        color: rgb(160, 160, 160);
    }

    .openScheduleButton{
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
    .openScheduleButton:hover {
        transform: scale(1.05);
    }
    .closeScheduleButton{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: rgb(226, 66, 66);
        color: white;
        margin-bottom: 10px; 
        margin-top: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .closeScheduleButton:hover {
        transform: scale(1.05);
    }
    .schedule{
        display: flex; /* 啟用Flexbox佈局 */
        flex-direction: row-reverse;
        justify-content: space-around;
        padding: 25px;
        margin: 20px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
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

    .Comment{
        border: 5px solid black;
        margin: 20px;
    }
    .TagsBox {
        display: flex;
        flex-wrap: wrap;
    }
</style>
