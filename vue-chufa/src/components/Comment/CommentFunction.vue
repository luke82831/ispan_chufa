<template>
  <button class="replyButton" @click="reply">回覆</button>
  <div v-if="replyInput" class="outputReply">
    <input class="outputReplyInput" type="text" placeholder="請輸入留言" v-model="comment" />
    <button class="outputReplyButton" @click="outputReply">送出</button>
  </div>
</template>

<script setup>
import axiosapi from "@/plugins/axios.js";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";

import DOMPurify from "dompurify"; // 用來過濾使用者輸入的資料

import eventBus from "@/eventBus";

import { useUserStore } from "@/stores/user";
const user = useUserStore();
const { member, isLoggedIn } = user;

const props = defineProps(["parentId", "replyUser", "replyCommentId"]);
const route = useRoute();
const postid = ref(route.params.postid); // 取得動態路由參數

const comment = ref();
const replyInput = ref(false);

const router = useRouter();
const CheckLogin = async () => {
  if (!isLoggedIn) {
    Swal.fire({
      title: "錯誤,請先登入再回覆",
      text: "是否跳到登入畫面",
      icon: "error", // success, error, warning, info, question
      confirmButtonText: "是的",
      confirmButtonColor: "blue",
      cancelButtonText: "取消",
      cancelButtonColor: "red",
      showCancelButton: true,
    }).then((result) => {
      if (result.isConfirmed) {
        router.push("/secure/Login");
      }
    });
    return;
  }
};
const reply = () => {
  if (isLoggedIn) {
    if (replyInput.value != true) {
      replyInput.value = true;
    } else {
      replyInput.value = false;
    }
  } else {
    CheckLogin();
  }
};



const outputReply = async () => {
  if (isLoggedIn) {
    console.log("送出Reply留言");
    const replyUser = ref();
    const safeHTML = ref(DOMPurify.sanitize(comment.value)); // 过滤 HTML

    if (props.replyUser != null) {
      // 如果是從回復來回覆的話 前面會加超連接
      replyUser.value = `<a href="#commentId:${props.replyCommentId}">@${props.replyUser.name}:</a>`;
      safeHTML.value = replyUser.value + safeHTML.value;
    }

    const body = {
      postId: postid.value,
      userId: member.userid,
      content: `${safeHTML.value}`,
      parentId: `${props.parentId}`,
    };
    console.log(body);
    const response = await axiosapi.post(`/comment/create`, body);
    if (response.data.successs) {
      console.log(response.data);
      replyInput.value = false;
      alert(response.data.message);
    } else {
      alert(response.data.message);
    }
    eventBus.emit("outputReply", props.parentId);
    comment.value = "";
  } else {
    alert("請登入帳號");
  }
};
</script>

<style scoped>
.replyButton{
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        font-size: 14px;
        cursor: pointer;
        background-color: #17a2b8;
        color: white;
        margin-bottom: 10px; 
        transition: background-color 0.3s ease, transform 0.2s ease;
    }
    .replyButton:hover {
        transform: scale(1.05);
    }
    .outputReply{
      display: flex;
        padding: 10px 20px;
        border: none;
        border-radius: 25px;
        cursor: pointer;
        background-color: #c1c8f0;
        color: white;
        margin-bottom: 10px; 
    }
    .outputReplyInput{
      flex-grow: 1;
      font-size: 16px;
      border: none;
    }
    .outputReplyButton{
      padding: 10px 20px;
        border: none;
        border-radius: 25px;
        cursor: pointer;
        background-color: #7e8ee4;
        color: white;
    }
</style>
