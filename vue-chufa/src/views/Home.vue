<template>
  <div class="main-container">
    <div
      v-for="post in posts"
      :key="post.postid"
      class="post-item"
      :class="{ repost: post.repost }"
      @click="navigateToDetail(post.postid)"
    >
      <!-- REPOST 版型處理 -->
      <div v-if="post.repost" class="repost-header">
        <div class="interaction-info">
          <div class="repost-profile-container">
            <!-- 顯示轉發者的頭像 (較小) -->
            <img
              v-if="post.member?.profilePicture"
              :src="'data:image/jpeg;base64,' + post.member.profilePicture"
              alt="Interaction Profile Picture"
              class="profile-picture small-profile"
            />
          </div>
          <p class="interaction-name">
            {{ post.member.nickname }} ({{ post.member.name }}) 轉發貼文
          </p>
        </div>
      </div>

      <div class="author-info" @click.stop>
        <div class="author-header">
          <div class="profile-picture-container">
            <router-link :to="`/blog/blogprofile/${post.member.userid}`">
              <!-- 顯示發文者的頭像 -->
              <!-- v-if="post.repostDTO?.member?.profilePicture"  -->
              <!-- post.repostDTO.member.profilePicture -->
              <img
                v-if="post.member.profilePicture"
                :src="'data:image/jpeg;base64,' + post.member.profilePicture"
                alt="Author's Profile Picture"
                class="profile-picture"
              />
              <div v-else class="default-profile"></div>
            </router-link>
          </div>
          <div class="author-name" @click.stop>
            <strong
              >{{
                post.repostDTO
                  ? post.repostDTO.member.nickname
                  : post.member.nickname
              }}
              ({{ post.repostDTO?.member?.name || post.member.name }})</strong
            >
          </div>
        </div>
        <h3>
          {{
            post.repostDTO
              ? post.repostDTO.postTitle
              : post.postTitle || "無標題"
          }}
        </h3>
      </div>

      <p class="post-content">{{ post.postContent }}</p>
      <a
        v-if="post.postLink"
        :href="post.postLink"
        target="_blank"
        class="read-more"
        >閱讀更多</a
      >
      <div class="post-meta">
        <p>
          發佈時間:
          {{
            formatDate(post.repost ? post.repostDTO.postTime : post.postTime)
          }}
        </p>
        <p v-if="post.repostDTO">互動時間: {{ formatDate(post.postTime) }}</p>
        <p>貼文類型: {{ post.repost ? "REPOST" : "原創" }}</p>
      </div>

      <!-- 轉發次數和點讚數放到右下角 -->
      <div class="post-stats">
        <p>轉發次數: {{ post.repostCount }}</p>
        <p>點讚數: {{ post.likeCount }}</p>
      </div>

      <!-- 按鈕區域 -->
      <div class="post-actions" @click.stop>
        <button
          @click="likePost(post.postid)"
          class="action-btn like-btn"
          @click.stop
        >
          👍 點讚
        </button>
        <button
          @click="repostPost(post.postid)"
          class="action-btn repost-btn"
          @click.stop
        >
          🔁 轉發
        </button>
        <button
          @click="collectPost(post.postid)"
          class="action-btn collect-btn"
          @click.stop
        >
          ❤️ 收藏
        </button>
      </div>
    </div>
    <!-- 分頁控制 -->
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
      <span>第 {{ currentPage }} 頁</span>
      <button @click="nextPage">下一頁</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";

