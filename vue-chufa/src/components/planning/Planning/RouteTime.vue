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
  date: String, // ✅ 確保接收日期
  index: Number, // ✅ 統一使用 index，而不是 placeOrder
});

const itineraryStore = useItineraryStore();
const routeTime = ref(null);

// ✅ 取得當日的 `routePairs`（按 index 存取）
const routePairs = computed(() => itineraryStore.getRoutePairs(props.date));

// ✅ 直接使用 `index` 來取得對應的 `routePair`
const routePair = computed(() => {
  if (!routePairs.value) return null;
  return routePairs.value[props.index] || null;
});

// **計算路徑時間**
const calculateRouteTime = () => {
  const pair = routePair.value;
  if (!pair || !pair.origin || !pair.destination) {
    console.warn("🚨 起點或終點資訊缺失，無法計算路徑時間");
    return;
  }

  const directionsService = new google.maps.DirectionsService();
  const request = {
    origin: new google.maps.LatLng(pair.origin.lat, pair.origin.lng),
    destination: new google.maps.LatLng(pair.destination.lat, pair.destination.lng),
    travelMode: google.maps.TravelMode.DRIVING,
  };

  directionsService.route(request, (result, status) => {
    if (status === "OK") {
      const travelMinutes = Math.round(result.routes[0].legs[0].duration.value / 60);
      routeTime.value = travelMinutes;
      console.log(`✅ 計算成功：${travelMinutes} 分鐘，正在存入 Pinia`);
      itineraryStore.setRouteTime(props.date, props.index, travelMinutes); // ✅ 存入 Pinia
    } else {
      console.error("❌ 無法計算路徑時間:", status);
      routeTime.value = null;
    }
  });
};

// **監聽 `routePair` 變更，自動重新計算**
watch(
  routePair,
  (newVal) => {
    if (newVal?.origin && newVal?.destination) {
      // console.log("✅ 觸發計算，開始 calculateRouteTime()");
      calculateRouteTime();
    } else {
      console.warn("⚠️ routePair 資料不完整，無法計算");
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped></style>
