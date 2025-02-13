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
const departureTime = computed(() => itineraryStore.getStartTime(props.date));

// **計算每個地點的到達與離開時間**
const computedItinerary = computed(() => {
  if (!departureTime.value || !props.itinerary.length) return [];

  let [year, month, day] = props.date.split("-").map(Number);
  let [hours, minutes] = departureTime.value.split(":").map(Number);
  let baseTime = new Date(year, month - 1, day, hours, minutes);
  let currentTime = new Date(baseTime);

  // console.log("🕒 原始 baseTime:", baseTime.toLocaleString());

  // 讀取 store 中的行車時間與停留時間
  const routeTimes = itineraryStore.routeTimes[props.date] || {};
  const stayTimes = itineraryStore.stayDurations[props.date] || {};

  let itineraryWithTimes = [];

  props.itinerary.forEach((place, index) => {
    // 行車時間：若 index 為 0 則沒有行車時間，否則使用前一個地點後的行車時間
    let travelTime = index > 0 ? routeTimes[index - 1] || 0 : 0;
    // 停留時間：直接從 store 取對應 index 的停留時間 (預設為 0)
    let stayTime = stayTimes[index] ?? 0;

    console.log(`📌 檢查行程 index ${index}:`, { 原始停留時間: stayTime });

    let startTime;
    if (index === 0) {
      // ✅ 第 1 個地點，直接使用出發時間
      startTime = new Date(currentTime.getTime());
    } else {
      // ✅ 其他地點：上一個地點的 `endTime` + 行車時間
      startTime = new Date(itineraryWithTimes[index - 1].endTime);
      startTime.setMinutes(startTime.getMinutes() + travelTime);
    }

    let endTime = new Date(startTime.getTime());
    endTime.setMinutes(endTime.getMinutes() + stayTime);

    itineraryWithTimes.push({
      ...place,
      startTime,
      endTime,
    });

    // console.log(
    //   `📌 地點 ${index}: ${startTime.toLocaleString()} - ${endTime.toLocaleString()}`
    // );
  });

  // ✅ 計算完畢後，將最後一個地點的 `endTime` 存入 `itineraryStore`
  if (itineraryWithTimes.length > 0) {
    const lastEndTime =
      itineraryWithTimes[itineraryWithTimes.length - 1].endTime;
    itineraryStore.setEndTime(props.date, formatTime(lastEndTime));
  }

  return itineraryWithTimes;
});

// **取得對應 `index` 的地點時間**
const currentPlaceTime = computed(
  () => computedItinerary.value[props.index] || null
);

// **格式化時間 (HH:MM)**
const formatTime = (date) => {
  if (!date || !(date instanceof Date)) return "00:00:00";

  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${hours}:${minutes}`; // 確保格式為 HH:mm:ss
};
</script>