export default {
  setup() {
    const router = useRouter();
    const profileLoaded = ref(false);
    const member = ref({});
    const posts = ref([]);
    const isAdmin = ref(false);
    const userId = ref(null);
    const currentPage = ref(1); // 當前頁數

    const navigateToDetail = (postid) => {
      router.push(`/blog/find/${postid}`);
    };

    const formatDate = (date) => {
      if (!date) return "";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    const fetchProfile = async () => {
      try {
        const response = await axios.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (response.data.success) {
          member.value = response.data.user || {};
          isAdmin.value = response.data.user.role === "ADMIN";
          userId.value = member.value.userid;
        } else {
          Swal.fire("味登入", "登入體驗更好");
        }
        profileLoaded.value = true;
      } catch (error) {
        console.error("Fetch profile failed:", error);
        Swal.fire("錯誤", "無法取得會員資料", "error");
      }
    };

    const fetchPosts = async () => {
      try {
        const response = await axios.post(
          "/api/posts/post",
          {
            sortByLikes: true,
            repost: true,
            page: currentPage.value, // 添加分頁參數
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.postdto && response.data.postdto.length > 0) {
          posts.value = response.data.postdto.filter(
            (post) => !post.repost && post.repostDTO === null
          );
        } else {
          currentPage.value = Math.max(1, currentPage.value - 1); // 返回有效的上一頁
          Swal.fire("已經到底啦!", "no post。", "info");
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", "無法取得貼文", "error");
      }
    };

    //分頁
    const nextPage = () => {
      currentPage.value++;
      fetchPosts();
    };

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
        fetchPosts();
      }
    };

    const repostPost = async (postid) => {
      try {
        const data = {
          postid: postid,
          userid: member.value.userid,
        };

        const response = await axios.post("/api/posts/repost/forward", data, {
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (response.data.repost) {
          Swal.fire("成功", "轉發成功！", "success");
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "轉發失敗！", "error");
        }
      } catch (error) {
        console.error("轉發請求失敗:", error);
        Swal.fire("錯誤", "無法執行轉發操作", "error");
      }
    };

    const likePost = async (postid) => {
      try {
        const data = {
          postid: postid,
          userid: member.value.userid,
          interactionType: "LIKE",
        };

        const response = await axios.post(
          "/api/posts/insertinteraction",
          data,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.success) {
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error");
      }
    };

    const collectPost = async (postid) => {
      try {
        const data = {
          postid: postid,
          userid: member.value.userid,
          interactionType: "COLLECT",
        };

        const response = await axios.post(
          "/api/posts/insertinteraction",
          data,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.success) {
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("錯誤", "無法執行點讚操作", "error");
      }
    };

    onMounted(async () => {
      await fetchPosts();
      await fetchProfile();
    });

    return {
      profileLoaded,
      member,
      isAdmin,
      formatDate,
      likePost,
      collectPost,
      repostPost,
      posts,
      navigateToDetail,
      currentPage,
      nextPage,
      prevPage,
    };
  },
};
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 800px;
  padding: 20px;
  margin: 0 auto;
}

.post-item {
  background: linear-gradient(145deg, #ffffff, #f9f9f9);
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  width: 100%;
  box-sizing: border-box;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.post-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.post-item h3 {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.post-content {
  font-size: 16px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
}

.read-more {
  color: #007bff;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.read-more:hover {
  color: #0056b3;
}

.post-meta {
  font-size: 14px;
  color: #777;
  margin-bottom: 15px;
}

.post-stats {
  position: absolute;
  bottom: 15px;
  right: 15px;
  font-size: 12px;
  color: #888;
}

.post-stats p {
  margin: 0;
  line-height: 1.2;
}

.post-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.action-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.action-btn:hover {
  transform: scale(1.05);
}

.like-btn {
  background-color: #28a745;
  color: white;
}

.repost-btn {
  background-color: #17a2b8;
  color: white;
}

.collect-btn {
  background-color: #dc3545;
  color: white;
}

.profile-picture-container {
  display: inline-block;
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #ddd;
  background-color: #f0f0f0;
  margin-top: 10px;
}

.profile-picture {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-profile {
  width: 100%;
  height: 100%;
  background-color: #ddd;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  color: #fff;
  font-weight: bold;
}

.repost-profile-container {
  display: inline-block;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  overflow: hidden;
}

.repost-profile-container img.small-profile {
  width: 30px;
  height: 30px;
  object-fit: cover;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
}

.pagination button {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin: 0 8px;
  transition: background 0.3s;
}

.pagination button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background: #0056b3;
}

.pagination span {
  font-size: 1.1rem;
  color: #333;
}
</style>
