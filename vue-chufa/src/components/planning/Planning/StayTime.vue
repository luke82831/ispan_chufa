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
          🕒 {{ formatTime(place.startTime) }} - {{ formatTime(place.endTime) }}
        </p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";
import { usePlaceStore } from "@/stores/PlaceStore";

const props = defineProps({
  departureTime: String, // "HH:MM" 格式
  itinerary: Array, // 當天行程列表
  stayDurations: Object, // 每個地點的停留時間
});

const placeStore = usePlaceStore();

// 計算包含時間資訊的行程
const computedItinerary = computed(() => {
  let currentTime = new Date(
    Date.UTC(2023, 0, 1, ...props.departureTime.split(":"))
  );
  let itineraryWithTimes = [];

  props.itinerary.forEach((place, index) => {
    let stayTime = props.stayDurations[place.id] || 0;
    let travelTime = 0;

    if (index > 0) {
      let prevPlaceId = props.itinerary[index - 1].id;
      travelTime = placeStore.routePairs[prevPlaceId]?.[place.id] || 30; // 預設30分鐘
    }

    // 計算到達與離開時間
    let startTime = new Date(currentTime);
    currentTime.setMinutes(currentTime.getMinutes() + stayTime);
    let endTime = new Date(currentTime);

    itineraryWithTimes.push({
      ...place,
      startTime,
      endTime,
    });

    // 更新當前時間，加上行車時間
    currentTime.setMinutes(currentTime.getMinutes() + travelTime);
  });

  return itineraryWithTimes;
});

// 格式化時間 (HH:MM)
const formatTime = (date) => {
  return date.toISOString().substr(11, 5); // 轉成 "HH:MM"
};
</script>
