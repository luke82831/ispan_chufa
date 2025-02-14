<template>
  <div>
    <div  class="memberComment">
      <img @click="inBlogprofile(comment)" v-if="comment.memberBean.profilePicture!=null" :src="'data:image/jpeg;base64,' + comment.memberBean.profilePicture" class="memberPicture">
      <img @click="inBlogprofile(comment)" v-else src="@/assets/empty.png" class="memberPicture">
      <h3>{{ comment.memberBean.name }}</h3>
    </div>

    <div v-html="htmlComment"></div>

    <p v-if="comment.commentUpdatedAt == null">
      創建留言時間:{{ comment.commentCreatedAt }}
    </p>
    <p v-else>更新留言時間:{{ comment.commentCreatedAt }}</p>
    <CommentFunction :parentId="comment.commentId"></CommentFunction>
  </div>
</template>

<script setup>
import CommentFunction from "@/components/Comment/CommentFunction.vue";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
const props = defineProps(["comment"]);
const router = useRouter();
const inBlogprofile = (comment) => {
        router.push(`/blog/blogprofile/${comment.memberBean.userid}`);
    }
const htmlComment = ref(props.comment.content);
</script>

<style scoped>
  .memberPicture{
        width: 40px;
        height: 40px;
        border-radius: 50%;
        border: 2px solid #ddd;
        margin: 5px;
        cursor: pointer;
    }
    .memberComment{
        display: flex; /* 啟用Flexbox佈局 */
        align-items: center;
        flex-grow: 1;
    }
</style>
