<template>
  <div class="main-container">
    <div class="content">
      <div>
        <Carousel />
      </div>
      <div>
        <div class="tabs-container">
          <button
            class="tab"
            :class="{ active: selectedPlace === null }"
            @click="switchPlace(null)"
          >
            首頁
          </button>
          <button
            class="tab"
            :class="{ active: selectedPlace === 'follow' }"
            @click="switchPlace('follow')"
            v-if="userStore.isLoggedIn"
          >
            關注
          </button>
          <button
            v-for="place in places"
            :key="place.id"
            class="tab"
            :class="{ active: selectedPlace === place.name }"
            @click="switchPlace(place.name)"
          >
            {{ place.name }}
          </button>

          <div class="sort-select-container">
            <select
              id="sortSelect"
              v-model="sortBy"
              @change="fetchPosts"
              class="border p-2 rounded"
            >
              <option value="likes">熱度排序</option>
              <option value="time">時間排序</option>
            </select>
          </div>
        </div>
      </div>
      <!-- 貼文網格布局 -->
      <div class="posts-grid" v-if="posts.length > 0">
        <div
          v-for="post in visiblePosts"
          :key="post.postid"
          class="post-card"
          @click="navigateToDetail(post, $event)"
        >
          <!-- REPOST 版型處理 -->
          <div v-if="post.repost" class="repost-header">
            <div class="interaction-info">
              <div class="repost-profile-container">
                <img
                  v-if="post.member?.profilePicture"
                  :src="'data:image/jpeg;base64,' + post.member.profilePicture"
                  alt="Interaction Profile Picture"
                  class="profile-picture small-profile"
                />
                <div v-else>
                  <img
                    :src="defaultProfilePic"
                    alt="Default Profile Picture"
                    class="profile-picture small-profile"
                  />
                </div>
              </div>
              <p class="interaction-name">
                {{
                  post.member.nickname ? post.member.nickname : post.member.name
                }}
                轉發貼文
              </p>
            </div>
          </div>

          <!-- 作者信息 -->
          <div class="author-info">
            <div class="author-header">
              <div class="profile-picture-container">
                <router-link
                  :to="`/blog/blogprofile/${post.member.userid}`"
                  @click.stop
                >
                  <img
                    v-if="
                      post.repostDTO
                        ? post.repostDTO.member?.profilePicture
                        : post.member?.profilePicture
                    "
                    :src="
                      'data:image/jpeg;base64,' +
                      (post.repostDTO?.member?.profilePicture ??
                        post.member.profilePicture)
                    "
                    alt="Author's Profile Picture"
                    class="profile-picture"
                  />
                  <img
                    v-else
                    :src="defaultProfilePic"
                    alt="Default Profile Picture"
                    class="profile-picture"
                  />
                </router-link>
              </div>
              <div class="author-name">
                <strong
                  v-if="
                    post.repostDTO
                      ? post.repostDTO.member.nickname
                      : post.member.nickname
                  "
                >
                  {{
                    post.repostDTO
                      ? post.repostDTO.member.nickname
                      : post.member.nickname
                  }}
                </strong>
                <strong v-else>
                  {{ post.repostDTO?.member?.name || post.member.name }}
                </strong>
                <p class="post-time">{{ formatDate(post.postTime) }}</p>
              </div>
            </div>
          </div>

          <!-- 顯示第一張圖片 -->
          <div
            v-if="
              getFirstImage(
                post.repostDTO ? post.repostDTO.postContent : post.postContent
              )
            "
            class="post-image-container"
          >
            <img
              :src="
                getFirstImage(
                  post.repostDTO ? post.repostDTO.postContent : post.postContent
                )
              "
              class="post-image"
            />
          </div>
          <div v-else class="post-image-container">
            <img :src="defaultpicture" class="post-image" />
          </div>
          <div class="post-content-preview">
            <p>
              {{
                post.repostDTO
                  ? post.repostDTO.postTitle
                  : post.postTitle || "無標題"
              }}
            </p>
            {{
              getTextPreview(
                post.repostDTO
                  ? post.repostDTO.postContent
                  : post.postContent || "無標題",
                30
              )
            }}
          </div>
          <!-- 互動按鈕 -->
          <div class="post-actions" @click.stop>
            <button
              @click.stop="likePost(post.postid)"
              class="action-btn like-btn"
              :class="{ active: post.likedByCurrentUser }"
            >
              <span class="heart-icon"></span>
              <!-- {{ post.likedByCurrentUser ? '已點讚' : '點讚' }} -->
              {{ post.likeCount }}
            </button>
            <button
              @click.stop="repostPost(post.postid)"
              class="action-btn repost-btn"
            >
              🔁 {{ post.repostCount }}
            </button>
            <button
              @click.stop="collectPost(post.postid)"
              class="action-btn collect-btn"
              :class="{ active: post.collectByCurrentUser }"
            >
              {{ post.collectByCurrentUser ? "已收藏" : "收藏" }}
            </button>
          </div>
        </div>
      </div>
      <div v-else>
        <p>沒有文章喔~</p>
      </div>

      <!-- 分頁控制 -->
      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1">上一頁</button>
        <span>第 {{ currentPage }} 頁</span>
        <button @click="nextPage">下一頁</button>
      </div>

      <div v-if="userStore.isLoggedIn">
        <!-- 發文按鈕 -->
        <div
          id="blogbutton"
          @mouseenter="hoverBlog = true"
          @mouseleave="hoverBlog = false"
        >
          <RouterLink to="/blog/create" class="button">
            <transition name="fade" mode="out-in">
              <div v-if="hoverBlog" key="expanded" class="expanded-content">
                <img
                  class="icon-large"
                  src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAEj0lEQVR4nO2beYhWVRTAjzaWWws2maAwRaGVlJW2oAhRomG2UBRJpkWS0YKk/VHQHxMF9kdaIoV/qIEGmQ7avlBi0bS5RQmltlDYqubapo3zi9OcNxyf773vjs18ft/73u+vmXvPfe+dc5dz7rn3EykoKCgoKCg7wMPAb8BWYKrUEsC1HMpB4EKpFYAPOJxpUgsAF5HMpVILABcAs4Fm4C9TfoPUIkAPGxHDJO8A5wEvAw8C/aRWAPoATwL/uPn+BzAfOEfyDm09nkYrMF5yPse3ks0cySvALSWUz7cHANYGGGAf0F3yBjCaMJ6RPAKsCFBeF8GzJW8ApwEtAQZ4VfII8FTg8L9C8gZwPLA7QPmNQDfJG8CMwN6/XfIGcAzwTYDyvwI9JW8ANwT2fqNrcwnwAnCx5DTTE+dvYIBrs8rVrQMm60iSagMYQRgLXZsxKTI6jabrTlKqBeD5QAO0Jz+Aj0rI7gHmAoOkkgEGAgcClH/btbmOcPYDyzR7JJUAcCUw2P3/eKAiV5l8d+DzDhjgECPa+8sfQwBDrCfUjdVZWW9gR8CHb452fcAk/j9bbJ3oVQ7F+1kv61BU5rq6ewI/+C6XJPmazkM7oxGo7wrFewB3AttjL/3PZ+swBL4M+Mid0YquhqBrUPe6uNPyjMA4G2ZxtjiZCYEfN8vkewE/0LUctOzz+COOJ4A64OeAKM4HMWmodxho8jMpLz8Bi4Apdv5QDxwXYoAJGQ8dYjLnWkKjFM+ZfF+brxWDZBhgeUqbNU7m2cD3jDD5qcArwGPAjWbA22wfoGtE2ZEU5U+2BSWJ6SYzIENG+dGG3rQO7CLVt5cVSfmYe1PkW6JNDPBIxnPfBU6JPVO9xWBgIvAEsNqSJjol1gBNwFIqxABrU+TfsvqeGXP5aXWdJne1U1Zj+opDEpQfmiE/2c3lOHrud58bzrqBqXgkwQDaY0noOf6JJvNZrE6DpMus7gTN9FIlSAd8/1KTGRsr/xRosLozgC+oIqQDvv8ak3ndlS2LNiLA5Xbji2o2QFOKnO70jgXOcoFPcxRmWmwfkguoOCTQ9883mTtc2dgSLrMqkADfr4x2Fxsj+lqZBjy5MMDHKTLfRpkXPcVNMIBuNnJhgN9TZCY5mZWufJyV3Z/xfHWdS4CRiRFXW/v+FigdFcR9yDsJ9St83i02Sj50Ed/1Fv7qYrnX/p4RmqEx9zs7cGfZqYj7iAZLNu63ed0YKehkvo+1byqVt9fbn8ACuwVyd2y90ejxATfFNMb47qgYoBS2mYnygfEDjChCbL/mApyfkPV93+rqY896CTjV5QweBf6kDEgourtLaK9D9la3PdYF8w0nfyBBviFhPYnC6eHufYPsbqFOqYowQF3Cud9D7j7Aeivb5dq8mPDOm1Kuy7d6A7hnnGRTpvkI1ohfgHl6QzVY0Syckl8BN7us8ZvupZ9knPy0Rr8JsCk1z3p4czSSAk6fJtrIeA/YZCOnxZTdaNNJL2aO6vQDVtp8/jbgzNhdoA2myGvA6a4urmS7S61KaFuhq/8Mv6CgoKCgQCL+BVWzEQZxQJDIAAAAAElFTkSuQmCC"
                  alt="發文圖示"
                />
                <span class="button-text">發佈文章</span>
              </div>
              <img
                v-else
                key="small"
                class="icon-small"
                src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAEj0lEQVR4nO2beYhWVRTAjzaWWws2maAwRaGVlJW2oAhRomG2UBRJpkWS0YKk/VHQHxMF9kdaIoV/qIEGmQ7avlBi0bS5RQmltlDYqubapo3zi9OcNxyf773vjs18ft/73u+vmXvPfe+dc5dz7rn3EykoKCgoKCg7wMPAb8BWYKrUEsC1HMpB4EKpFYAPOJxpUgsAF5HMpVILABcAs4Fm4C9TfoPUIkAPGxHDJO8A5wEvAw8C/aRWAPoATwL/uPn+BzAfOEfyDm09nkYrMF5yPse3ks0cySvALSWUz7cHANYGGGAf0F3yBjCaMJ6RPAKsCFBeF8GzJW8ApwEtAQZ4VfII8FTg8L9C8gZwPLA7QPmNQDfJG8CMwN6/XfIGcAzwTYDyvwI9JW8ANwT2fqNrcwnwAnCx5DTTE+dvYIBrs8rVrQMm60iSagMYQRgLXZsxKTI6jabrTlKqBeD5QAO0Jz+Aj0rI7gHmAoOkkgEGAgcClH/btbmOcPYDyzR7JJUAcCUw2P3/eKAiV5l8d+DzDhjgECPa+8sfQwBDrCfUjdVZWW9gR8CHb452fcAk/j9bbJ3oVQ7F+1kv61BU5rq6ewI/+C6XJPmazkM7oxGo7wrFewB3AttjL/3PZ+swBL4M+Mid0YquhqBrUPe6uNPyjMA4G2ZxtjiZCYEfN8vkewE/0LUctOzz+COOJ4A64OeAKM4HMWmodxho8jMpLz8Bi4Apdv5QDxwXYoAJGQ8dYjLnWkKjFM+ZfF+brxWDZBhgeUqbNU7m2cD3jDD5qcArwGPAjWbA22wfoGtE2ZEU5U+2BSWJ6SYzIENG+dGG3rQO7CLVt5cVSfmYe1PkW6JNDPBIxnPfBU6JPVO9xWBgIvAEsNqSJjol1gBNwFIqxABrU+TfsvqeGXP5aXWdJne1U1Zj+opDEpQfmiE/2c3lOHrud58bzrqBqXgkwQDaY0noOf6JJvNZrE6DpMus7gTN9FIlSAd8/1KTGRsr/xRosLozgC+oIqQDvv8ak3ndlS2LNiLA5Xbji2o2QFOKnO70jgXOcoFPcxRmWmwfkguoOCTQ9883mTtc2dgSLrMqkADfr4x2Fxsj+lqZBjy5MMDHKTLfRpkXPcVNMIBuNnJhgN9TZCY5mZWufJyV3Z/xfHWdS4CRiRFXW/v+FigdFcR9yDsJ9St83i02Sj50Ed/1Fv7qYrnX/p4RmqEx9zs7cGfZqYj7iAZLNu63ed0YKehkvo+1byqVt9fbn8ACuwVyd2y90ejxATfFNMb47qgYoBS2mYnygfEDjChCbL/mApyfkPV93+rqY896CTjV5QweBf6kDEgourtLaK9D9la3PdYF8w0nfyBBviFhPYnC6eHufYPsbqFOqYowQF3Cud9D7j7Aeivb5dq8mPDOm1Kuy7d6A7hnnGRTpvkI1ohfgHl6QzVY0Syckl8BN7us8ZvupZ9knPy0Rr8JsCk1z3p4czSSAk6fJtrIeA/YZCOnxZTdaNNJL2aO6vQDVtp8/jbgzNhdoA2myGvA6a4urmS7S61KaFuhq/8Mv6CgoKCgQCL+BVWzEQZxQJDIAAAAAElFTkSuQmCC"
                alt="發文圖示"
              />
            </transition>
          </RouterLink>
        </div>

        <!-- 開始規劃按鈕 -->
        <div
          id="planningbutton"
          @mouseenter="hoverPlanning = true"
          @mouseleave="hoverPlanning = false"
        >
          <RouterLink to="/myitineraries" class="button">
            <transition name="fade" mode="out-in">
              <div v-if="hoverPlanning" key="expanded" class="expanded-content">
                <img
                  class="icon-large"
                  src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAF/klEQVR4nO2beYhVVRjArzOaLWppiylWDmW0UBiUlm0aOhlli6kQzGSCEUlmSDaVRLaRFWUrIdaUS1QWFpTSQkRQWZoVYYshOhHtm2U2Tqa/+HrnDd98c997975zz5tnzA/uH8M95zvnfHPPOd/2oqibbrrpppvqA+gDzABWAKuBVcA84PBodwAYCDQCVwMnpuzbAPxAPH8DtwA1UTUC9ARuALaZiT8L9C7R9xBgJcl4KKo2gOHAh0UmfV+Bfj2AK4HfScekqBoAegO3u89T8wGwRP29EzjV9B0GvEV5fAcM6LqVR/8t4BTgMzMx+fxnA7WuTbN69wWwp+p/LX482VUL3we43/1XNW/akxroD3yj2sw3Z4Z8KT6cXenFnw5sMJOQ/Tur0OkMnKfa7tA3A3B8zPZJw1dA30osvB+wCNhlJvAyMKREX1nkr6rPx0Av9V7OEB8erMTiPzGD/ix3doID8rYC/+G5pt3nHgrodMBmrYDrzYAvAYMTHJCfFpn0duAY1f5k4B8PJXQ4YLNWwEdqoBkJzNgHYg7ION7W54YYOPhxRygFbFCDnFWkXT2wOeWkZ6r+fYEWDwXIVhseQgFPqEHWyvVl3g+QO7nMSW8FDlOyxuPHOju/LBQwDGhVgzSpd5OcVebDK2a8xZ7y2ueXpRKa1ACijNHOXc2KqWqs/YHvPWTJ/I7MWgE93ecfit/0zQJM8ZT3buZuMzmDpo1wLDfj+X5hRW+ssgBuJSwXqbEGA1s8ZP0BHFreSh1irIjXZqy2YgaOL98C+6nxLveUtzIqB6CXO/i2O/t/nHo30tNqK8VjJmDyuqe8xrSLP8HdpxoxcPqoNgsIhyi8Xo1VB/zpIU/8loFJFr6X+OtF/rv3qrZ7AxsJh1W4b/DkmVKLPzrGz7eIYkaoPmNj3OMsaY8jSoQJWOMp74JCix9kojbFELd4D9X3ccIhCh+ZYfBE1rhvnAKWpRR0swl5yckdivVG4b7X8KIoxpHZkVKIGEPHKhkTCcu8DK9h2bKjtQLOLVPQ6nzk18l5jnCIwo9TY41KGG8oxHtaAZd5CLpGyTkY+IVwvG8ULkEXH47IC5rgIUTu5rqMlJmE2SbylDb40jmzRM719LHqxErroSb2KuHYpvMOkhPwkNWgt4FOYZXDNCVrqIvyhOINo/ByI1Gn2cxs2uSkRmL+g5S8mYRlurnF0gZPftJ5ibwgydD68LySVQO8Q+WCJ5NT9p/TYfHK6yo3U5vnYmNaiycZihfN/F9I2G+Vvk3igp9/eUxKgqP9lby5hGWKCZ7Il1HMAHpUW5WxANd5TqrZxBGtW50lspcPVONNL9Bus3avi0LO6/INftabyhEfB6YUi802fs381xemzh7j73W1GF/+LsIyIeYalljFmFQL17isrg8LTKDlS8LRoUbA1TD4JUrJeV3iimaSsgbODBw8yb5yjFyK28frWq/L49wpHILWIOmwMrwuibgsd3tersCHgfNNocXXGS9eUuxHRYELojaVmMQ6F1eoTSCv3PiDZaszucNXjQLjCuzfXe6wrE0p7ynPxYvHOTTcimMw9X55Zpk2cg+fBFzlymqmiqMVI+sA4Mcyna5p2husGHQOfjab96NMKY3+Sp4GDjLtL0m5+BXa4+wSgDHuEJNPsJ8pkmhLYBx1SFS6YqtSiKs7OapW6OxASZhsKXCPy9Fr1piiqCElsr9LurweuBQmmrTJHk7u69Bm9UTz/ooClt05UbVDztuTHHyeWI8LuFu1WRZzcEqNcf68eKQipa9ZINVd5l6OPZ1dEWT7NiiwjaR89oxod4KOCthSRAEjVLu1BdpU/mrzxRVR6C0wtkC7O1WbpdH/CXInvo681Jn3402N4YVRtUAuetvoSmHkmRNXXxfTTj9SQq9pdTfDfJc02WnyiYk+dUlju1+dNQV4ZC01MsilMVfQxpjJxLVLS0ucWZzS9M6SxshpIokC4tolpc1lb9qDlwkVELLwQmjIf9ryQ0X5XOW5Sa6kmMnYdkmeG13CoujvCoooQLaA/PgqzZhJH1lLzb9OmIK693ZfOQAAAABJRU5ErkJggg=="
                  alt="規劃圖示"
                />
                <span class="button-text">開始規劃</span>
              </div>
              <img
                v-else
                key="small"
                class="icon-small"
                src="data:image/png;base64,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"
                alt="規劃圖示"
              />
            </transition>
          </RouterLink>
        </div>
      </div>
    </div>

    <footer>
      <div class="footer-content">
        <div class="footer-section">
          <h3>探索下一個旅程</h3>
          <ul>
            <li><a href="#">關於我們</a></li>
            <li><a href="#">官方粉專</a></li>
            <li>
              <a
                href="https://docs.google.com/forms/d/e/1FAIpQLScfLqx3Y79IbletTm2zhNoW25Wbo_pM_RkAfNbvKjgt4tY89A/viewform?usp=dialog"
                target="_blank"
                rel="noopener noreferrer"
              >
                聯絡我們
              </a>
            </li>
          </ul>
        </div>
        <div class="footer-section">
          <h3>公司資訊</h3>
          <ul>
            <li>快樂出發有限公司</li>
            <li>統一編號：12345678</li>
            <li>旅行業註冊編號：CHUFA</li>
          </ul>
        </div>
        <div class="footer-section">
          <h3>其他產品</h3>
          <ul>
            <li>統一編號：9999999</li>
            <li>聯繫地址：106台北市大安區復興南路一段390號2樓</li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© Chufa, Inc. 2025</p>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, watch, inject, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user.js";
