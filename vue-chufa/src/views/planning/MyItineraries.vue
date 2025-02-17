<template>
  <div class="itineraries-container">
    <h1 class="itineraries-title">我的行程</h1>

    <!-- 新增行程按鈕 -->
    <div class="itineraries-new-btn">
      <button @click="goToItineraryForm">
        <span class="plus-icon">+</span> 新的行程
      </button>
    </div>

    <!-- 行程列表 -->
    <div v-if="loading" class="loading-text">載入中...</div>
    <div v-else-if="schedules.length === 0" class="no-schedules">目前沒有行程</div>
    <div v-else class="itineraries-grid">
      <div
        v-for="schedule in paginatedSchedules"
        :key="schedule.tripId"
        class="itinerary-card"
        @click="goToPlanningPage(schedule.tripId)"
      >
        <div class="itinerary-image-container">
          <img
            :src="getImageSource(schedule.coverPhoto)"
            alt="行程封面"
            class="itinerary-image"
          />

          <!-- 右上角垃圾桶按鈕 -->
          <button class="delete-btn" @click.stop="confirmDelete(schedule.tripId)">
            🗑️
          </button>
        </div>

        <!-- 文字內容 -->
        <div class="itinerary-details">
          <h3>{{ schedule.tripName }}</h3>
          <p>{{ schedule.startDate }} - {{ schedule.endDate }}</p>
        </div>
      </div>
    </div>

    <!-- 🔹 分頁按鈕 -->
    <div class="pagination">
      <button @click="changePage('prev')" :disabled="currentPage === 1">
        &lt; 上一頁
      </button>
      <span>第 {{ currentPage }} / {{ totalPages }} 頁</span>
      <button @click="changePage('next')" :disabled="currentPage === totalPages">
        下一頁 &gt;
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useScheduleStore } from "@/stores/ScheduleStore";
import Swal from "sweetalert2";

export default {
  setup() {
    const router = useRouter();
    const scheduleStore = useScheduleStore();
    const loading = ref(true);
    const schedules = computed(() => scheduleStore.schedules);

    const fetchSchedules = async () => {
      await scheduleStore.fetchSchedules();
      loading.value = false;
    };

    // 分頁設定
    const currentPage = ref(1); // 當前頁數
    const itemsPerPage = 8; // 每頁顯示 6 個行程

    // 計算總頁數
    const totalPages = computed(() => Math.ceil(schedules.value.length / itemsPerPage));

    // 取得當前頁面的行程清單
    const paginatedSchedules = computed(() => {
      const start = (currentPage.value - 1) * itemsPerPage;
      return schedules.value.slice(start, start + itemsPerPage);
    });

    // 切換頁面
    const changePage = (direction) => {
      if (direction === "prev" && currentPage.value > 1) {
        currentPage.value--;
      } else if (direction === "next" && currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const getImageSource = (coverPhoto) =>
      coverPhoto
        ? coverPhoto.startsWith("data:image")
          ? coverPhoto
          : `data:image/jpeg;base64,${coverPhoto}`
        : "";

    const goToItineraryForm = () => {
      router.push("/itineraryform");
    };

    // 刪除行程（使用 SweetAlert2）
    const confirmDelete = async (tripId) => {
      const result = await Swal.fire({
        title: "確定要刪除這個行程嗎？",
        text: "刪除後將無法恢復！",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "是的，刪除！",
        cancelButtonText: "取消",
      });

      if (result.isConfirmed) {
        await scheduleStore.deleteSchedule(tripId);
        await Swal.fire("已刪除！", "您的行程已成功刪除。", "success");

        // 確保刪除後頁面更新
        if (schedules.value.length % itemsPerPage === 1 && currentPage.value > 1) {
          currentPage.value--;
        }
      }
    };

    // 跳轉至行程規劃頁面
    const goToPlanningPage = (tripId) => {
      router.push({ path: `/planningpage/${tripId}` });
    };

    onMounted(fetchSchedules);

    return {
      schedules,
      loading,
      currentPage,
      totalPages,
      itemsPerPage,
      paginatedSchedules,
      goToItineraryForm,
      goToPlanningPage,
      getImageSource,
      confirmDelete,
      changePage,
    };
  },
};
</script>

<style>
/* 整個行程頁面 */
.itineraries-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 行程列表 */
.itineraries-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

/* 行程卡片 */
.itinerary-card {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
  position: relative;
}

.itinerary-card:hover {
  transform: scale(1.03);
}
.itineraries-new-btn {
  display: flex;
  justify-content: flex-end;
  align-items: center; /* 確保按鈕垂直對齊 */
  margin-top: 16px;
}

.itineraries-new-btn button {
  display: flex;
  align-items: center;
  gap: 5px; /* 調整 "+" 和文字之間的距離 */
  background-color: #2973b2;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  border: none;
  cursor: pointer;
  transition: background 0.3s;
}

.itineraries-new-btn button:hover {
  background-color: #1e5a8a;
}

/* 行程圖片 */
.itinerary-image-container {
  position: relative;
}

.itinerary-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

/* 右上角垃圾桶按鈕 */
.delete-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(253, 253, 253, 0.7);
  color: rgb(255, 255, 255);
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 滑鼠懸停時變暗 */
.delete-btn:hover {
  background: rgba(255, 0, 0, 1);
}

/* 行程資訊 */
.itinerary-details {
  padding: 12px;
}

.itinerary-details h3 {
  font-size: 16px;
  font-weight: bold;
}

.itinerary-details p {
  font-size: 14px;
  color: #777;
}

/* Loading 文字 */
.loading-text,
.no-schedules {
  text-align: center;
  color: #777;
  margin-top: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  margin: 0 10px;
  font-size: 14px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  transition: background 0.3s;
}

.pagination button:hover {
  background-color: #0056b3;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>
