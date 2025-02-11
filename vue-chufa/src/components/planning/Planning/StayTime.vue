<template>
  <div v-if="currentPlaceTime" class="text-gray-500">
    🕒 {{ formatTime(currentPlaceTime.startTime) }} -
    {{ formatTime(currentPlaceTime.endTime) }}
  </div>
</template>

<script setup>
import { computed, watch } from "vue";
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

// **確保 `stayDurations` 直接使用 props**
const stayDurationsReactive = props.stayDurations;

// **計算每個地點的到達與離開時間**
const computedItinerary = computed(() => {
  if (!departureTime.value || !props.itinerary.length) return [];

  let [year, month, day] = props.date.split("-").map(Number);
  let [hours, minutes] = departureTime.value.split(":").map(Number);
  let baseTime = new Date(year, month - 1, day, hours, minutes);
  let currentTime = new Date(baseTime);

  console.log("🕒 原始 baseTime:", baseTime.toLocaleString());

  let itineraryWithTimes = [];
  const routeTimes = itineraryStore.routeTimes[props.date] || {};
  const stayTimes = itineraryStore.stayDurations[props.date] || {};

  props.itinerary.forEach((_, index) => {
    let travelTime = Number(routeTimes[index]) || 0;
    let stayTime = Number(stayTimes[index]) || 0;

    console.log(
      `🛣 地點 ${index} 行車時間: ${travelTime} 分鐘, 停留時間: ${stayTime} 分鐘`
    );

    // ✅ **index 0 的 `startTime` 來自出發時間**
    let startTime;
    if (index === 0) {
      startTime = new Date(currentTime.getTime()); // ✅ 初始出發時間
    } else {
      // ✅ **後續地點的 `startTime` 應該來自上一個 `endTime`**
      startTime = new Date(itineraryWithTimes[index - 1].endTime);
    }

    // ✅ **確保 `endTime = startTime + 行車時間 + 停留時間`**
    let endTime = new Date(startTime.getTime());
    endTime.setMinutes(endTime.getMinutes() + travelTime + stayTime);

    console.log(
      `📌 地點 ${index}: ${startTime.toLocaleString()} - ${endTime.toLocaleString()}`
    );

    itineraryWithTimes.push({
      startTime: startTime, // ✅ 確保 Vue 讀取的是 `Date` 物件
      endTime: endTime,
    });
  });

  return itineraryWithTimes;
});

// **取得對應 `index` 的地點時間**
const currentPlaceTime = computed(
  () => computedItinerary.value[props.index] || null
);

watch(
  () => computedItinerary.value,
  (newVal) => {
    console.log("📌 computedItinerary 變更:", newVal);
  },
  { deep: true }
);

watch(
  () => itineraryStore.routeTimes[props.date],
  (newVal) => {
    console.log("🚗 行車時間變更:", newVal);
  },
  { deep: true }
);

watch(
  () => stayDurationsReactive,
  (newVal) => {
    console.log("⏳ 停留時間變更:", newVal);
  },
  { deep: true }
);

watch(
  () => currentPlaceTime.value,
  (newVal) => {
    console.log("⏰ currentPlaceTime 更新:", JSON.stringify(newVal, null, 2));
  }
);

// **格式化時間 (HH:MM)**
const formatTime = (date) => {
  if (!date) return "時間未設定";
  return new Date(date).toLocaleTimeString("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
  });
};
</script>
