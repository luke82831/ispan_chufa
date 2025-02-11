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

// **取得當天的路徑配對資訊**
const routePairs = computed(() => itineraryStore.getRoutePairs(props.date));

// **計算路徑時間**
const calculateRouteTime = () => {
  const routePair = routePairs.value[props.index];

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
      const travelMinutes = Math.round(
        result.routes[0].legs[0].duration.value / 60
      );
      routeTime.value = travelMinutes;
      console.log(`✅ 計算成功：${travelMinutes} 分鐘`);
    } else {
      console.error("❌ 無法計算路徑時間:", status);
      routeTime.value = null;
    }
  });
};

// **監聽 `itineraryStore` 內的路線變更**
watch(
  () => routePairs.value[props.index], // ✅ 改為監聽 itineraryStore
  (newVal) => {
    if (newVal && newVal.origin && newVal.destination) {
      console.log("✅ 觸發計算，開始 calculateRouteTime()");
      calculateRouteTime();
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped></style>
