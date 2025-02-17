<template>
  <div v-show="isMe && isLoggedIn">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      @click="edit"
    >
      <path d="M12 20h9" />
      <path d="M16.5 3.5a2.12 2.12 0 1 1 3 3L7 19l-4 1 1-4Z" />
    </svg>

    <div v-show="isClick">
      <button @click="editComment">編輯留言</button>
      <button @click="removeComment">刪除留言</button>
    </div>
    <div v-show="isEdit">
      <input type="text" v-model="content" />
      <button @click="outputEdit">確認編輯</button>
    </div>
  </div>
</template>

<script setup>
import axiosapi from "@/plugins/axios.js";
import { ref } from "vue";
import eventBus from "@/eventBus";

import { useUserStore } from "@/stores/user";
const user = useUserStore();
const { member, isLoggedIn } = user;

const props = defineProps(["comment"]);
const isMe = ref(member.userid == props.comment.memberBean.userid);
const isClick = ref(false);

const edit = () => {
  if (isClick.value == false) {
    isClick.value = true;
  } else {
    isClick.value = false;
    isEdit.value = false;
  }
};

const removeComment = async () => {
  console.log("刪除留言");
  const body = { commentId: props.comment.commentId };
  const response = await axiosapi.post(`/comment/delete`, body);
  alert(response.data.message);
  eventBus.emit("deleteComment", props.comment.parentId);
};

const isEdit = ref(false);
const anchorString = ref();
const content = ref();
const editComment = async () => {
  if (isEdit.value == false) {
    isEdit.value = true;

    // DOMParser解析原本留言的內容
    const parser = new DOMParser();
    const doc = parser.parseFromString(props.comment.content, "text/html");

    // 確認是否存在 <a> 標籤
    const anchorTag = doc.querySelector("a");
    if (anchorTag != null) {
      // 將 HTML文檔 轉字串
      anchorString.value = anchorTag.outerHTML;
      // 將 <a> 標籤排除
      doc.querySelectorAll("a").forEach((tag) => tag.remove());
    }

    // 將<a>以外的留言標籤取出
    content.value = doc.body.innerHTML;
  } else {
    isEdit.value = false;
  }
};

const outputEdit = async () => {
  console.log("編輯留言");
  if (anchorString.value != undefined) {
    content.value = anchorString.value + content.value;
  }
  anchorString.value;
  const body = {
    commentId: props.comment.commentId,
    content: content.value,
  };
  const response = await axiosapi.put(`/comment/update`, body);
  if (response.data.successs) {
    console.log("編輯留言成功");
    isEdit.value = false;
    console.log(response.data.list[0].parentId);
    eventBus.emit("editComment", response.data.list[0].parentId);
  } else {
    alert("編輯留言失敗");
  }
  content.value = "";
  anchorString.value = "";
};
</script>

<style scoped></style>
