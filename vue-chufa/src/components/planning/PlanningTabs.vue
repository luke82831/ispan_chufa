<template>
  <div>
    <h2>{{ itineraryTitle }}</h2>
    <p>{{ formattedStartDate }} - {{ formattedEndDate }}</p>

    <!-- 日期分頁 -->
    <div class="date-tabs">
      <button class="arrow-button" @click="changeDate('prev')" :disabled="isFirstDay">
        &lt;
        <!-- 左箭頭 -->
      </button>

      <button
        v-for="(date, index) in dateRange"
        :key="index"
        :class="{ active: selectedTab === formatDate(date) }"
        @click="updateSelectedDate(date)"
      >
        {{ formatDate(date) }}
      </button>

      <!-- 顯示"＋"按鈕，在最後一天顯示 -->
      <button v-if="isLastDay" @click="addOneMoreDay" class="add-day-btn">＋</button>

      <button class="arrow-button" @click="changeDate('next')" :disabled="isLastDay">
        &gt;
        <!-- 右箭頭 -->
      </button>
    </div>

    <!-- 根據選擇的日期顯示行程 -->
    <div v-for="(date, index) in dateRange" :key="'item-' + index">
      <div v-show="selectedTab === formatDate(date)" class="itinerary-content">
        <VueDraggableNext
          v-model="placeStore.itinerariesByDate[formatDate(date)]"
          item-key="key"
        >
          <div
            v-for="(item, index) in computedItinerary[formatDate(date)]"
            :key="item.key"
          >
            <RouteSelector
              v-if="item.type === 'travel'"
              :origin="item.origin"
              :destination="item.destination"
              :defaultRoute="item.route"
              :onUpdate="(newRoute) => updateRoute(index, formatDate(date), newRoute)"
            ></RouteSelector>
            <div v-else class="itinerary-item">
              <span class="itinerary-name">{{ item.displayName }}</span>
              <span class="itinerary-address">{{ item.formattedAddress }}</span>
            </div>
          </div>
        </VueDraggableNext>
      </div>
    </div>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/PlaceStore"; // 引入 Pinia store
import { ref, onMounted, watch, computed } from "vue";
import { VueDraggableNext } from "vue-draggable-next";
import RouteSelector from "./GoogleMap/RouteSelector.vue";

const placeStore = usePlaceStore();

// 記錄日期範圍和當前選中的 tab
const selectedTab = ref(""); // 用來跟蹤當前選擇的日期
const dateRange = ref([]);

// 記錄行程標題和開始/結束日期
const itineraryTitle = ref("");

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

// 更新選擇的日期，並儲存到 Pinia
const updateSelectedDate = (date) => {
  selectedTab.value = formatDate(date); // 更新選中的日期
  placeStore.setSelectedDate(selectedTab.value); // 更新 Pinia 中的 selectedDate
};

// 切換日期
const changeDate = (direction) => {
  const currentDateIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedTab.value
  );

  if (direction === "prev" && currentDateIndex > 0) {
    selectedTab.value = formatDate(dateRange.value[currentDateIndex - 1]);
  } else if (direction === "next" && currentDateIndex < dateRange.value.length - 1) {
    selectedTab.value = formatDate(dateRange.value[currentDateIndex + 1]);
  }
};

// 增加一天到日期範圍
const addOneMoreDay = () => {
  const lastDate = dateRange.value[dateRange.value.length - 1]; // 取最後一天
  const newDate = new Date(lastDate);
  newDate.setDate(newDate.getDate() + 1); // 增加一天

  // 更新日期範圍
  dateRange.value.push(newDate); // 加入新的一天

  // 同步更新行程資料
  const formattedDate = formatDate(newDate);
  if (!placeStore.itinerariesByDate[formattedDate]) {
    placeStore.itinerariesByDate[formattedDate] = [];
  }

  // 設定選擇的日期為新增的日期
  selectedTab.value = formattedDate;
};

// 計算是否為第一天
const isFirstDay = computed(() => {
  const currentDateIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedTab.value
  );
  return currentDateIndex === 0;
});

// 計算是否為最後一天
const isLastDay = computed(() => {
  const currentDateIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedTab.value
  );
  return currentDateIndex === dateRange.value.length - 1;
});

// 計算開始日期和結束日期
const formattedStartDate = computed(() => {
  const startDate = dateRange.value[0]; // 取日期範圍的第一天
  return startDate ? startDate.toLocaleDateString("zh-TW") : "";
});

const formattedEndDate = computed(() => {
  const endDate = dateRange.value[dateRange.value.length - 1]; // 取日期範圍的最後一天
  return endDate ? endDate.toLocaleDateString("zh-TW") : "";
});

// **用 computed 產生包含地點與路線的行程**
const computedItinerary = computed(() => {
  const itineraryWithRoutes = {};

  for (const date in placeStore.itinerariesByDate) {
    const places = placeStore.itinerariesByDate[date];
    itineraryWithRoutes[date] = [];

    places.forEach((place, index) => {
      // 為每個 place 項目生成唯一的 key
      itineraryWithRoutes[date].push({
        ...place,
        type: "place", // 地點
      });

      // **在地點之間插入 travel (預設最佳路線)**
      if (index < places.length - 1) {
        itineraryWithRoutes[date].push({
          type: "travel",
          key: `travel-${index}`,
          origin: place,
          destination: places[index + 1],
          route: place.route || null, // 預設最佳路線
        });
      }
    });
  }

  return itineraryWithRoutes;
});

// **當使用者選擇新路線時更新**
const updateRoute = (index, date, newRoute) => {
  computedItinerary.value[date][index].route = newRoute;
};

// 初始化行程資料
const initItinerary = () => {
  const storedData = JSON.parse(localStorage.getItem("itineraryData"));

  if (storedData && storedData.startDate && storedData.endDate) {
    itineraryTitle.value = storedData.name || "無行程名稱";
    dateRange.value = getDateRange(storedData.startDate, storedData.endDate);

    // 初始化每個日期對應的行程
    dateRange.value.forEach((date) => {
      const formattedDate = formatDate(date);
      if (!placeStore.itinerariesByDate[formattedDate]) {
        placeStore.itinerariesByDate[formattedDate] = [];
      }
    });

    // 預設選中第一天
    selectedTab.value = formatDate(dateRange.value[0]);
  }
};

// 根據從 localStorage 讀取的日期初始化行程
onMounted(() => {
  initItinerary();
});

// 監聽 selectedTab 的變化，確保在變更後能夠更新相應的行程顯示
watch(selectedTab, (newTab) => {
  if (newTab) {
    placeStore.setSelectedDate(newTab); // 更新 Pinia 中的 selectedDate
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

/* 日期分頁按鈕的樣式 */
.date-tabs button {
  padding: 10px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  margin-right: 5px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s; /* 讓顏色變化過渡 */
}

.date-tabs button.active {
  background-color: #007bff; /* 設定選中的日期顏色 */
  color: white; /* 文字顏色為白色 */
  font-weight: bold; /* 強調字體 */
}

/* 當按鈕被懸停時的效果 */
.date-tabs button:hover {
  background-color: #0056b3; /* 懸停時的背景顏色 */
  color: white; /* 懸停時文字顏色 */
}
</style>
