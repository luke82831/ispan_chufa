<template>
  <div>
    <h2>{{ itineraryTitle }}</h2>
    <p>{{ formattedStartDate }} - {{ formattedEndDate }}</p>

    <div class="date-tabs">
      <button class="arrow-button" @click="changeDate('prev')" :disabled="isFirstDay">
        &lt;
      </button>

      <button
        v-for="(date, index) in dateRange"
        :key="index"
        :class="{ active: selectedTab === formatDate(date) }"
        @click="updateSelectedDate(date)"
      >
        {{ formatDate(date) }}
      </button>

      <button v-if="isLastDay" @click="addOneMoreDay" class="add-day-btn">＋</button>

      <button class="arrow-button" @click="changeDate('next')" :disabled="isLastDay">
        &gt;
      </button>
    </div>

    <div v-for="(date, index) in dateRange" :key="'item-' + index">
      <div v-show="selectedTab === formatDate(date)" class="itinerary-content">
        <VueDraggableNext
          v-model="placeStore.itinerariesByDate[formatDate(date)]"
          item-key="key"
          @update:modelValue="handleDragUpdate"
        >
          <div
            v-for="(item, index) in computedItinerary[formatDate(date)]"
            :key="item.key"
          >
            <div v-if="item.type === 'place'" class="itinerary-item">
              <span class="itinerary-name">{{ item.displayName }}</span>
              <span class="itinerary-address">{{ item.formattedAddress }}</span>
            </div>
            <div v-if="item.travel">
              <div v-if="item.travel.travelTime">
                <p>路線時間: {{ item.travel.travelTime }} 分鐘</p>
              </div>
            </div>
          </div>
        </VueDraggableNext>
      </div>
    </div>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/PlaceStore";
import { ref, onMounted, watch, computed } from "vue";
import { VueDraggableNext } from "vue-draggable-next";

const placeStore = usePlaceStore();

const selectedTab = ref("");
const dateRange = ref([]);
const itineraryTitle = ref("");

const getDateRange = (startDate, endDate) => {
  const dates = [];
  let currentDate = new Date(startDate);
  while (currentDate <= new Date(endDate)) {
    dates.push(new Date(currentDate));
    currentDate.setDate(currentDate.getDate() + 1);
  }
  return dates;
};

const formatDate = (date) =>
  date.toLocaleDateString("zh-TW", { month: "numeric", day: "numeric" });

const updateSelectedDate = (date) => {
  selectedTab.value = formatDate(date);
  placeStore.setSelectedDate(selectedTab.value);
};

const changeDate = (direction) => {
  const currentIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedTab.value
  );
  if (direction === "prev" && currentIndex > 0)
    selectedTab.value = formatDate(dateRange.value[currentIndex - 1]);
  if (direction === "next" && currentIndex < dateRange.value.length - 1)
    selectedTab.value = formatDate(dateRange.value[currentIndex + 1]);
};

const addOneMoreDay = () => {
  const newDate = new Date(dateRange.value[dateRange.value.length - 1]);
  newDate.setDate(newDate.getDate() + 1);
  dateRange.value.push(newDate);
  const formattedDate = formatDate(newDate);
  placeStore.itinerariesByDate[formattedDate] =
    placeStore.itinerariesByDate[formattedDate] || [];
  selectedTab.value = formattedDate;
};

const isDayAtEdge = (isStart) => {
  const currentIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedTab.value
  );
  return isStart ? currentIndex === 0 : currentIndex === dateRange.value.length - 1;
};

const isFirstDay = computed(() => isDayAtEdge(true));
const isLastDay = computed(() => isDayAtEdge(false));

const formattedStartDate = computed(
  () => dateRange.value[0]?.toLocaleDateString("zh-TW") || ""
);
const formattedEndDate = computed(
  () => dateRange.value[dateRange.value.length - 1]?.toLocaleDateString("zh-TW") || ""
);

const computedItinerary = computed(() => {
  const itinerary = {};
  for (const date in placeStore.itinerariesByDate) {
    itinerary[date] = placeStore.itinerariesByDate[date].map((place, index, arr) => {
      const result = { ...place, type: "place" };
      if (index < arr.length - 1) {
        const nextPlace = arr[index + 1];
        result.travel = {
          type: "travel",
          key: `travel-${index}`,
          origin: place,
          destination: nextPlace,
          travelTime: calculateTravelTime(place, nextPlace),
        };
      }
      return result;
    });
  }
  return itinerary;
});

const calculateTravelTime = (origin, destination) => {
  const distanceInMinutes = 30; // 固定30分鐘
  return distanceInMinutes;
};

const initItinerary = () => {
  const storedData = JSON.parse(localStorage.getItem("itineraryData")) || {};
  itineraryTitle.value = storedData.name || "無行程名稱";
  dateRange.value = getDateRange(storedData.startDate, storedData.endDate);
  dateRange.value.forEach((date) => {
    const formattedDate = formatDate(date);
    placeStore.itinerariesByDate[formattedDate] =
      placeStore.itinerariesByDate[formattedDate] || [];
  });
  selectedTab.value = formatDate(dateRange.value[0]);
};

const handleDragUpdate = (newValue) => {
  newValue.forEach((item) => {
    if (item.type === "travel" && !item.travel) {
      item.travel = {};
      item.travel.travelTime = calculateTravelTime(item.origin, item.destination);
    }
  });
  placeStore.itinerariesByDate[selectedTab.value] = newValue;
};

onMounted(initItinerary);

watch(selectedTab, (newTab) => {
  placeStore.setSelectedDate(newTab);
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
