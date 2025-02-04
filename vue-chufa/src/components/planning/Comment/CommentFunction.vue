<template>
    <div>
        <button @click="reply">回覆</button>
        <button>分享</button>
    </div>
    <input v-show="replyInput" type="text" placeholder="請輸入留言" v-model="comment">
    <button v-show="replyInput" @click="outputReply">送出</button>

    <div>
        <button v-show="haveReply" @click="openReply">{{lookReply}}{{ dataLength }}則回覆</button>
        <div v-if="lookReply=='▼'" class="Reply">
            <div v-for="comment in replyData">
                <h3>{{comment.memberBean.name}}</h3>
                <p>{{comment.content}}</p>
                <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
                <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
            </div>
        </div>
    </div>
</template>
    
<script>
    import axiosapi from '@/plugins/axios.js';
    import { onMounted, ref, watch } from 'vue';
    import { useRoute } from 'vue-router';

    export default {
        props:[
            'parentId',
        ],
        setup(props){
            const route = useRoute();
            const postid = ref(route.params.postid); // 取得動態路由參數

            const comment = ref()
            const replyInput = ref(false)

            const reply = () => {
                if(replyInput.value!=true){
                    replyInput.value=true
                }else{
                    replyInput.value=false
                }
            }

            const outputReply = async () => {
                const body = {
                    "postId":postid.value,
                    "userId":"1",
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
                comment.value=''
                findReply()
            }

            const lookReply = ref("▲")
            const openReply = () => {
                if(lookReply.value == "▲"){
                    lookReply.value = "▼"
                }else{
                    lookReply.value = "▲"
                }
            }

            const haveReply = ref(false)

            onMounted(()=>{
                findReply()
            })

            const replyData = ref()
            const dataLength = ref()
            const findReply = async () => {
                const response =await axiosapi.get(`/comment/findByParentId/${props.parentId}`);
                if (response.data.successs) {
                    haveReply.value = true
                    replyData.value = response.data.list
                    dataLength.value = replyData.value.length
                }else{
                    
                }
            }

            return{
                comment, replyInput,
                reply, outputReply,
                lookReply,openReply,haveReply,
                replyData,dataLength,findReply
            }
        }
    }
    
</script>
    
<style>
    .Reply{
        margin: 30px;
    }
</style>