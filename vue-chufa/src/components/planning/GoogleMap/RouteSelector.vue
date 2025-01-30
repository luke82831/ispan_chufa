<template>
  <div class="route-selector">
    <!-- 顯示預設最佳路線 -->
    <div class="route-summary" @click="toggleExpand">
      🚗 最佳路線：
      <span v-if="selectedRoute?.duration">{{ selectedRoute?.duration }}</span>
      <span v-else>正在計算路線...</span>
    </div>

    <!-- 展開時顯示所有可選路線 -->
    <div v-if="expanded" class="route-options">
      <div v-for="(route, index) in routes" :key="index" class="route-option">
        <input
          type="radio"
          :id="'route-' + index"
          :value="route"
          v-model="selectedRoute"
          @change="updateRoute"
        />
        <label :for="'route-' + index">
          🛣️ {{ route.summary }} - 約 {{ route.duration }}
        </label>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { usePlaceStore } from "@/stores/PlaceStore"; // 引入 Pinia store

// 定義 props
const props = defineProps({
  onUpdate: {
    type: Function,
    required: true,
  },
});

const placeStore = usePlaceStore();

const expanded = ref(false); // 控制展開與收合
const routes = ref([]); // 存放多條可選路線
const selectedRoute = ref(null); // 初始為 null，後續根據路線選擇更新

// 監聽 store 中的 origin 和 destination
watch(
  () => placeStore.origin,
  (newOrigin) => {
    console.log("Origin changed:", newOrigin); // 確認 origin 是否有變
    fetchRoutes();
  }
);

watch(
  () => placeStore.destination,
  (newDestination) => {
    console.log("Destination changed:", newDestination); // 確認 destination 是否有變
    fetchRoutes();
  }
);

// 切換展開/收合
const toggleExpand = () => {
  expanded.value = !expanded.value;
};

// 取得 Google Maps 路線
const fetchRoutes = async () => {
  const origin = placeStore.origin;
  const destination = placeStore.destination;

  if (!origin || !destination) {
    console.warn("Origin or Destination is missing");
    console.log("Origin:", origin);
    console.log("Destination:", destination);
    return;
  }

  const originLatLng = new google.maps.LatLng(origin.lat, origin.lng);
  const destinationLatLng = new google.maps.LatLng(destination.lat, destination.lng);

  const directionsService = new google.maps.DirectionsService();

  try {
    const response = await directionsService.route({
      origin: originLatLng,
      destination: destinationLatLng,
      travelMode: google.maps.TravelMode.DRIVING,
      provideRouteAlternatives: true,
    });

    if (response.status === "OK") {
      routes.value = response.routes.map((route) => ({
        summary: route.summary,
        duration: route.legs[0].duration.text,
        routeData: route,
      }));

      if (!selectedRoute.value && routes.value.length > 0) {
        selectedRoute.value = routes.value[0];
        if (props.onUpdate) props.onUpdate(routes.value[0]); // 傳遞選中的路線到父組件
      }
    } else {
      console.error("Error fetching routes:", response.status);
    }
  } catch (error) {
    console.error("Error fetching routes:", error);
  }
};

// 初始化時載入路線
onMounted(fetchRoutes);
</script>

<style scoped>
.route-selector {
  cursor: pointer;
  background: #f8f8f8;
  padding: 8px;
  border-radius: 8px;
  margin: 8px 0;
}

.route-summary {
  font-weight: bold;
}

.route-options {
  margin-top: 8px;
}

.route-option {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
