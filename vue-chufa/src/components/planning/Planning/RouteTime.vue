<template>
  <div class="route-time-container">
    <p v-if="routeTime !== null">🚗 {{ routeTime }} 分鐘</p>
    <p v-else>正在計算行程時間...</p>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useItineraryStore } from "@/stores/ItineraryStore";

const props = defineProps({
  date: String,
  index: Number,
});

const itineraryStore = useItineraryStore();
const routeTime = ref(null);

// **透過 itineraryStore 取得當天的行程順序**
const itineraryForDay = computed(() =>
  itineraryStore.getItineraryForDay(props.date)
);

// **根據 index 取得當前地點的起點與終點**
const routePair = computed(() => {
  const places = itineraryForDay.value;
  if (!places || places.length < 2 || props.index >= places.length - 1)
    return null;

  return {
    origin: places[props.index],
    destination: places[props.index + 1],
  };
});

// **計算路徑時間**
const calculateRouteTime = () => {
  if (
    !routePair.value ||
    !routePair.value.origin ||
    !routePair.value.destination
  ) {
    console.warn("🚨 起點或終點資訊缺失，無法計算路徑時間");
    return;
  }

  const { origin, destination } = routePair.value;

  // ✅ 修正：確保 Google Maps API 能讀取正確的經緯度格式
  const originLatLng = new google.maps.LatLng(
    origin.latitude,
    origin.longitude
  );
  const destinationLatLng = new google.maps.LatLng(
    destination.latitude,
    destination.longitude
  );

  // 確保數據有效
  if (
    typeof origin.latitude !== "number" ||
    typeof origin.longitude !== "number" ||
    typeof destination.latitude !== "number" ||
    typeof destination.longitude !== "number"
  ) {
    console.error("❌ 無效的經緯度數據:", { origin, destination });
    return;
  }

  const directionsService = new google.maps.DirectionsService();
  const request = {
    origin: originLatLng,
    destination: destinationLatLng,
    travelMode: google.maps.TravelMode.DRIVING,
  };

  directionsService.route(request, (result, status) => {
    if (status === "OK") {
      routeTime.value = Math.round(
        result.routes[0].legs[0].duration.value / 60
      );
      itineraryStore.setRouteTime(props.date, props.index, routeTime.value);
      console.log(`✅ 計算成功：${routeTime.value} 分鐘`);
    } else {
      console.error("❌ 無法計算路徑時間:", status);
      routeTime.value = null;
    }
  });
};

// **監聽行程順序變化，重新計算時間**
watch(
  () => itineraryForDay.value,
  (newVal) => {
    if (newVal && newVal.length > 1) {
      console.log("✅ 觸發計算，開始 calculateRouteTime()");
      calculateRouteTime();
    } else {
      console.warn("⚠️ 無法計算路徑，行程資料不足");
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped></style>
