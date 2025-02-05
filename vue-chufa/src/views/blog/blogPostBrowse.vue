<template>
  <div>
    <p>{{ member }}</p>
    <!-- Post創建者 -->
    <p class="title">{{ postData.postTitle }}</p>
    <!-- 標題 -->
    <p>===========</p>
    <div v-html="htmlContent"></div>
    <!-- 內容 -->
  </div>
  <InputComment></InputComment>
  <Comment></Comment>
</template>

<script setup>
import InputComment from "@/components/Comment/InputComment.vue";
import Comment from "@/components/Comment/Comment.vue";

import axiosapi from "@/plugins/axios.js";
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";

const router = useRouter();
const route = useRoute();
const postid = ref(route.params.postid); // 取得動態路由參數

const member = ref("");
const postData = ref("");
const htmlContent = ref("");

const Check = async () => {
  Swal.fire({
    title: "錯誤",
    text: "搜尋不到這篇文章",
    icon: "error", // success, error, warning, info, question
    confirmButtonText: "是的",
    confirmButtonColor: "blue",
  }).then((result) => {
    if (result.isConfirmed) {
      router.push("/");
    }
  });
};

const findPost = async () => {
  await console.log("搜尋Post文章");
  const response = await axiosapi.get(`/post/findById/${postid.value}`);
  if (response.data.successs) {
    postData.value = response.data.list[0];
    member.value = postData.value.member.name;
    htmlContent.value = response.data.list[0].postContent;
  } else {
    Check();
  }
};
onMounted(() => {
  findPost();
});
</script>

<style></style>
