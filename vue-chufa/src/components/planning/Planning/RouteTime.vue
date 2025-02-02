<template>
  <div>
    <p v-if="routeTime">
      從 {{ origin.displayName }} 到 {{ destination.displayName }} 的行程時間：{{
        routeTime
      }}
      分鐘
    </p>
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
  const origin = placeStore.origin.value;
  const destination = placeStore.destination.value;

  if (!origin || !destination) {
    console.error("起點或終點資訊缺失");
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
    if (status === google.maps.DirectionsStatus.OK) {
      // 獲取路徑時間 (路徑時間是以秒為單位)
      const duration = result.routes[0].legs[0].duration.value;
      routeTime.value = Math.round(duration / 60); // 轉換為分鐘
    } else {
      console.error("無法計算路徑時間:", status);
    }
  });
};

// 在組件掛載時執行計算
onMounted(() => {
  calculateRouteTime();
});
</script>
