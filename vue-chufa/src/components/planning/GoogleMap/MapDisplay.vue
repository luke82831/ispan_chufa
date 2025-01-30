<template>
  <div class="map">
    <div id="map"></div>
    <div class="search">
      <PlaceSearch @place-selected="handlePlaceChanged" />
    </div>
    <PlaceDetail v-if="placeDetails" />
    <div class="route-planner">
      <!-- 按鈕或其他方式觸發路線規劃 -->
      <button @click="planRoute">規劃路線</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import PlaceSearch from "@/components/planning/GoogleMap/PlaceSearch.vue";
import PlaceDetail from "./PlaceDetail.vue";
import { usePlaceStore } from "@/stores/PlaceStore"; // 引入 Pinia store
import { initMap, updateMarker, formatPlaceDetails } from "@/utils/placeManager"; // 引入工具函式

const placeStore = usePlaceStore();

const map = ref(null);
const markers = ref([]);

onMounted(() => {
  initMapFunction();
});

const initMapFunction = async () => {
  try {
    const { Map } = await google.maps.importLibrary("maps");

    // 使用 utils 中的 initMap 函式
    map.value = initMap(document.getElementById("map"), {
      zoom: 12,
      center: { lat: 25.0339643, lng: 121.564468 },
      mapId: "DEMO_MAP_ID",
      gestureHandling: "cooperative",
      zoomControl: true,
      mapTypeControl: false,
      streetViewControl: false,
      fullscreenControl: true,
    });
  } catch (error) {
    console.error("地圖初始化失敗:", error);
  }
};

// 處理地點變更
const handlePlaceChanged = (place) => {
  if (!place || !place.placeId) {
    console.error("無效的地點或缺少幾何資料");
    return;
  }

  const { lat, lng } = { lat: place.latitude, lng: place.longitude };

  // 更新地圖中心和縮放級別
  map.value.setCenter({ lat, lng });
  map.value.setZoom(17);

  // 更新標記
  updateMarker(map.value, { lat, lng }, place, markers.value);

  // 使用 utils 中的 formatPlaceDetails 來處理地點資料
  const updatedPlace = formatPlaceDetails(place);
  placeStore.setPlaceDetails(updatedPlace);
};

// 從 store 中取得 placeDetails
const placeDetails = placeStore.placeDetails;
</script>

<style>
.search {
  position: absolute;
  top: 20px; /* 距離容器頂部 20px，可根據需要調整 */
  left: 50%;
  transform: translateX(-50%);
  width: 400px; /* 固定寬度，可根據需求調整 */
  border-radius: 8px; /* 圓角設計 */
  z-index: 10; /* 確保在其他元素之上 */
}

.map {
  height: 100%; /* 確保容器滿高 */
  width: 100%;
  flex-grow: 1;
  position: relative; /* 確保內部元素的絕對定位相對於 .container */
  position: relative; /* 為地圖設定定位基準 */
  height: 100%; /* 確保容器滿高 */
}

#map {
  height: 100%;
  width: 100%;
}
</style>
