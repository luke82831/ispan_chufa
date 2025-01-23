<template>
  <div class="map">
    <div id="map"></div>
    <div class="search">
      <PlaceSearch @place-selected="handlePlaceChanged" />
    </div>
    <!-- 使用 Pinia store 中的 placeDetails -->
    <PlaceDetail v-if="placeDetails" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import PlaceSearch from "@/components/planning/GoogleMap/PlaceSearch.vue";
import PlaceDetail from "./PlaceDetail.vue";
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store

const placeStore = usePlaceStore(); // 使用 store

const map = ref(null); // 地圖實例
const markers = ref([]); // 存儲所有標記

onMounted(() => {
  initMap();
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

// 處理地點變更
const handlePlaceChanged = (place) => {
  if (!place || !place.placeId) {
    console.error("無效的地點或缺少幾何資料");
    return;
  }

  const { lat, lng } = {
    lat: place.latitude,
    lng: place.longitude,
  };

  // 更新地圖中心和縮放級別
  map.value.setCenter({ lat, lng });
  map.value.setZoom(17);

  // 更新標記
  updateMarker({ lat, lng }, place);

  // 格式化地點資料
  const convertPriceLevel = (priceLevel) => {
    switch (priceLevel) {
      case 0:
        return "便宜";
      case 1:
        return "平價";
      case 2:
        return "中等";
      case 3:
        return "高級";
      case 4:
        return "奢華";
      default:
        return null;
    }
  };

  // 格式化營業時間資料
  const convertBusinessHours = (openingHours) => {
    if (!openingHours) return null;
    return openingHours.split("\n").reduce((acc, line) => {
      const [day, hours] = line.split(": ");
      if (day && hours) {
        acc[day.trim()] = hours.trim();
      }
      return acc;
    }, {});
  };

  // 格式化訂位資訊
  const updatedPlace = {
    displayName: place.placeName || null,
    formattedAddress: place.placeAddress || null,
    location: { lat: place.latitude, lng: place.longitude }, // 確保有 lat 和 lng
    rating: place.rating || null,
    openingHours: convertBusinessHours(place.businessHours) || null,
    photos: place.photos || [],
    types: place.placeType || null,
    formattedPhoneNumber: place.placePhone || null,
    website: place.website || null,
    priceLevel: convertPriceLevel(place.priceLevel) || null,
    addressComponents: place.address_components || [],
    reservation: place.reservation || null,
  };

  // 使用 store 設定 placeDetails
  placeStore.setPlaceDetails(updatedPlace);
};

// 更新或新增標記並顯示資訊框
function updateMarker(position, place) {
  const marker = new google.maps.Marker({
    map: map.value,
    position: position,
    animation: google.maps.Animation.DROP,
  });

  const infoWindow = new google.maps.InfoWindow({
    content: `<div><h3>${place.placeName}</h3><p>${place.placeAddress}</p></div>`,
  });

  marker.addListener("animation_changed", () => {
    if (marker.getAnimation() === null) {
      infoWindow.open(map.value, marker);
    }
  });

  markers.value.push(marker);
}

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
