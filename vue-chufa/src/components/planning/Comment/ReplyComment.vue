<template>
    <div>
        <button v-show="haveReply" @click="openReply">{{lookReply}}{{ dataLength }}則回覆</button>
        <div v-show="lookReply=='▼'" class="Reply">
            <div v-for="comment in replyData">
                <div :id="`commentId:${comment.commentId}`">
                    <h3>{{comment.memberBean.name}}</h3>
                    <editComment :comment="comment"></editComment>
                    <div v-html="comment.content"></div>
                    <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
                    <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
                    <CommentFunction :parentId="comment.parentId" :replyUser="comment.memberBean" :replyCommentId="comment.commentId"></CommentFunction>
                </div>
            </div>
        </div>
    </div>
</template>
    
<script setup>
import editComment from './editComment.vue';
    import CommentFunction from '@/components/planning/Comment/CommentFunction.vue'
    import axiosapi from '@/plugins/axios.js';
    import { ref,onMounted } from 'vue';

    import eventBus from '@/eventBus';


    const props = defineProps(['parentId'])

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
        eventBus.on('outputReply', (newCommentParentId)=>{
            if(props.parentId==newCommentParentId){
                findReply()
            }
        });
        eventBus.on('deleteComment', (deleteCommentParentId)=>{
            if(props.parentId==deleteCommentParentId){
                findReply()
            }
        });
        eventBus.on('editComment', (editCommentParentId)=>{
            if(props.parentId==editCommentParentId){
                findReply()
            }
        });
    })

    const replyData = ref()
    const dataLength = ref()
    const findReply = async () => {
        await console.log("搜尋Reply留言")
        const response =await axiosapi.get(`/comment/findByParentId/${props.parentId}`);
        if (response.data.successs) {
            haveReply.value = response.data.successs
            dataLength.value = response.data.list.length
            replyData.value = response.data.list
        }else{
            haveReply.value = response.data.successs
            dataLength.value = response.data.list.length
            replyData.value = response.data.list
        }
    }
    
</script>
    
<style>
    .Reply{
        margin: 30px;
    }
</style>