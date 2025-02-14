<template>
  <!-- 只有當 isExpanded 為 true 且 currentSchedule 存在時才顯示 -->
  <div v-if="isExpanded && scheduleStore.currentSchedule" ref="popup" class="popup">
    <h2>{{ scheduleStore.currentSchedule.tripName }} 的所有行程</h2>
    <ul>
      <li v-for="event in scheduleStore.currentSchedule.events" :key="event.eventId">
        📅 {{ event.date }}
        <ul>
          <li v-for="placeId in getPlacesForEvent(event)" :key="placeId">
            📍 {{ getPlaceName(placeId) || `地點 ID: ${placeId} (載入中...)` }}
          </li>
        </ul>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { defineProps, watch, onMounted, computed } from "vue";
import { useScheduleStore } from "@/stores/ScheduleStore";
import { usePlaceStore } from "@/stores/PlaceStore";
import { useRoute } from "vue-router";

const route = useRoute();
const scheduleStore = useScheduleStore();
const placeStore = usePlaceStore();

const props = defineProps({
  isExpanded: Boolean, // 父組件傳來的開關狀態
});

// 📌 監聽 `isExpanded`，當 `true` 時自動讀取行程資料
watch(
  () => props.isExpanded,
  async (newVal) => {
    console.log("isExpanded 變更:", newVal);
    if (newVal) {
      const tripId = route.params.scheduleId;
      console.log("讀取行程 ID:", tripId);

      if (
        tripId &&
        (!scheduleStore.currentSchedule ||
          scheduleStore.currentSchedule.tripId !== tripId)
      ) {
        console.log("發送 API 請求...");
        await scheduleStore.fetchScheduleById(tripId);
      }

      // 獲取所有 placeId
      const allPlaceIds = getAllPlaceIds();
      if (allPlaceIds.length) {
        console.log("載入所有地點資料:", allPlaceIds);
        await placeStore.fetchMultiplePlaces(allPlaceIds);
      }
    }
  }
);

// 📌 當組件掛載時，確認是否已經有 `tripId`
onMounted(async () => {
  const tripId = route.params.scheduleId;
  console.log("onMounted 讀取的 tripId:", tripId);

  if (tripId) {
    await scheduleStore.fetchScheduleById(tripId);

    // 獲取所有 placeId
    const allPlaceIds = getAllPlaceIds();
    if (allPlaceIds.length) {
      console.log("載入所有地點資料:", allPlaceIds);
      await placeStore.fetchMultiplePlaces(allPlaceIds);
    }
  }
});

// 📌 取得特定 `event` 下的所有地點 ID
const getPlacesForEvent = (event) => {
  // 從 eventXPlaceBeans 獲取 placeId
  const eventPlaces = event.eventXPlaceBeans?.map((p) => p.placeId) || [];

  // 加上 event 內的 placeIds（確保不重複）
  return [...new Set([...eventPlaces, ...event.placeIds])];
};

// 📌 取得所有行程內的地點 ID（用於批次加載）
const getAllPlaceIds = () => {
  if (!scheduleStore.currentSchedule) return [];

  return [
    ...new Set(
      scheduleStore.currentSchedule.events.flatMap((event) => getPlacesForEvent(event))
    ),
  ];
};

// 📌 透過 `placeId` 取得 `placeName`
const getPlaceName = (placeId) => {
  return placeStore.getPlaceDetailById(placeId)?.placeName || null;
};
</script>

<style scoped>
.popup {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 500px;
  background: white;
  border: 1px solid #ddd;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
  padding: 20px;
  border-radius: 10px;
  z-index: 1000000;
  transition: opacity 0.3s ease, transform 0.3s ease;
  margin-left: 100px;
}
</style>
