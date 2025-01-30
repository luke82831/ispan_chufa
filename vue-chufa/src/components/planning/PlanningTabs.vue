<template>
  <div>
    <!-- 行程標題 -->
    <h2>{{ itineraryTitle }}</h2>
    <p>{{ formattedStartDate }} - {{ formattedEndDate }}</p>

    <!-- 日期選擇的分頁 -->
    <div class="tabs">
      <button
        v-for="(date, index) in dateRange"
        :key="index"
        @click="selectedDate = formatDate(date)"
        :class="{ active: selectedDate === formatDate(date) }"
      >
        {{ formatDate(date) }}
      </button>
    </div>

    <!-- 選擇的日期 -->
    <h3>選擇的日期：{{ selectedDate }}</h3>

    <!-- 新增地點按鈕 -->
    <button @click="addPlaceToDate">新增地點</button>

    <!-- 顯示對應日期的行程 -->
    <div v-if="dateRange.length > 0">
      <h3>行程：{{ selectedDate }}</h3>
      <VueDraggableNext
        v-model="placeStore.itinerariesByDate[selectedDate]"
        item-key="displayName"
      >
        <div
          v-for="(itinerary, index) in placeStore.itinerariesByDate[selectedDate]"
          :key="itinerary.displayName"
          class="itinerary-item"
        >
          <ol>
            <span class="itinerary-name">{{ itinerary.displayName }}</span>
            <span class="itinerary-address">{{ itinerary.formattedAddress }}</span>
            <!-- 刪除按鈕 -->
            <button @click="removeFromItinerary(index, selectedDate)" class="delete-btn">
              X
            </button>
          </ol>
        </div>
      </VueDraggableNext>
    </div>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/placestore";
import { ref, onMounted } from "vue";
import { VueDraggableNext } from "vue-draggable-next";

const placeStore = usePlaceStore();

// 使用者選擇的日期（預設為第一天）
const selectedDate = ref("");

// 記錄行程標題和開始/結束日期
const itineraryTitle = ref("");
const formattedStartDate = ref("");
const formattedEndDate = ref("");
const dateRange = ref([]);

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

// 新增地點到選擇的日期
const addPlaceToDate = () => {
  if (!selectedDate.value) {
    alert("請先選擇日期");
    return;
  }

  const newPlace = {
    displayName: "新地點",
    formattedAddress: "地址",
  };

  // 確保該日期有初始化
  if (!placeStore.itinerariesByDate[selectedDate.value]) {
    placeStore.itinerariesByDate[selectedDate.value] = [];
  }

  placeStore.itinerariesByDate[selectedDate.value].push(newPlace);
};

// 刪除指定日期的行程
const removeFromItinerary = (index, date) => {
  placeStore.itinerariesByDate[date].splice(index, 1);
};

// 初始化行程日期
onMounted(() => {
  const storedData = JSON.parse(localStorage.getItem("itineraryData"));

  if (storedData && storedData.startDate && storedData.endDate) {
    itineraryTitle.value = storedData.name || "無行程名稱";
    formattedStartDate.value = new Date(storedData.startDate).toLocaleDateString("zh-TW");
    formattedEndDate.value = new Date(storedData.endDate).toLocaleDateString("zh-TW");

    dateRange.value = getDateRange(storedData.startDate, storedData.endDate);

    // 設定預設選擇的日期為第一天
    selectedDate.value = formatDate(dateRange.value[0]);

    // 初始化每個日期對應的行程
    dateRange.value.forEach((date) => {
      const formattedDate = formatDate(date);
      if (!placeStore.itinerariesByDate[formattedDate]) {
        placeStore.itinerariesByDate[formattedDate] = [];
      }
    });
  }
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
