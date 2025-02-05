<template>
    <div>
        <h3>{{comment.memberBean.name}}</h3>

        <div v-html="htmlComment"></div>

        <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
        <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
        <CommentFunction :parentId="comment.commentId"></CommentFunction>
    </div>
</template>
    
<script setup>
    import CommentFunction from '@/components/planning/Comment/CommentFunction.vue'
    import { ref } from 'vue'
    
    const props = defineProps(['comment'])

    const htmlComment = ref(props.comment.content)
</script>
    
<style>

</style>