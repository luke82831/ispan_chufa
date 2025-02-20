<template>
    <div class="inputCommentBody">
        <h1>新增留言</h1>
        <input type="text" placeholder="請輸入留言" v-model="comment" >
        <button class="outputComment" @click="outputComment">送出</button>
        <button class="removeComment" @click="removeComment">清空</button>
    </div>
</template>
    
<script setup>
    import axiosapi from '@/plugins/axios.js';
    import { ref } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import Swal from "sweetalert2";
    import eventBus from '@/eventBus';

    import DOMPurify from "dompurify" // 用來過濾使用者輸入的資料
    import { useUserStore } from '@/stores/user'
    const user = useUserStore()
    const { member, isLoggedIn } = user

    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    const router = useRouter();
    const CheckLogin = async () => {
        if (!isLoggedIn) {
            Swal.fire({
                title: '錯誤,請先登入再留言',
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

    const comment=ref('')
    const outputComment = async () => {
        if(isLoggedIn){
            console.log("送出Root留言")
            const safeHTML = ref(DOMPurify.sanitize(comment.value))  // 过滤 HTML
            const body = {
                "postId":postid.value,
                "userId":member.userid,
                "content":`${safeHTML.value}`,
                "parentId":""
            }
            const response =await axiosapi.post(`/comment/create`,body);
            if (response.data.successs) {
                console.log(response.data)
                alert(response.data.message);
            }else{
                alert(response.data.message);
            }
            eventBus.emit('outputComment');
            comment.value=''
        }else{
            CheckLogin()
        }
        
    }
    const removeComment = () => {
        comment.value=''
    }

</script>
    
<style scoped>
    .inputCommentBody{
        padding: 10px;
        margin: 0px;
        border-radius: 12px;
        border: 1px solid #e0e0e0;
        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
        display: flex;
        justify-content: flex-start;
        align-items: center;
    }
    .inputCommentBody > h1{
        margin: 10px;
    }
    .inputCommentBody > input{
        font-size: 18px;
        flex-grow: 1;
        margin: 10px;
    }
    .inputCommentBody > button{
        margin: 10px;
    }
    .outputComment{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #17a2b8;
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .outputComment:hover {
        transform: scale(1.05);
    }
    .removeComment{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #d34659;
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .removeComment:hover {
        transform: scale(1.05);
    }
</style>