<template>
  <div>
    <h1>出發 ! 來一場旅行吧</h1>
    <form @submit.prevent="handleSubmit" class="itinerary-form">
      <div class="form-group">
        <label for="coverPhoto">封面照片：</label>
        <input
          type="file"
          @change="onFileChange"
          id="coverPhoto"
          class="input-file"
        />
      </div>

      <div class="form-group">
        <label for="itinerary-name">行程名稱：</label>
        <input
          type="text"
          id="itinerary-name"
          v-model="itineraryStore.itineraryTitle"
          class="input-text"
          required
        />
      </div>

      <!-- 起始日期：只允許明天或之後的日期 -->
      <div class="form-group">
        <label for="start-date">開始日期：</label>
        <input
          type="date"
          id="start-date"
          v-model="itineraryStore.startDate"
          :min="minStartDate"
          class="input-date"
          required
        />
      </div>

      <!-- 結束日期：必須大於起始日期 -->
      <div class="form-group">
        <label for="end-date">結束日期：</label>
        <input
          type="date"
          id="end-date"
          v-model="itineraryStore.endDate"
          :min="minEndDate"
          class="input-date"
          required
        />
      </div>

      <button type="submit" class="submit-button">建立行程</button>
    </form>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
import { useRouter } from "vue-router";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { useUserStore } from "@/stores/user"; // 使用 Pinia 的 userStore
import axios from "@/plugins/axios.js";
import Swal from "sweetalert2";

const router = useRouter();
const itineraryStore = useItineraryStore();
const userStore = useUserStore(); // 直接使用 userStore

// **使用 Pinia 的 userId**
const userId = computed(() => userStore.member?.userid);

// 計算明天的日期（即「今天之後」的最小日期）
const minStartDate = computed(() => {
  const today = new Date();
  today.setDate(today.getDate());
  return today.toISOString().split("T")[0];
});

// 計算結束日期的最小值
const minEndDate = computed(() => {
  if (itineraryStore.startDate) {
    const start = new Date(itineraryStore.startDate);
    start.setDate(start.getDate() + 1);
    return start.toISOString().split("T")[0];
  }
  return minStartDate.value;
});

// 如果起始日期改變，則清除不符合條件的結束日期
watch(
  () => itineraryStore.startDate,
  (newStartDate) => {
    if (itineraryStore.endDate && itineraryStore.endDate < minEndDate.value) {
      itineraryStore.endDate = "";
    }
  }
);

// 提交行程
const handleSubmit = async () => {
  if (!userId.value || !itineraryStore.coverPhoto) {
    Swal.fire(
      "錯誤",
      userId.value ? "封面照片是必填的" : "無法找到使用者ID",
      "error"
    );
    return;
  }

  const formData = new FormData();
  formData.append("coverPhoto", itineraryStore.coverPhoto);
  formData.append("tripName", itineraryStore.itineraryTitle);
  formData.append("startDate", itineraryStore.startDate);
  formData.append("endDate", itineraryStore.endDate);
  formData.append("userId", userId.value);

  try {
    const response = await axios.post("/api/schedule", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    // 從後端獲取 tripId
    const tripId = response.data.tripId;

    // 確保 tripId 存在
    if (!tripId) {
      throw new Error("行程 ID 未正確返回");
    }

    Swal.fire("成功", "行程已成功建立", "success");

    // 存入行程資料
    itineraryStore.setItinerary(
      itineraryStore.itineraryTitle,
      itineraryStore.startDate,
      itineraryStore.endDate
    );

    // **修改這裡，帶入 tripId 進行跳轉**
    router.push(`/planningpage/${tripId}`);
  } catch (error) {
    Swal.fire("錯誤", error.response?.data?.message || "發生未知錯誤", "error");
  }
};

// 處理上傳圖片
const onFileChange = (event) => {
  const file = event.target.files[0];
  if (file && file.size <= 10 * 1024 * 1024) {
    itineraryStore.coverPhoto = file;
  } else {
    Swal.fire("錯誤", "文件大小超過限制或未選擇檔案", "error");
  }
};
</script>

<style scoped>
/* 容器設定 */
.itinerary-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 標題 */
h1 {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

/* 表單區塊 */
.form-group {
  margin-bottom: 15px;
}

.label {
  font-weight: bold;
  margin-bottom: 5px;
  display: block;
  color: #555;
}

/* 輸入框 */
.input-file,
.input-text,
.input-date {
  width: 100%;
  padding: 8px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ddd;
  margin-top: 5px;
}

.input-file {
  padding: 6px;
}

.input-text:focus,
.input-date:focus {
  border-color: #0056b3;
  outline: none;
}

/* 按鈕 */
.submit-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #0056b3;
}

/* 錯誤訊息 */
.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}
</style>
