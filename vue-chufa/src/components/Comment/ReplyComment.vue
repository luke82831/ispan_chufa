<template>
  <div>
    <button v-show="haveReply" @click="openReply">
      {{ lookReply }}{{ dataLength }}則回覆
    </button>
    <div v-if="lookReply == '▼'" class="Reply">
      <div v-for="comment in replyData">
        <div :id="`commentId:${comment.commentId}`">
          <div class="memberComment">
            <img
              @click="inBlogprofile(comment)"
              v-if="comment.memberBean.profilePicture != null"
              :src="'data:image/jpeg;base64,' + comment.memberBean.profilePicture"
              class="memberPicture"
            />
            <img
              @click="inBlogprofile(comment)"
              v-else
              src="@/assets/empty.png"
              class="memberPicture"
            />
            <h3>{{ comment.memberBean.name }}</h3>
          </div>
          <div v-html="comment.content"></div>
          <p v-if="comment.commentUpdatedAt == null">
            創建留言時間:{{ comment.commentCreatedAt }}
          </p>
          <p v-else>更新留言時間:{{ comment.commentCreatedAt }}</p>
          <CommentFunction
            :parentId="comment.parentId"
            :replyUser="comment.memberBean"
            :replyCommentId="comment.commentId"
          ></CommentFunction>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import CommentFunction from "@/components/Comment/CommentFunction.vue";
import axiosapi from "@/plugins/axios.js";
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import eventBus from "@/eventBus";
const router = useRouter();
const props = defineProps(["parentId"]);
const inBlogprofile = (comment) => {
  router.push(`/blog/blogprofile/${comment.memberBean.userid}`);
};
const lookReply = ref("▲");
const openReply = () => {
  if (lookReply.value == "▲") {
    lookReply.value = "▼";
  } else {
    lookReply.value = "▲";
  }
};

const haveReply = ref(false);

onMounted(() => {
  findReply();
  eventBus.on("outputReply", (newCommentParentId) => {
    if (props.parentId == newCommentParentId) {
      findReply();
    }
  });
});

const replyData = ref();
const dataLength = ref();
const findReply = async () => {
  await console.log("搜尋Reply留言");
  const response = await axiosapi.get(`/comment/findByParentId/${props.parentId}`);
  if (response.data.successs) {
    haveReply.value = response.data.successs;
    replyData.value = response.data.list;
    dataLength.value = replyData.value.length;
  }
};
</script>

<style scoped>
.Reply {
  margin: 30px;
}
.memberPicture {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #ddd;
  margin: 5px;
  cursor: pointer;
}
.memberComment {
  display: flex; /* 啟用Flexbox佈局 */
  align-items: center;
  flex-grow: 1;
}
</style>
