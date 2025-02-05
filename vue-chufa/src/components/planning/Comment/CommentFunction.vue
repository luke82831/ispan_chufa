<template>
    <div>
        <button @click="reply">回覆</button>
        <button>分享</button>
    </div>
    <input v-show="replyInput" type="text" placeholder="請輸入留言" v-model="comment">
    <button v-show="replyInput" @click="outputReply">送出</button>

</template>
    
<script>
    import axiosapi from '@/plugins/axios.js';
    import { ref, onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import Swal from "sweetalert2";

    import eventBus from '@/eventBus';

    import { useUserStore } from '@/stores/user'
    const user = useUserStore()
    const { member, isLoggedIn } = user

    export default {
        props:[
            'parentId',
            'replyUser',
        ],
        setup(props){
            const route = useRoute();
            const postid = ref(route.params.postid); // 取得動態路由參數

            onMounted(()=>{
                if(props.replyUser!=null){
                    comment.value = `@${props.replyUser.name}：`
                }
            })
            const comment = ref()
            const replyInput = ref(false)

            const router = useRouter();
            const CheckLogin = async () => {
                if (!isLoggedIn) {
                    Swal.fire({
                        title: '錯誤,請先登入再回覆',
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
            const reply = () => {
                if(isLoggedIn){
                    if(replyInput.value!=true){
                        replyInput.value=true
                    }else{
                        replyInput.value=false
                    }
                }else{
                    CheckLogin()
                }
            }

            const outputReply = async () => {
                if(isLoggedIn){
                    console.log("送出Reply留言")
                    const body = {
                        "postId":postid.value,
                        "userId":member.userid,
                        "content":comment.value,
                        "parentId":`${props.parentId}`
                    }
                    console.log(body)
                    const response =await axiosapi.post(`/comment/create`,body);
                    if (response.data.successs) {
                        console.log(response.data)
                        replyInput.value=false
                    }else{
                        alert(response.data.message);
                    }
                    eventBus.emit('outputReply',props.parentId);
                    comment.value=''
                }else{
                    alert("請登入帳號");
                }
            }

            return{
                comment, replyInput,
                reply, outputReply,
            }
        }
    }
    
</script>
    
<style>
    
</style>