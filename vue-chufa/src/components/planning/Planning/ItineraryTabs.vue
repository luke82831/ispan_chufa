<template>
  <div v-if="scheduleStore.currentSchedule">
    <div class="header">
      <button class="back-button" @click="goBack">⬅ 返回</button>
      <h2 v-if="!isEditing">{{ scheduleStore.currentSchedule.tripName }}</h2>
      <input
        v-if="isEditing"
        v-model="newTitle"
        @blur="saveTitle"
        class="edit-input"
        type="text"
      />
      <button v-if="!isEditing" class="button" @click="editTitle">✏️</button>
      <button v-if="isEditing" class="button" @click="saveTitle">儲存</button>
    </div>

    <!-- 行程封面 -->
    <div v-if="coverPhotoUrl" class="cover-photo-container">
      <img
        :src="`data:image/jpeg;base64,${scheduleStore.currentSchedule.coverPhoto}`"
        alt="封面照片"
        class="cover-photo"
      />

      <!-- 固定在圖片右下角的按鈕 -->
      <!-- <button @click="toggleExpanded" class="toggle-button fixed-button">
        {{ isExpanded ? "收合行程" : "查看完整行程" }}
      </button> -->

      <div class="list-container">
        <!-- 傳遞 isExpanded 狀態給子組件 -->
        <ItineraryList :isExpanded="isExpanded" @close="isExpanded = false" />
      </div>
    </div>

    <!-- 行程日期範圍 -->
    <h2>
      行程日期: {{ scheduleStore.currentSchedule.startDate }} -
      {{ scheduleStore.currentSchedule.endDate }}
    </h2>

    <div class="date-tabs-container">
      <!-- 總覽按鈕 -->
      <button
        class="overview-button"
        :class="{ active: selectedDate === 'overview' }"
        @click="showOverview"
      >
        總覽
      </button>

      <!-- 左側箭頭 -->
      <button class="arrow-button arrow-left" @click="scrollTabs('left')">&lt;</button>

      <!-- 日期滾動區 -->
      <div class="date-tabs" ref="tabsContainer">
        <button
          v-for="(date, index) in dateRange"
          :key="index"
          :class="{ active: selectedDate === formatDate(date) }"
          @click="updateSelectedDate(date)"
        >
          {{ formatDate(date) }}
        </button>
      </div>

      <!-- + 按鈕（新增一天） -->
      <button class="add-day-btn" @click="addOneMoreDay">＋</button>

      <!-- 右側箭頭 -->
      <button class="arrow-button arrow-right" @click="scrollTabs('right')">&gt;</button>
    </div>

    <!-- 根據選擇顯示對應的組件 -->
    <ItineraryList v-if="selectedDate === 'overview'" />
    <PlanningDay v-else :selectedDate="selectedDate" />
  </div>

  <!-- 如果 `currentSchedule` 還沒載入，顯示 Loading -->
  <div v-else class="loading">載入中...</div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useScheduleStore } from "@/stores/ScheduleStore";
import PlanningDay from "./PlanningDay.vue";
import ItineraryList from "@/components/planning/Itinerary/ItineraryList.vue";

const router = useRouter();
const route = useRoute();
const scheduleStore = useScheduleStore();

// 從 URL 取得行程 ID
const tripId = route.params.tripId;
const selectedDate = ref("overview"); // 預設為總覽頁
const isEditing = ref(false);
const newTitle = ref("");
const isExpanded = ref(false);
const tabsContainer = ref(null);

// 滾動日期選單
const scrollTabs = (direction) => {
  if (!tabsContainer.value) return;
  const scrollAmount = 150; // 每次滾動 150px
  tabsContainer.value.scrollLeft += direction === "left" ? -scrollAmount : scrollAmount;
};

// 切換總覽頁
const showOverview = () => {
  console.log("📜 切換到總覽頁");
  selectedDate.value = "overview";
  scheduleStore.setSelectedDate("overview");
};

