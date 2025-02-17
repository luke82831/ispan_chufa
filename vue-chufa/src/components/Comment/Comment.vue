<template>
  <div v-if="commentData != null" class="getCommentBody">
    <div v-for="comment in commentData">
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

<style scoped>
.getCommentBody {
  padding: 10px;
  margin: 0px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}
</style>
