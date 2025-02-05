<template>
    <div>
        <button v-show="haveReply" @click="openReply">{{lookReply}}{{ dataLength }}則回覆</button>
        <div v-show="lookReply=='▼'" class="Reply">
            <div v-for="comment in replyData">
                <h3>{{comment.memberBean.name}}</h3>
                <p>{{comment.content}}</p>
                <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
                <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
                <CommentFunction :parentId="comment.parentId" :replyUser="comment.memberBean"></CommentFunction>
            </div>
        </div>
    </div>
</template>
    
<script setup>
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
    })

    const replyData = ref()
    const dataLength = ref()
    const findReply = async () => {
        await console.log("搜尋Reply留言")
        const response =await axiosapi.get(`/comment/findByParentId/${props.parentId}`);
        if (response.data.successs) {
            haveReply.value = response.data.successs
            replyData.value = response.data.list
            dataLength.value = replyData.value.length
        }
    }
    
</script>
    
<style>
    .Reply{
        margin: 30px;
    }
</style>