// 更新選擇的日期
const updateSelectedDate = (date) => {
  if (date === "overview") {
    showOverview();
    return;
  }
  const formatted = formatDate(date);
  console.log("📅 選擇的行程日期:", formatted);
  selectedDate.value = formatted;
  scheduleStore.setSelectedDate(formatted);
};

// 取得封面圖片
const coverPhotoUrl = computed(() => scheduleStore.currentSchedule?.coverPhoto || null);

// 取得開始與結束日期
const startDate = computed(() =>
  scheduleStore.currentSchedule?.startDate
    ? new Date(scheduleStore.currentSchedule.startDate)
    : null
);
const endDate = computed(() =>
  scheduleStore.currentSchedule?.endDate
    ? new Date(scheduleStore.currentSchedule.endDate)
    : null
);

// 取得日期範圍
const dateRange = computed(() => {
  if (!startDate.value || !endDate.value) return [];
  const dates = [];
  let currentDate = new Date(startDate.value);
  while (currentDate <= endDate.value) {
    dates.push(new Date(currentDate));
    currentDate.setDate(currentDate.getDate() + 1);
  }
  return dates;
});

// 格式化日期
const formatDate = (date) =>
  date instanceof Date && !isNaN(date)
    ? date.toLocaleDateString("zh-TW", { month: "numeric", day: "numeric" })
    : "";

// 切換日期
const changeDate = (direction) => {
  const currentIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedDate.value
  );
  if (direction === "prev" && currentIndex > 0) {
    updateSelectedDate(dateRange.value[currentIndex - 1]);
  } else if (direction === "next" && currentIndex < dateRange.value.length - 1) {
    updateSelectedDate(dateRange.value[currentIndex + 1]);
  }
};

// 監聽日期範圍變化，設定預設選擇日期
watch(
  dateRange,
  (newDates) => {
    if (newDates.length > 0 && selectedDate.value === "overview") return;
    if (newDates.length > 0 && selectedDate.value === "") {
      updateSelectedDate(newDates[0]);
    }
  },
  { immediate: true }
);

// 新增一天
const addOneMoreDay = async () => {
  if (!endDate.value) return;

  const newDate = new Date(endDate.value);
  newDate.setDate(newDate.getDate() + 1);
  const formattedDate = newDate.toISOString().split("T")[0];

  console.log("🗓️ 新增一天:", formattedDate);

  // 更新行程結束日期
  scheduleStore.currentSchedule.endDate = formattedDate;

  try {
    console.log("🔄 更新 `endDate`:", formattedDate);
    await scheduleStore.updateScheduleEndDate(tripId, formattedDate);
    console.log("✅ `endDate` 更新成功");

    // 成功後才更新選擇日期
    updateSelectedDate(newDate);
  } catch (error) {
    console.error("❌ 更新行程結束日期失敗:", error);
  }
};

// 監聽行程名稱變更，確保標題更新
watch(
  () => scheduleStore.currentSchedule?.tripName,
  (newName) => {
    newTitle.value = newName || "";
  },
  { immediate: true }
);

// 編輯標題
const editTitle = () => {
  isEditing.value = true;
};

// 儲存標題
const saveTitle = async () => {
  if (scheduleStore.currentSchedule) {
    try {
      await scheduleStore.updateScheduleTitle(
        scheduleStore.currentSchedule.tripId,
        newTitle.value
      );
      console.log("行程標題已更新");
    } catch (error) {
      console.error("行程標題更新失敗", error);
    }
  }
  isEditing.value = false;
};

// 初始化行程數據
onMounted(async () => {
  if (tripId) {
    await scheduleStore.fetchScheduleById(tripId);
  }
});

// 返回行程列表
const goBack = () => {
  router.push("/myitineraries");
};
</script>

<style scoped>
/* 全局字體設定 */
* {
  font-family: "Noto Sans TC", sans-serif; /* 使用台灣常見的無襯線字體 */
  font-size: 1.2rem; /* 統一字體大小 */
}

