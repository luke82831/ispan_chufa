<template>

    <!-- 凍結視窗 (上半部) -->
    <div class="fixed-window">
      <div class="trip-name">{{ itinerary.name }}</div>
      <div class="trip-dates">{{ formattedStartDate }} - {{ formattedEndDate }}</div>
      <div class="add-day-btn">
        <v-btn @click="addDay" color="primary">新增一天</v-btn>
      </div>
    </div>

    <!-- 可滾動的行程細節 (下半部) -->
    <div class="scrollable-content">
      <!-- 向左、向右切換按鈕 -->
      <div class="tab-navigation">
        <v-btn @click="navigateLeft" color="primary" :disabled="currentDayIndex === 0">
          <span class="arrow">&lt;</span>
        </v-btn>

        <!-- 行程日期 Tabs -->
        <v-tabs v-model="currentDayIndex" class="tabs">
          <v-tab v-for="(day, index) in itinerary.days" :key="index">
            {{ formatDate(day.date) }} 第{{ index + 1 }}天
          </v-tab>
        </v-tabs>

        <v-btn
          @click="navigateRight"
          color="primary"
          :disabled="currentDayIndex === itinerary.days.length - 1"
        >
          <span class="arrow">&gt;</span>
        </v-btn>
      </div>

      <!-- 行程日期內容 -->
      <div class="day-content">
        <!-- 只顯示當前選中的日期內容 -->
        <div
          v-if="itinerary.days.length > 0"
          class="day-item"
        >
          <ul>
            <li v-for="location in itinerary.days[currentDayIndex].locations" :key="location.id">
              {{ location.name }}
            </li>
          </ul>
        </div>
      </div>
    </div>
</template>

<script>
import { defineComponent, reactive, ref, computed, onMounted } from "vue";
import { VSelect, VBtn, VTabs, VTab } from "vuetify/components";

export default defineComponent({
  components: {
    VSelect,
    VBtn,
    VTabs,
    VTab,
  },
  setup() {
    const itinerary = reactive({
      name: "", // 行程名稱
      startDate: "", // 開始日期
      endDate: "", // 結束日期
      days: [], // 每天行程
    });

    const currentDayIndex = ref(0); // 當前選中的天數索引

    const formatDate = (date) => {
      if (!date) return "";
      const options = { month: "2-digit", day: "2-digit" }; // 只顯示月和日
      return new Date(date).toLocaleDateString("zh-TW", options);
    };

    const generateDays = () => {
      if (!itinerary.startDate || !itinerary.endDate) return;

      const startDate = new Date(itinerary.startDate);
      const endDate = new Date(itinerary.endDate);
      itinerary.days = []; // 清空行程天數

      let currentDate = new Date(startDate);
      while (currentDate <= endDate) {
        itinerary.days.push({
          date: currentDate.toISOString().split("T")[0],
          locations: [], // 每天的地點資料
        });
        currentDate.setDate(currentDate.getDate() + 1); // 下一天
      }
    };

    const addDay = () => {
      if (!itinerary.startDate) {
        alert("請先設定行程開始日期！");
        return;
      }

      const lastDayIndex = itinerary.days.length;
      const newDayDate = new Date(itinerary.startDate);
      newDayDate.setDate(newDayDate.getDate() + lastDayIndex);

      itinerary.endDate = newDayDate.toISOString().split("T")[0];
      itinerary.days.push({
        date: newDayDate.toISOString().split("T")[0],
        locations: [], // 新增的天數
      });

      // 自動切換到新增的那一天
      currentDayIndex.value = lastDayIndex;
    };

    const navigateLeft = () => {
      if (currentDayIndex.value > 0) {
        currentDayIndex.value--;
      }
    };

    const navigateRight = () => {
      if (currentDayIndex.value < itinerary.days.length - 1) {
        currentDayIndex.value++;
      }
    };

    const formattedStartDate = computed(() => formatDate(itinerary.startDate));
    const formattedEndDate = computed(() => formatDate(itinerary.endDate));

    const loadItineraryData = () => {
      const storedItinerary = localStorage.getItem("itineraryData");
      if (storedItinerary) {
        const data = JSON.parse(storedItinerary);
        itinerary.name = data.name;
        itinerary.startDate = data.startDate;
        itinerary.endDate = data.endDate;
        itinerary.days = data.days || [];
      }
    };

    const initializeItinerary = () => {
      if (itinerary.startDate && itinerary.endDate) {
        generateDays();
      }
    };

    onMounted(() => {
      loadItineraryData();
      initializeItinerary();
    });

    return {
      itinerary,
      currentDayIndex,
      formattedStartDate,
      formattedEndDate,
      formatDate,
      addDay,
      navigateLeft,
      navigateRight,
    };
  },
});
</script>

<style scoped>
.fixed-window {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
  padding: 20px;
  border-bottom: 1px solid #ddd;
}

.trip-name {
  font-size: 24px;
  font-weight: bold;
}

.trip-dates {
  font-size: 18px;
}

.add-day-btn {
  margin-left: auto;
}

.scrollable-content {
  max-height: 500px;
  overflow-y: auto;
}

.tab-navigation {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 10px;
}

.tabs {
  flex: 1;
  overflow-x: auto;
  padding: 0 !important;
  margin: 0 !important;
}

.arrow {
  font-size: 10px;
  line-height: 1;
}

.v-btn {
  min-width: 32px;
  padding: 4px;
  margin-right: 8px; /* 增加按鈕之間的右邊距 */
}

.day-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.day-item {
  display: block; /* 顯示當前選中的天 */
  width: 100%; /* 確保每個框框占滿屏幕 */
  padding: 30px; /* 增大內邊距，使內容區域更大 */
  margin: 15px 0; /* 增加上下邊距，讓區塊之間的空隙更大 */
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 25px; /* 增加字體大小 */
  line-height: 1.5; /* 調整行高，使文字更清晰 */
}
</style>
