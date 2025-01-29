<template>
  <h2>{{ itineraryTitle }}</h2>
  <p>{{ formattedStartDate }} - {{ formattedEndDate }}</p>

  <div>
    <!-- 顯示當前選擇的日期行程 -->
    <div>
      <h3>選擇日期: {{ placeStore.selectedDate }}</h3>
      <ul>
        <li
          v-for="(itinerary, index) in placeStore.itinerariesByDate[
            placeStore.selectedDate
          ]"
          :key="index"
        >
          {{ itinerary.displayName }} - {{ itinerary.formattedAddress }}
        </li>
      </ul>
    </div>
  </div>

  <!-- 日期分頁 -->
  <v-tabs v-model="selectedTab" @change="updateSelectedDate">
    <v-tab v-for="(date, index) in dateRange" :key="index" :value="date">
      {{ formatDate(date) }}
    </v-tab>

    <!-- 分頁內容 -->
    <v-tab-item v-for="(date, index) in dateRange" :key="'item-' + index">
      <div class="itinerary-content">
        <!-- 使用 VueDraggableNext 顯示每個日期對應的行程 -->
        <VueDraggableNext
          v-model="placeStore.itinerariesByDate[formatDate(date)]"
          item-key="displayName"
        >
          <div
            v-for="(itinerary, index) in placeStore.itinerariesByDate[formatDate(date)]"
            :key="itinerary.displayName"
            class="itinerary-item"
            @dragover="onDragOver"
            @dragleave="onDragLeave"
          >
            <ol>
              <span class="itinerary-name">{{ itinerary.displayName }}</span>
              <span class="itinerary-address">{{ itinerary.formattedAddress }}</span>
              <!-- 刪除按鈕 -->
              <button
                @click="removeFromItinerary(index, formatDate(date))"
                class="delete-btn"
              >
                X
              </button>
            </ol>
          </div>
        </VueDraggableNext>
      </div>
    </v-tab-item>
  </v-tabs>
</template>

<script setup>
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store
import { VueDraggableNext } from "vue-draggable-next";
import { ref, onMounted, watch } from "vue";

const placeStore = usePlaceStore();

// 記錄日期範圍和當前選中的 tab
const selectedTab = ref(0);
const dateRange = ref([]);

// 記錄行程標題和開始/結束日期
const itineraryTitle = ref("");
const formattedStartDate = ref("");
const formattedEndDate = ref("");

// 計算日期範圍
const getDateRange = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const dates = [];

  while (start <= end) {
    dates.push(new Date(start));
    start.setDate(start.getDate() + 1);
  }

  return dates;
};

// 格式化日期為 "1/1" 格式
const formatDate = (date) => {
  const options = { month: "numeric", day: "numeric" };
  return date.toLocaleDateString("zh-TW", options);
};

// 根據日期範圍初始化行程
onMounted(() => {
  const storedData = JSON.parse(localStorage.getItem("itineraryData"));

  if (storedData && storedData.startDate && storedData.endDate) {
    itineraryTitle.value = storedData.name || "無行程名稱";
    formattedStartDate.value = new Date(storedData.startDate).toLocaleDateString("zh-TW");
    formattedEndDate.value = new Date(storedData.endDate).toLocaleDateString("zh-TW");

    dateRange.value = getDateRange(storedData.startDate, storedData.endDate);

    // 初始化每個日期對應的行程
    dateRange.value.forEach((date) => {
      const formattedDate = formatDate(date);
      if (!placeStore.itinerariesByDate[formattedDate]) {
        placeStore.itinerariesByDate[formattedDate] = [];
      }
    });
  }

  // 初始化 selectedDate，選擇當前的日期
  const initialDate = formatDate(dateRange.value[selectedTab.value]);
  placeStore.setSelectedDate(initialDate);
});

// 更新選擇的日期，並儲存到 Pinia
const updateSelectedDate = (date) => {
  placeStore.setSelectedDate(date); // 更新 Pinia 中的 selectedDate
};

// 呼叫 store 中的 removeFromItinerary 方法刪除行程
const removeFromItinerary = (index, date) => {
  placeStore.removeFromItinerary(index, date);
};

// 處理拖曳事件，改變鼠標樣式
const onDragOver = (event) => {
  event.preventDefault(); // 允許拖曳進入目標區域
  event.target.style.cursor = "move"; // 改變鼠標樣式
};

const onDragLeave = (event) => {
  event.target.style.cursor = "default"; // 恢復默認鼠標樣式
};

// 監視 selectedTab，當 tab 改變時，更新日期
watch(selectedTab, (newTabIndex) => {
  const selectedDate = formatDate(dateRange.value[newTabIndex]);
  updateSelectedDate(selectedDate); // 當選擇日期分頁時，更新 Pinia 儲存的日期
});
</script>

<style scoped>
.itinerary-item {
  border: 3px solid #5f5f5f; /* 强制应用边框 */
  margin: 0px 20px 10px 20px;
  border-radius: 5px;
  position: relative; /* 讓刪除按鈕可以定位 */
}

.itinerary-name {
  display: block;
  font-weight: bold; /* 粗体 */
}

.itinerary-address {
  display: block;
  font-size: 0.8em; /* 字体稍小 */
  color: #666; /* 地址颜色 */
}

.delete-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent; /* 按鈕背景透明 */
  border: none;
  font-size: 1.2em; /* 增大字體以顯示為 X */
  color: #888; /* 按鈕顏色 */
  cursor: pointer; /* 鼠標為可點擊狀態 */
}

.delete-btn:hover {
  color: #f44336; /* 鼠標懸停時顏色改變 */
}
</style>