/* 頁面標題與返回按鈕 */
.header {
  display: flex;
  align-items: center;
}

/* 返回按鈕 */
.back-button {
  background: white;
  color: #2973b2;
  font-size: 1.2rem; /* 調大字體 */
  padding: 10px 15px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-right: 20px;
}
.back-button:hover {
  background: #2973b2;
  color: white;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}

/* 編輯行程名稱輸入框 */
.edit-input {
  padding: 8px;
  margin-left: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: auto;
}

/* 編輯按鈕 */
.edit-button {
  background: white;
  border: 2px solid #ccc;
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}
.edit-button:hover {
  background: #007bff;
  color: white;
  border-color: #007bff;
  box-shadow: 0 4px 10px rgba(0, 123, 255, 0.3);
}
.edit-button svg {
  width: 24px;
  height: 24px;
  fill: currentColor;
}

/* 通用按鈕樣式 */
button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.4rem; /* 統一按鈕字體大小 */
  margin-left: 10px;
}
button:hover {
  color: #007bff;
}

/* 日期選擇區 */
.date-tabs-container {
  position: relative;
  width: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  border-top: #f3f3f3 solid;
  border-bottom: #f3f3f3 solid;
}

.date-tabs {
  display: flex;
  align-items: center; /* 確保所有項目垂直置中 */
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  white-space: nowrap;
  overflow-x: auto; /* 啟用水平滾動條 */
  scrollbar-width: thin; /* 適用於 Firefox */
  -ms-overflow-style: auto; /* 適用於 Edge */
}

/* 隱藏滾動條（適用於 Webkit 瀏覽器） */
.date-tabs::-webkit-scrollbar {
  display: none;
}

/* 日期按鈕 */
.date-tabs button {
  border: none;
  padding: 10px 20px;
  min-width: 60px;
  height: 50px; /* 設定固定高度，確保對齊 */
  font-size: 1.2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  display: flex; /* 讓內容可用 flex 排列 */
  align-items: center; /* 垂直置中 */
  justify-content: center; /* 水平置中 */
}

.date-tabs button.active {
  background-color: #2973b2;
  color: white;
  font-weight: bold;
}

/* 左右箭頭按鈕 */
.arrow-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.5rem;
  opacity: 0; /* 預設隱藏 */
  transition: opacity 0.3s ease-in-out;
}

/* 左側按鈕 */
.arrow-left {
  left: 0;
}

/* 右側按鈕 */
.arrow-right {
  right: 50px; /* 避免與 + 按鈕重疊 */
}

/* + 按鈕樣式 */
.add-day-btn {
  font-size: 1.5rem;
  font-weight: bold;
  color: black;
  padding: 8px 12px;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 60px; /* 確保與其他按鈕一致 */
  height: 50px; /* 確保與其他按鈕一致 */
  margin-left: 10px; /* 避免太靠近最後一天 */
}

.add-day-btn:hover {
  background-color: #2973b2;
  color: white;
}

/* 滑鼠移動到 .date-tabs-container 才顯示箭頭 */
.date-tabs-container:hover .arrow-button {
  opacity: 1;
}

/* 封面圖片 */
.cover-photo-container {
  width: 100%;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  margin-top: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}
.cover-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 行程列表 */
.list-container {
  position: relative;
  padding: 20px;
}

.cover-photo-container {
  position: relative; /* 設定相對定位 */
  display: inline-block;
  width: 100%;
}

.fixed-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: #2973b2;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.fixed-button:hover {
  background-color: #20527e;
  color: white;
}

.overview-button {
  white-space: nowrap; /* ✅ 防止文字換行 */
  display: inline-flex; /* ✅ 讓內容橫向排列 */
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  background-color: #ff6b6b; /* 紅色按鈕 */
  color: white;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.overview-button:hover {
  background-color: #ff4757;
}

.overview-button.active {
  background-color: #e84118; /* 深紅色表示選中狀態 */
}
</style>
