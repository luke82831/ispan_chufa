<template>
    <div>
        <h3>{{comment.memberBean.name}}</h3>

        <p>{{comment.content}}</p>

        <p v-if="comment.commentUpdatedAt==null">創建留言時間:{{comment.commentCreatedAt}}</p>
        <p v-else>更新留言時間:{{comment.commentCreatedAt}}</p>
        <CommentFunction :parentId="comment.commentId"></CommentFunction>
    </div>
</template>
    
<script setup>
    import CommentFunction from '@/components/planning/Comment/CommentFunction.vue'
    
    const props = defineProps(['comment'])

</script>
    
<style>

</style>