import Swal from "sweetalert2";
import { useRoute } from "vue-router";
import { useSearchStore } from "@/stores/search.js";
import axiosapi from "@/plugins/axios.js";
import { usePostStore } from "@/stores/usePostStore";
import defaultProfilePicture from "@/assets/empty.png";
import defaultback from "@/assets/default.jpg";
import Carousel from "@/components/blog/Carousel.vue";

export default {
  components: {
    Carousel, // 註冊 PostCard 元件
  },
  setup() {
    const router = useRouter();
    const profileLoaded = ref(false);
    const member = ref({});
    const posts = ref([]);
    const users = ref([]);
    const isAdmin = ref(false);
    const userId = ref(null);
    const currentPage = ref(1); // 當前頁數
    const noPosts = ref(false);
    const sortBy = ref("likes"); // 排序狀態
    const searchQuery = ref("");
    const isSearch = ref(false);
    const searchStore = useSearchStore();
    const selectedPlace = ref(null);
    const defaultProfilePic = ref(defaultProfilePicture);
    const defaultpicture = ref(defaultback);
    const isCarouselFlag = ref(false);
    // 管理輸入的 postid
    const postidInput = ref("");
    const postIds = ref([]);
    const hoverBlog = ref(false);
    const hoverPlanning = ref(false);

    // 更新 Carousel 的 postIds
    // const updateCarousel = () => {
    //   // 將輸入字串轉換成 postid 陣列
    //   postIds.value = postidInput.value
    //     .split(",")
    //     .map((id) => parseInt(id.trim()))
    //     .filter((id) => !isNaN(id));
    // };

    //place
    //const selectedPlace = ref(null);
    const places = ref([
      { id: 1, name: "台北市" },
      { id: 2, name: "新北市" },
      { id: 3, name: "南投縣" },
      { id: 4, name: "台中市" },
      { id: 5, name: "臺南市" },
      { id: 6, name: "高雄市" },
      { id: 7, name: "台東市" },
      { id: 8, name: "花蓮縣" },
    ]);

    const email = "chufa@gmail.com";
    const mailToLink = computed(() => {
      return `mailto:${email}`;
    });

    watch(sortBy, () => {
      fetchPosts(); // 每次排序方式改變時重新抓取資料
    });

    // 切換到下一張
    const nextSlide = () => {
      currentIndex.value = (currentIndex.value + 1) % posts.value.length;
    };

    // 切換到上一張
    const prevSlide = () => {
      currentIndex.value =
        (currentIndex.value - 1 + posts.value.length) % posts.value.length;
    };

    const getFirstImage = (content) => {
      const match = content.match(/<img[^>]+src="([^">]+)"/);
      return match ? match[1] : null;
    };

    const getTextPreview = (content, length) => {
      // 移除圖片和其他 HTML 標籤
      const textContent = content
        .replace(/<img[^>]*>/g, "")
        .replace(/<[^>]+>/g, "");
      return (
        textContent.slice(0, length) +
        (textContent.length > length ? "..." : "")
      );
    };
    const postStore = usePostStore();

    // computed 屬性：只回傳未被隱藏的貼文
    const visiblePosts = computed(() => {
      return posts.value.filter(
        (post) => !postStore.getHiddenReason(post.postid)
      );
    });
    const userStore = useUserStore(); // 使用 Pinia 的狀態

    const navigateToDetail = (post, event) => {
      const excludedElements = [".post-actions", ".action-btn", "a", "button"];
      for (let selector of excludedElements) {
        if (event.target.closest(selector)) {
          return; // 如果點擊的是按鈕、連結，就不觸發跳轉
        }
      }
      // 如果是轉發的貼文，跳轉到原貼文的詳細頁
      if (post.repost && post.repostDTO) {
        router.push(`/blog/find/${post.repostDTO.postid}`);
      } else {
        // 否則跳轉到當前貼文的詳細頁
        router.push(`/blog/find/${post.postid}`);
      }
    };

    const formatDate = (date) => {
      if (!date) return "";
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    const fetchProfile = async () => {
      try {
        const response = await axiosapi.get("/ajax/secure/profile", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (response.data.success) {
          member.value = response.data.user || {};
          isAdmin.value = response.data.user.role === "ADMIN";
          userId.value = member.value.userid;
        }
        profileLoaded.value = true;
      } catch (error) {
        console.error("Fetch profile failed:", error);
        Swal.fire("錯誤", "無法取得會員資料", "error");
      }
    };

    const fetchPosts = async (query = "") => {
      try {
        const requestData = {
          page: currentPage.value,
          size: 12,
          checklike: member.value.userid,
          repost: true,
        };

        // 動態設定排序條件
        requestData[
          sortBy.value === "likes" ? "sortByLikes" : "sortByTime"
        ] = true;

        if (query) {
          requestData.postTitle = query; // 加入搜尋條件
          isSearch.value = true;
        }
        if (selectedPlace.value === "follow") {
          //requestData.repost=true;
          requestData.followerId = member.value.userid;
        } else if (selectedPlace.value !== null || selectedPlace !== "users") {
          // 只有選擇地點時才加入 place
          requestData.places = selectedPlace.value;
        }

        const response = await axiosapi.post("/api/posts/post", requestData, {
          headers: {
            "Content-Type": "application/json",
            "Cache-Control": "no-cache",
          },
        });
        if (response.data.postdto && response.data.postdto.length > 0) {
          posts.value = response.data.postdto;
          // .filter(
          //   (post) => !post.repost && post.repostDTO === null
          // );
          noPosts.value = false;
        } else {
          posts.value = [];
          // currentPage.value = Math.max(1, currentPage.value - 1); // 返回有效的上一頁
          // Swal.fire("已經到底啦!", "no post。", "info");
        }
      } catch (error) {
        console.error("Fetch posts failed:", error);
        Swal.fire("錯誤", "無法取得貼文", "error");
      }
    };

    //tab
    const switchPlace = (placeName) => {
      if (placeName === "follow") {
        selectedPlace.value = "follow";
      } else {
        selectedPlace.value = placeName;
      }
      currentPage.value = 1;
      // const url = new URL(window.location.href);
      // url.search = ''; // 清空查詢參數
      // window.history.replaceState(null, '', url);

      searchStore.resetSearch(); // 清空搜索
      fetchPosts();
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

        const response = await axiosapi.post(
          "/api/posts/repost/forward",
          data,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        if (response.data.repost) {
          Swal.fire("成功", "轉發成功！", "success");
          await fetchPosts();
        } else {
          Swal.fire("錯誤", "轉發失敗！", "error");
        }
      } catch (error) {
        console.error("轉發請求失敗:", error);
        Swal.fire("請先登入", "登入體驗更好", "error");
      }
    };

    // 判断当前用户是否已经点赞
    const likePost = async (postid) => {
      try {
        // 查找当前操作的帖子
        const postToUpdate = posts.value.find((post) => post.postid === postid);
        const data = {
          postid: postid,
          userid: member.value.userid,
          interactionType: "LIKE", // 如果点赞则是 LIKE，取消点赞则是 DISLIKE
        };

        const response = await axiosapi.post(
          "/api/posts/insertinteraction",
          data,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );

        if (response.data.success) {
          // 更新本地状态：更新点赞状态和点赞数量
          // 更新本地状态：根据操作更新点赞状态和点赞数量
          const updatedPosts = posts.value.map((post) => {
            if (post.postid === postid) {
              return {
                ...post,
                likedByCurrentUser: !post.likedByCurrentUser, // 反转点赞状态
                likeCount: post.likedByCurrentUser
                  ? post.likeCount - 1
                  : post.likeCount + 1, // 根据点赞状态增加或减少点赞数
              };
            }
            return post;
          });

          // 更新本地 posts 状态
          posts.value = updatedPosts;
        } else {
          Swal.fire("錯誤", "點讚操作失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("請先登入", "登入體驗更好", "error");
      }
    };

    const collectPost = async (postid) => {
      try {
        const data = {
          postid,
          userid: member.value.userid,
          interactionType: "COLLECT",
        };

        const response = await axiosapi.post(
          "/api/posts/insertinteraction",
          data,
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        );
        if (response.data.success) {
          const updatedPosts = posts.value.map((post) => {
            if (post.postid === postid) {
              return {
                ...post,
                collectByCurrentUser: !post.collectByCurrentUser, // 反转点赞状态
              };
            }
            return post;
          });

          // 更新本地 posts 状态
          posts.value = updatedPosts;
        } else {
          Swal.fire("錯誤", "點讚失敗！", "error");
        }
      } catch (error) {
        console.error("點讚請求失敗:", error);
        Swal.fire("請先登入", "登入體驗更好", "error");
      }
    };

    const setSort = (type) => {
      if (sortBy.value !== type) {
        sortBy.value = type;
        fetchPosts();
      }
    };
    const route = useRoute();
    const resetSearch = () => {
      searchStore.resetSearch(); // 调用 Pinia store 中的 resetSearch 方法
    };

    watch(
      () => route.query.title,
      (newQuery) => {
        if (newQuery) {
          fetchPosts(newQuery); // 如果有搜尋條件就請求搜尋
        } else {
          fetchPosts(); // 沒有搜尋條件則請求普通的 fetchPosts
        }
      },
      { immediate: true }
    );

    //watch
    // 監聽 sortBy 的變化，當選擇變更時請求 fetchPost

    onMounted(async () => {
      selectedPlace.value = null;
      //selectedPlace.value = places.value[0].id;
      await fetchPosts();
      await fetchProfile();
      const query = route.query.title || ""; // 如果 query.title 為 undefined，則使用空字串
      fetchPosts(query); // 根據查詢條件抓取貼文
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
      visiblePosts, // 將 visiblePosts 回傳給模板
      navigateToDetail,
      currentPage,
      nextPage,
      prevPage,
      places,
      switchPlace,
      selectedPlace,
      getFirstImage,
      //getContentWithoutImages,
      getTextPreview,
      userStore,
      setSort,
      sortBy,
      searchQuery,
      isSearch,
      searchStore,
      users,
      defaultProfilePic,
      defaultpicture,
      //postidInput,
      nextSlide,
      prevSlide,
      isCarouselFlag,
      //updateCarousel,
      postIds,
      mailToLink,
      hoverBlog,
      hoverPlanning,
    };
  },
};
</script>

<style scoped>
.main-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  height: 100vh;
  flex-direction: column;
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* 保證頁面至少佔滿視口 */
}

/* Tab 容器 */
.tabs-container {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0; /* 底部邊框 */
  overflow-x: auto;
}

/* Tab 按鈕 */
.tab {
  padding: 10px 20px;
  border: none;
  background-color: transparent;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: color 0.3s, border-bottom 0.3s;
  white-space: nowrap;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Tab 按鈕懸停效果 */
.tab:hover {
  color: #333;
}

/* 當前選中的 Tab */
.tab.active {
  color: #000;
  font-weight: 500;
}

/* 選中 Tab 的下劃線效果 */
.tab.active::after {
  content: "";
  position: absolute;
  bottom: -1px; /* 對齊底部邊框 */
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #000; /* 黑色下劃線 */
  border-radius: 2px;
}

/* 排序選擇器 */
.sort-select-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-grow: 1;
}

.sort-select-container select {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background-color: #f9f9f9;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.sort-select-container select:hover {
  border-color: #ccc;
}

.sort-select-container select:focus {
  outline: none;
  border-color: #000;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
}

/* 帖子网格布局 */
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
  flex: 1;
}

/* 帖子卡片样式 */
.post-card {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

/* 转发布局样式 */
.repost-header {
  padding: 10px;
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.interaction-info {
  display: flex;
  align-items: center;
}

.repost-profile-container {
  margin-right: 10px;
}

.profile-picture.small-profile {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
}

.interaction-name {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 作者信息样式 */
.author-info {
  padding: 15px;
}

.author-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.profile-picture-container {
  margin-right: 10px;
}

.profile-picture {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 16px;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
  margin: 5px 0;
  text-align: left;
}

h3 {
  font-size: 18px;
  color: #333;
  margin: 10px 0;
}

/* 帖子图片样式 */
.post-image-container {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 帖子内容预览样式 */
.post-content-preview {
  font-size: 14px;
  color: #666;
  padding: 0 12px 12px;
  margin: 0;
  line-height: 1.5;
  text-align: left;
}

/* 互动按钮容器样式 */
.post-actions {
  display: flex;
  justify-content: space-around;
  padding: 10px;
  border-top: 1px solid #eee;
}

/* 没有文章时的提示样式 */
.posts-grid + div {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #666;
}

.default-profile {
  width: 100%;
  height: 100%;
  background-color: #ccc;
  border-radius: 50%;
}

/* 基本的愛心按鈕樣式 */
.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: transform 0.2s, color 0.2s;
}

.like-btn.active {
  color: red; /* 爱心变为红色 */
}

.like-btn.active::before {
  content: "❤️"; /* 实心爱心 */
}

.like-btn:not(.active)::before {
  content: "🖤"; /* 空心爱心 */
}

/* 点击时的动画效果 */
.like-btn:active {
  transform: scale(1.2);
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  transition: transform 0.2s, color 0.2s;
}

.collect-btn.active {
  color: #ffcc00; /* 书签变为黄色 */
}

.collect-btn.active::before {
  content: "⭐"; /* 实心书签 */
}

.collect-btn:not(.active)::before {
  content: "⭐"; /* 空心书签 */
}

/* 点击时的动画效果 */
.collect-btn:active {
  transform: scale(1.2);
}

@keyframes fillBookmark {
  0% {
    background: linear-gradient(90deg, #62605a 0%, #bab7a9 0%);
    -webkit-background-clip: text;
    color: transparent;
  }
  100% {
    background: linear-gradient(90deg, #ffcc00 100%, #ffcc00 100%);
    -webkit-background-clip: text;
    color: transparent;
  }
}

.collect-btn.active {
  animation: fillBookmark 0.5s ease-out forwards;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 10px;
}

.pagination button {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background-color: #f0f0f0;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: background-color 0.3s, color 0.3s;
}

.pagination button:hover {
  background-color: #2973b2;
  color: white;
}

.pagination button:disabled {
  background-color: #ccc;
  color: #666;
  cursor: not-allowed;
}

.pagination span {
  font-size: 14px;
  color: #333;
}

.clearfix::after {
  content: "";
  display: block;
  clear: both;
}

footer {
  background-color: #f2efe7;
  padding: 20px;
  font-family: Arial, sans-serif;
  color: #121322;
  margin-top: auto;
  bottom: 0;
  width: 200%;
  margin-left: -50%;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.footer-section {
  flex: 1;
  margin-right: 20px;
}

.footer-section h3 {
  margin-bottom: 15px;
  font-size: 18px;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
  line-height: 1.6; /* 統一文字行高 */
}

.footer-section ul li a {
  text-decoration: none;
  color: #333;
}

.footer-section ul li a:hover {
  color: #007bff;
}

.footer-bottom {
  text-align: center;
  border-top: 1px solid #ddd;
  padding-top: 10px;
  margin-top: 20px;
}

/* 按鈕內容 */
.button {
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  background-color: transparent;
  width: 100%;
  height: 100%;
  padding: 10px;
  transition: width 0.4s ease-in-out, background-color 0.3s ease-in-out;
}

/* 發文 & 開始規劃按鈕 */
#blogbutton,
#planningbutton {
  position: fixed;
  right: 50px;
  width: 80px;
  height: 80px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  transition: width 0.4s ease-in-out;
  cursor: pointer;
  min-width: 80px;
}

/* 個別按鈕顏色 */
#blogbutton {
  bottom: 150px;
  background-color: #48a6a7;
}

#planningbutton {
  bottom: 50px;
  background-color: #48a6a7;
}

/* 懸停時展開 */
#blogbutton:hover,
#planningbutton:hover {
  width: 200px;
  justify-content: flex-start;
}

/* 小圖示（收合狀態） */
.icon-small {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

/* 展開後的內容 */
.expanded-content {
  display: flex;
  align-items: center;
  gap: 10px; /* 圖示與文字之間的間距 */
}

/* 大圖示（展開狀態） */
.icon-large {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

/* 文字樣式 */
.button-text {
  white-space: nowrap;
  font-size: 25px;
  color: white;
  opacity: 0;
  transform: translateX(-10px);
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out;
}

/* 懸停時讓文字顯示 */
#blogbutton:hover .button-text,
#planningbutton:hover .button-text {
  opacity: 1;
  transform: translateX(0);
}

/* 動畫效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease-in-out;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
