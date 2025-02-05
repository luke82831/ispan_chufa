<template>
  <div>
    <div class="header">
      <button class="back-button" @click="goBack">⬅ 返回</button>
      <h2 v-if="!isEditing">{{ itineraryStore.itineraryTitle }}</h2>
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

    <div v-if="coverPhotoUrl" class="cover-photo-container">
      <img :src="coverPhotoUrl" alt="Cover Photo" class="cover-photo" />
    </div>
    <p>{{ itineraryStore.startDate }} - {{ itineraryStore.endDate }}</p>

    <div class="date-tabs">
      <button class="arrow-button" @click="changeDate('prev')" :disabled="isFirstDay">
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

      <button v-if="isLastDay" @click="addOneMoreDay" class="add-day-btn">＋</button>

      <button class="arrow-button" @click="changeDate('next')" :disabled="isLastDay">
        &gt;
      </button>
    </div>

    <!-- 傳遞 selectedDate 到 PlanningDay 組件 -->
    <PlanningDay :selectedDate="selectedDate" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useItineraryStore } from "@/stores/ItineraryStore";
import PlanningDay from "./PlanningDay.vue";

const router = useRouter();

const goBack = () => {
  router.push("/myitineraries");
};

// 在此初始化 itineraryStore
const itineraryStore = useItineraryStore();
const isEditing = ref(false);
const newTitle = ref(itineraryStore.itineraryTitle);

// 進入編輯模式
const editTitle = () => {
  isEditing.value = true;
};

// 儲存新的標題
const saveTitle = () => {
  itineraryStore.itineraryTitle = newTitle.value;
  isEditing.value = false;
};

const coverPhotoUrl = computed(() => {
  const coverPhoto = itineraryStore.coverPhoto;
  if (coverPhoto && coverPhoto instanceof File) {
    return URL.createObjectURL(coverPhoto);
  }
  return null;
});

const selectedDate = ref(""); // 當前選擇的日期

const startDate = computed(() =>
  itineraryStore.startDate ? new Date(itineraryStore.startDate) : null
);
const endDate = computed(() =>
  itineraryStore.endDate ? new Date(itineraryStore.endDate) : null
);

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

const formatDate = (date) => {
  if (!(date instanceof Date) || isNaN(date)) return "";
  return date.toLocaleDateString("zh-TW", { month: "numeric", day: "numeric" });
};

const updateSelectedDate = (date) => {
  selectedDate.value = formatDate(date);
  itineraryStore.setSelectedDate(selectedDate.value);
};

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

const addOneMoreDay = () => {
  if (!endDate.value) return;

  const newDate = new Date(endDate.value);
  newDate.setDate(newDate.getDate() + 1);
  itineraryStore.endDate = newDate.toISOString().split("T")[0];

  updateSelectedDate(newDate);
};

const isFirstDay = computed(() => selectedDate.value === formatDate(dateRange.value[0]));
const isLastDay = computed(
  () => selectedDate.value === formatDate(dateRange.value[dateRange.value.length - 1])
);

watch(
  dateRange,
  (newDates) => {
    if (newDates.length > 0 && !selectedDate.value) {
      updateSelectedDate(newDates[0]);
    }
  },
  { immediate: true }
);
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
