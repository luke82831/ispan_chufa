<template>
  <div v-if="currentPlaceTime" class="text-gray-500">
    🕒 {{ formatTime(currentPlaceTime.startTime) }} -
    {{ formatTime(currentPlaceTime.endTime) }}
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";

const props = defineProps({
  date: String, // 日期
  itinerary: Array, // 當天行程列表
  stayDurations: Object, // 停留時間
  index: Number, // 當前地點的索引
});

const itineraryStore = useItineraryStore();

// **取得當天出發時間**
const departureTime = computed(() => {
  return itineraryStore.getStartTime(props.date);
});

const stayDurationsReactive = computed(() => {
  return { ...props.stayDurations }; // 確保它是一個新的物件
});

// **計算每個地點的到達與離開時間**
const computedItinerary = computed(() => {
  if (!departureTime.value || !props.itinerary.length) return [];

  let baseTime = new Date(
    Date.UTC(2023, 0, 1, ...departureTime.value.split(":"))
  );

  let itineraryWithTimes = [];
  const routeTimes = itineraryStore.routeTimes[props.date] || {}; // 取得行車時間

  props.itinerary.forEach((place, index) => {
    let travelTime = index > 0 ? routeTimes[index - 1] || 0 : 0; // 取得行車時間
    let stayTime = stayDurationsReactive.value?.[place.id] ?? 0; // 改用響應式的 stayDurations

    let currentTime = new Date(baseTime); // 確保 `currentTime` 是獨立的

    if (index > 0) {
      currentTime.setMinutes(currentTime.getMinutes() + travelTime);
    }

    let startTime = new Date(currentTime);
    currentTime.setMinutes(currentTime.getMinutes() + stayTime);
    let endTime = new Date(currentTime);

    itineraryWithTimes.push({
      ...place,
      startTime,
      endTime,
    });

    baseTime = new Date(currentTime); // 確保下一個地點基於這個時間計算
  });

  return itineraryWithTimes;
});

// **取得對應 `index` 的地點時間**
const currentPlaceTime = computed(() => {
  return computedItinerary.value[props.index] || null;
});

// **格式化時間 (HH:MM)**
const formatTime = (date) => {
  return date.toISOString().substr(11, 5); // 轉成 "HH:MM"
};
</script>
