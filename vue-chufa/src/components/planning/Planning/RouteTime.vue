<template>
  <div class="route-time-container">
    <p v-if="routeTime !== null">🚗 {{ routeTime }} 分鐘</p>
    <p v-else>正在計算行程時間...</p>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { usePlaceStore } from "@/stores/PlaceStore";

const props = defineProps({
  date: String,
  index: Number,
});

const placeStore = usePlaceStore();
const routeTime = ref(null);

// 計算路徑時間
const calculateRouteTime = () => {
  const routePair = placeStore.routePairs[props.date]?.[props.index];

  if (!routePair || !routePair.origin || !routePair.destination) {
    console.warn("🚨 起點或終點資訊缺失，無法計算路徑時間");
    return;
  }

  const directionsService = new google.maps.DirectionsService();
  const request = {
    origin: new google.maps.LatLng(routePair.origin.lat, routePair.origin.lng),
    destination: new google.maps.LatLng(
      routePair.destination.lat,
      routePair.destination.lng
    ),
    travelMode: google.maps.TravelMode.DRIVING,
  };

  directionsService.route(request, (result, status) => {
    if (status === "OK") {
      routeTime.value = Math.round(
        result.routes[0].legs[0].duration.value / 60
      );
      console.log(`✅ 計算成功：${routeTime.value} 分鐘`);
    } else {
      console.error("❌ 無法計算路徑時間:", status);
      routeTime.value = null;
    }
  });
};

// 監聽 routePairs 變更，重新計算時間
watch(
  () => placeStore.routePairs[props.date]?.[props.index],
  (newVal) => {
    if (newVal && newVal.origin && newVal.destination) {
      console.log("✅ 觸發計算，開始 calculateRouteTime()");
      calculateRouteTime();
    } else {
      console.warn("⚠️ newVal 為空，未能觸發 calculateRouteTime");
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped>
.route-time-container {
  padding: 8px;
  background: #f7fafc;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 8px;
}
</style>
