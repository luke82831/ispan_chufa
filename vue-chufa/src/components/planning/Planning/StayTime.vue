<template>
  <div class="mt-4">
    <h3 class="text-lg font-bold">計算後的行程時間</h3>
    <ul class="mt-2">
      <li
        v-for="(place, index) in computedItinerary"
        :key="place.id"
        class="p-4 border rounded-lg shadow-md bg-gray-100"
      >
        <strong>{{ place.displayName }}</strong>
        <p>{{ place.formattedAddress }}</p>
        <p class="text-gray-600">
          🕒 {{ place.startTime }} - {{ place.endTime }}
        </p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { computed, watch, ref } from "vue";

// Props 接收來自 `planningday.vue` 的資料
const props = defineProps({
  departureTime: String,
  itinerary: Array,
});

// 計算後的行程時間
const computedItinerary = ref([]);

// 計算每個地點的時間
const calculateTimes = () => {
  let currentTime = props.departureTime; // 從出發時間開始
  computedItinerary.value = props.itinerary.map((place, index) => {
    const startTime = currentTime;

    // 計算結束時間 (開始時間 + 停留時間)
    const endTime = addMinutes(startTime, place.stayDuration);

    // 計算下一個地點的開始時間 (本地點結束時間 + 行車時間)
    currentTime = addMinutes(endTime, place.routeDuration);

    return { ...place, startTime, endTime };
  });
};

// 監聽 props 變化，自動重新計算時間
watch(() => [props.departureTime, props.itinerary], calculateTimes, {
  deep: true,
  immediate: true,
});

// **時間計算函式**
const addMinutes = (time, minutes) => {
  if (!time) return "";
  const [hours, mins] = time.split(":").map(Number);
  const date = new Date();
  date.setHours(hours, mins);
  date.setMinutes(date.getMinutes() + minutes);
  return date.toTimeString().slice(0, 5);
};
</script>
