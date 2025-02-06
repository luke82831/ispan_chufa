<template>
    <div>
        <div v-for="comment in commentData" class="comment">
            <RootComment :comment="comment"></RootComment>
            <ReplyComment :parentId="comment.commentId"></ReplyComment>
        </div>
    </div>
</template>
    
<script setup>
    import ReplyComment from '@/components/planning/Comment/ReplyComment.vue'
    import RootComment from '@/components/planning/Comment/RootComment.vue'
    import axiosapi from '@/plugins/axios.js';
    import { ref, onMounted } from 'vue';
    import { useRoute } from 'vue-router';
    import eventBus from '@/eventBus';
    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數

    onMounted(()=>{
        lookComment()
        eventBus.on('outputComment', ()=>{
            console.log('outputComment')
            lookComment()
        });
        eventBus.on('deleteComment', (deleteCommentParentId)=>{
            if(deleteCommentParentId == null){
                console.log('deleteComment')
                lookComment()
            }
        });
        eventBus.on('editComment', (editCommentParentId)=>{
            if(editCommentParentId == null){
                console.log('editComment')
                lookComment()
            }
        });
    })

    const commentData = ref()
    const lookComment = async () => {
        commentData.value = []
        console.log("搜尋Root留言")
        const response =await axiosapi.get(`/comment/findByPostId/${postid.value}`);
        console.log(response)
        if (response.data.successs) {
            commentData.value = response.data.list
        }
    }
</script>
    
<style>
    .comment{
        margin: 20px;
    }
</style>