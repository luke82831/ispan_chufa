<template>
  <div class="map">
    <div id="map"></div>
    <div class="search">
      <PlaceSearch @place-selected="handlePlaceChanged" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { usePlaceStore } from "@/stores/PlaceStore";
import PlaceSearch from "@/components/planning/GoogleMap/PlaceSearch.vue";

const placeStore = usePlaceStore();

const map = ref(null);
const markers = ref([]);

// ✅ 監聽 store 內的 `selectedPlaceDetail`
const selectedPlaceDetail = computed(() => placeStore.selectedPlaceDetail);

// 地圖初始化
onMounted(() => {
  initMap();
});

// ✅ 監聽 `selectedPlaceDetail` 的變化，當地點變更時，自動更新 `marker`
watch(selectedPlaceDetail, (newPlace) => {
  if (newPlace) {
    handlePlaceChanged(newPlace);
  }
});

// 初始化地圖
const initMap = async () => {
  try {
    const { Map } = await google.maps.importLibrary("maps");
    map.value = new Map(document.getElementById("map"), {
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

// 更新地點
const handlePlaceChanged = (place) => {
  console.log("選擇的地點", place);

  if (!place || (!place.placeId && !place.googlemapPlaceId)) {
    console.error("❌ 無效的地點或缺少幾何資料", place);
    return;
  }

  if (!map.value) {
    console.error("❌ 地圖尚未初始化，無法設定標記");
    return;
  }

  const lat = place.latitude || place?.location?.lat || null;
  const lng = place.longitude || place?.location?.lng || null;

  if (!lat || !lng) {
    console.error("❌ 地點缺少經緯度資訊:", place);
    return;
  }

  // 更新地圖中心和縮放級別
  map.value.setCenter({ lat, lng });
  map.value.setZoom(17);

  // 更新標記
  updateMarker({ lat, lng }, place);
};

// 更新或新增標記
function updateMarker(position, place) {
  if (!map.value) {
    console.error("地圖尚未初始化，無法添加標記");
    return;
  }

  // 先清除舊標記
  clearMarkers();

  // 創建新標記
  const marker = new google.maps.Marker({
    position,
    map: map.value,
    title: place.placeName || "標記",
    animation: google.maps.Animation.DROP,
  });

  // 創建資訊視窗
  const infoWindow = new google.maps.InfoWindow({
    content: `<div><h3>${place.placeName}</h3><p>${place.placeAddress}</p></div>`,
  });

  // **監聽標記動畫結束，然後開啟資訊視窗**
  marker.addListener("animation_changed", () => {
    if (!marker.getAnimation()) {
      infoWindow.open(map.value, marker);
    }
  });

  // 點擊標記時顯示資訊視窗
  marker.addListener("click", () => {
    infoWindow.open(map.value, marker);
  });

  // 儲存標記
  markers.value.push(marker);
}

// 清除所有標記
function clearMarkers() {
  markers.value.forEach((marker) => {
    if (marker) {
      marker.setMap(null);
    }
  });

  markers.value = [];
}
</script>

<style scoped>
.search {
  position: absolute;
  top: 20px; /* 距離容器頂部 20px，可根據需要調整 */
  left: 50%;
  transform: translateX(-50%);
  width: 400px; /* 固定寬度，可根據需求調整 */
  border-radius: 8px; /* 圓角設計 */
}

.map {
  height: 100%; /* 確保容器滿高 */
  width: 100%;
  flex-grow: 1;
  position: relative; /* 為地圖設定定位基準 */
}

#map {
  height: 100%;
  width: 100%;
}
</style>
