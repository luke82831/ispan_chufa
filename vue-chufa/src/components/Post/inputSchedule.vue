<template>
<div class="itineraries-container">
<h1 class="itineraries-title">我的行程</h1>

<!-- 行程列表 -->
<div v-if="loading" class="loading-text">載入中...</div>
<div v-else-if="schedules.length === 0" class="no-schedules">
目前沒有行程
</div>
<div v-else class="itineraries-grid">
<div
v-for="schedule in paginatedSchedules"
:key="schedule.tripId"
class="itinerary-card"
@click="goToPlanningPage(schedule)"
>
<div class="itinerary-image-container">
<img
:src="getImageSource(schedule.coverPhoto)"
alt="行程封面"
class="itinerary-image"
/>
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
<button
@click="changePage('next')"
:disabled="currentPage === totalPages"
>
下一頁 &gt;
</button>
</div>
</div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useScheduleStore } from "@/stores/ScheduleStore";
import eventBus from '@/eventBus';

const scheduleStore = useScheduleStore();
const loading = ref(true);
const schedules = computed(() => scheduleStore.schedules);

const fetchSchedules = async () => {
await scheduleStore.fetchSchedules();
loading.value = false;
};

// 分頁設定
const currentPage = ref(1); // 當前頁數
const itemsPerPage = 6; // 每頁顯示 6 個行程

// 計算總頁數
const totalPages = computed(() =>
Math.ceil(schedules.value.length / itemsPerPage)
);

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

// 跳轉至行程規劃頁面(改成抓行程資料)
const goToPlanningPage = (schedule) => {
    console.log("抓行程資料")
    eventBus.emit('inputSchedule',schedule);
};

onMounted(fetchSchedules);
</script>

