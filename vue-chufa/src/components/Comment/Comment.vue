<template>
  <div>
    <div v-for="comment in commentData" class="comment">
      <RootComment :comment="comment"></RootComment>
      <ReplyComment :parentId="comment.commentId"></ReplyComment>
    </div>
  </div>
</template>

<script setup>
import ReplyComment from "@/components/Comment/ReplyComment.vue";
import RootComment from "@/components/Comment/rootComment.vue";
import axiosapi from "@/plugins/axios.js";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import eventBus from "@/eventBus";
const route = useRoute();
const postid = ref(route.params.postid); // 取得動態路由參數
onMounted(() => {
    lookComment();
    eventBus.on("outputComment", () => {
        lookComment();
    });
});

const commentData = ref();
const lookComment = async () => {
    console.log("搜尋Root留言");
    const response = await axiosapi.get(`/comment/findByPostId/${postid.value}`);
    if (response.data.successs) {
        commentData.value = response.data.list;
    }
};
</script>

<style>
    .comment{
        border: 5px solid black;
        margin: 20px;
    }
</style>
