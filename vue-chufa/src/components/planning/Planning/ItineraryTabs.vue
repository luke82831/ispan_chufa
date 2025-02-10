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
    </div>

    <!-- 行程日期範圍 -->
    <p>
      {{ scheduleStore.currentSchedule.startDate }} -
      {{ scheduleStore.currentSchedule.endDate }}
    </p>

    <!-- 日期分頁 -->
    <div class="date-tabs">
      <button
        class="arrow-button"
        @click="changeDate('prev')"
        :disabled="isFirstDay"
      >
        &lt;
      </button>

      <button
        v-for="(date, index) in dateRange"
        :key="index"
        :class="{ active: selectedDate === formatDate(date) }"
        @click="updateSelectedDate(date)"
      >
        {{ formatDate(date) }}
      </button>

      <button v-if="isLastDay" @click="addOneMoreDay" class="add-day-btn">
        ＋
      </button>

      <button
        class="arrow-button"
        @click="changeDate('next')"
        :disabled="isLastDay"
      >
        &gt;
      </button>
    </div>

    <!-- 傳遞選擇的日期到 PlanningDay 組件 -->
    <PlanningDay :selectedDate="selectedDate" />
  </div>

  <!-- 如果 `currentSchedule` 還沒載入，顯示 Loading -->
  <div v-else class="loading">載入中...</div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { useEventStore } from "@/stores/EventStore";
import PlanningDay from "./PlanningDay.vue";

const router = useRouter();
const route = useRoute();
const scheduleStore = useScheduleStore();
const eventStore = useEventStore();

// 從 URL 取得行程 ID
const tripId = route.params.tripId;
const selectedDate = ref(""); // 當前選擇的日期
const isEditing = ref(false);
const newTitle = ref("");

// **初始化行程數據**
onMounted(async () => {
  if (tripId) {
    await scheduleStore.fetchScheduleById(tripId);
  }
});

// **監聽行程名稱變更，確保標題更新**
watch(
  () => scheduleStore.currentSchedule?.tripName,
  (newName) => {
    newTitle.value = newName || "";
  },
  { immediate: true }
);

// **編輯標題**
const editTitle = () => {
  isEditing.value = true;
};

// **儲存標題**
const saveTitle = () => {
  if (scheduleStore.currentSchedule) {
    scheduleStore.currentSchedule.tripName = newTitle.value;
  }
  isEditing.value = false;
};

// **封面圖片**
const coverPhotoUrl = computed(() => {
  const coverPhoto = scheduleStore.currentSchedule?.coverPhoto;
  return coverPhoto ? coverPhoto : null;
});

// **開始與結束日期**
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

// **日期範圍**
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

// **格式化日期**
const formatDate = (date) => {
  if (!(date instanceof Date) || isNaN(date)) return "";
  return date.toLocaleDateString("zh-TW", { month: "numeric", day: "numeric" });
};

// **更新選擇的日期**
const updateSelectedDate = (date) => {
  const formatted = formatDate(date);
  console.log("📅 選擇的行程日期:", formatted);
  selectedDate.value = formatted;
  scheduleStore.setSelectedDate(formatted);
};

// **切換日期**
const changeDate = (direction) => {
  const currentIndex = dateRange.value.findIndex(
    (date) => formatDate(date) === selectedDate.value
  );
  if (direction === "prev" && currentIndex > 0) {
    updateSelectedDate(dateRange.value[currentIndex - 1]);
  } else if (
    direction === "next" &&
    currentIndex < dateRange.value.length - 1
  ) {
    updateSelectedDate(dateRange.value[currentIndex + 1]);
  }
};

// **頁面載入時設定初始選擇日期**
watch(
  dateRange,
  (newDates) => {
    if (newDates.length > 0 && !selectedDate.value) {
      updateSelectedDate(newDates[0]);
    }
  },
  { immediate: true }
);

// **新增一天**
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

    // ✅ 不需要手動更新 eventStore，因為切換日期時會自動查詢
  } catch (error) {
    console.error("❌ 更新行程結束日期失敗:", error);
  }

  updateSelectedDate(newDate); // 切換到新日期，自動觸發事件查詢
};

// **是否為第一天 / 最後一天**
const isFirstDay = computed(
  () => selectedDate.value === formatDate(dateRange.value[0])
);
const isLastDay = computed(
  () =>
    selectedDate.value ===
    formatDate(dateRange.value[dateRange.value.length - 1])
);

// **返回行程列表**
const goBack = () => {
  router.push("/myitineraries");
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
}
.back-button {
  background: white;
  border: 2px solid #007bff;
  color: #007bff;
  font-size: 1.2rem;
  padding: 8px 12px;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-right: 20px;
}
.back-button:hover {
  background: #007bff;
  color: white;
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}
.edit-input {
  padding: 5px;
  margin-left: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: auto;
}
.edit-button {
  background: white;
  border: 2px solid #ccc;
  border-radius: 50%;
  width: 40px;
  height: 40px;
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
  width: 20px;
  height: 20px;
  fill: currentColor;
}

button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
  margin-left: 10px;
}

button:hover {
  color: #007bff;
}

/* 日期分頁按鈕的樣式 */
.date-tabs button {
  padding: 10px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  margin-right: 5px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

.date-tabs button.active {
  background-color: #007bff;
  color: white;
  font-weight: bold;
}

.date-tabs button:hover {
  background-color: #0056b3;
  color: white;
}

.cover-photo-container {
  width: 100%;
  height: 280px;
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
</style>
