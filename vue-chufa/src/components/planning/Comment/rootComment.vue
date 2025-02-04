<template>
    <div>
        <div v-for="comment in commentData">
            <h3>{{comment.memberBean.name}}</h3>

            <p>{{comment.content}}</p>

            <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
            <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
            <CommentFunction :parentId="comment.commentId"></CommentFunction>
            <h3>======================</h3>
        </div>
    </div>
</template>
    
<script setup>
    import CommentFunction from '@/components/planning/Comment/CommentFunction.vue'
    import axiosapi from '@/plugins/axios.js';
    import { ref, onMounted } from 'vue';
    import { useRoute } from 'vue-router';
    import eventBus from '@/eventBus';
    const route = useRoute();
    const postid = ref(route.params.postid); // 取得動態路由參數
    onMounted(()=>{
        lookComment()
        eventBus.on('outputComment', (aaa)=>{
            console.log(aaa)});
            lookComment()
    })

    const commentData = ref()
    const lookComment = async () => {
        const response =await axiosapi.get(`/comment/findByPostId/${postid.value}`);
        if (response.data.successs) {
            commentData.value = response.data.list
        }else{
            alert(response.data.message);
        }
    }
</script>
    
<style>

</style>