<template>
  <div v-if="isMe && isLoggedIn">
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
      class="openEditComment"
    >
      <path d="M12 20h9" />
      <path d="M16.5 3.5a2.12 2.12 0 1 1 3 3L7 19l-4 1 1-4Z" />
    </svg>

    <div v-if="isClick" class="editBox">
      <button @click="editComment" class="editButton edit">編輯留言</button>
      <div v-if="isEdit">
        <input type="text" v-model="content" class="editInput"> 
        <button @click="outputEdit" class="editOkButton">確認編輯</button>
      </div>
      <button v-else @click="removeComment" class="editButton remove">刪除留言</button>
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
    alert("編輯留言成功");
  } else {
    alert("編輯留言失敗");
  }
  content.value = "";
  anchorString.value = "";
};
</script>

<style scoped>
.openEditComment:hover{
        cursor: pointer;
        color: rgb(219, 35, 3);
    }
    .editBox{
        position: absolute;
        background: linear-gradient(145deg, #ffffff, #f8f8f8);
        border: 1px solid #ddd;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
        
        animation: fadeIn 0.3s ease;

        display: flex;
        flex-direction: column;
    }
    .editButton{
        color: #4882c0;
        padding: 10px;
        width: 140px;
        text-align: left;
        font-size: 16px;
        background-color: #ffffff;
        border: none;
    }
    .edit{
        margin-top: 10px;
        margin-bottom: 2px;
    }
    .remove{
        color: red;
        margin-top: 2px;
        margin-bottom: 10px;
    }
    .editButton:hover{
        background-color: #d4f3f3
    }
    .editOkButton{
      padding: 10px 20px;
        border: none;
        border-radius: 25px;
        cursor: pointer;
        background-color: #7e8ee4;
        color: white;
    }
    .editInput{
      font-size: 16px;
    }
</style>
