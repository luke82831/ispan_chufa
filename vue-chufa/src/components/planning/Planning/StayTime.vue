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
  departureTime: String, // 出發時間 "HH:MM"
  itinerary: Array, // 當天行程列表
  stayDurations: Object, // 停留時間
  index: Number, // 當前地點的索引
});

const itineraryStore = useItineraryStore();

// **計算每個地點的到達與離開時間**
const computedItinerary = computed(() => {
  if (!props.departureTime || !props.itinerary.length) return [];

  let currentTime = new Date(
    Date.UTC(2023, 0, 1, ...props.departureTime.split(":"))
  );
  let itineraryWithTimes = [];

  const routeTimes = itineraryStore.routeTimes[props.date] || {}; // 取得行車時間

  props.itinerary.forEach((place, index) => {
    let travelTime = index > 0 ? routeTimes[index - 1] || 0 : 0; // 取得行車時間
    let stayTime = props.stayDurations[place.id] || 0; // 停留時間

    // 第二個地點開始才加上 `travelTime`
    if (index > 0) {
      currentTime.setMinutes(currentTime.getMinutes() + travelTime);
    }

    let startTime = new Date(currentTime);

    // 加上 `stayTime`
    currentTime.setMinutes(currentTime.getMinutes() + stayTime);
    let endTime = new Date(currentTime);

    itineraryWithTimes.push({
      ...place,
      startTime,
      endTime,
    });
  });

  return itineraryWithTimes;
});

// 取得對應 `index` 的地點時間
const currentPlaceTime = computed(() => {
  return computedItinerary.value[props.index] || null;
});

// **格式化時間 (HH:MM)**
const formatTime = (date) => {
  return date.toISOString().substr(11, 5); // 轉成 "HH:MM"
};
</script>
