<template>
  <div class="route-time-container">
    <p v-if="routeTime !== null">🚗 {{ routeTime }} 分鐘</p>
    <p v-else>正在計算行程時間...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { usePlaceStore } from "@/stores/PlaceStore"; // 引入 PlaceStore

const placeStore = usePlaceStore(); // 使用 PlaceStore
const routeTime = ref(null);

// 使用 DirectionsService 計算路徑時間
const calculateRouteTime = () => {
  console.log("🚀 開始計算路線時間...");
  console.log("🔍 檢查起點:", placeStore.origin);
  console.log("🔍 檢查終點:", placeStore.destination);

  const origin = placeStore.origin;
  const destination = placeStore.destination;

  if (!origin || !destination) {
    console.error("起點或終點資訊缺失");
    console.log("origin:" + origin);
    console.log("destination:" + destination);
    return;
  }

  const directionsService = new google.maps.DirectionsService();

  // 設置路徑查詢的參數
  const request = {
    origin: new google.maps.LatLng(origin.lat, origin.lng),
    destination: new google.maps.LatLng(destination.lat, destination.lng),
    travelMode: google.maps.TravelMode.DRIVING, // 可以根據需要選擇 WALKING, BICYCLING, TRANSIT
  };

  directionsService.route(request, (result, status) => {
    console.log("Google Maps API 回傳:", result, status);
    if (status === google.maps.DirectionsStatus.OK) {
      // 獲取路徑時間 (路徑時間是以秒為單位)
      const duration = result.routes[0].legs[0].duration.value;
      routeTime.value = Math.round(duration / 60); // 轉換為分鐘
      console.log("✅ 計算成功，時間:", routeTime.value, "分鐘");
    } else {
      console.error("❌ 無法計算路徑時間:", status);
    }
  });
};

// 在組件掛載時執行計算
onMounted(() => {
  console.log("onMounted觸發，開始計算路線時間");
  calculateRouteTime();
});
</script>

<style scoped>
.route-time-container {
  padding: 8px;
  background: #f7fafc;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  margin-top: 8px;
  margin-left: 20px; /* 確保路徑與地點分開顯示 */
}
</style>
