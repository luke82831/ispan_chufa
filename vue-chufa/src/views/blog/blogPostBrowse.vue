<template>

    <div class="post">
        <h1 class="title">標題:{{ postData.postTitle }}</h1><!-- 標題 -->
        <EditPost v-show="member.userid == memberId" :postData="postData"></EditPost>
        <p>作者:{{ memberName }}</p><!-- Post作者 -->
        <p><strong>文章狀態：</strong>{{ postData.postStatus }}</p>
        <div v-if="schedule!=null" class="schedule">
            <h2>行程</h2>
            <p>行程名稱:{{ schedule.tripName }}</p>
            <p>行程ID:{{ schedule.tripId }}</p>
            <p>行程開始時間:{{ schedule.startDate }}</p>
            <p>行程結束時間:{{ schedule.endDate }}</p>
            <div class="itinerary-image-container">
                <img :src="img" alt="Base64 圖片">
            </div>
        </div>
        <div v-html="htmlContent"></div><!-- 內容 -->
        <p><strong>連結：</strong><a :href="postData.postLink" target="_blank">{{ postData.postLink }}</a></p>
        <p><strong>發布時間：</strong>{{ postData.postTime }}</p>
    </div>
    
    <!-- <div>
        <button @click="outputImg">提取圖片</button>
        <div v-html="imgText"></div>
    </div> -->
    
    <div class="Comment">
        <InputComment></InputComment>
        <Comment></Comment>
    </div>
</template>

<script setup>
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


    const getImageSource = (coverPhoto) =>
    coverPhoto
    ? coverPhoto.startsWith("data:image")
    ? coverPhoto
    : `data:image/jpeg;base64,${coverPhoto}`
    : "";



    const router = useRouter();
    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    const memberName=ref('')
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

    const img = ref(null)
    const schedule = ref(null)
    const findPost=async ()=>{
        await console.log("搜尋Post文章")
        const response =await axiosapi.get(`/post/findById/${postid.value}`);
        if (response.data.successs) {
            postData.value = response.data.list[0]
            memberName.value = postData.value.member.name
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

<style>
    .schedule{
        border: 5px solid black;
        margin: 20px;
    }
    .post{
        border: 5px solid black;
        margin: 20px;
    }
    .Comment{
        border: 5px solid black;
        margin: 20px;
    }
</style>
