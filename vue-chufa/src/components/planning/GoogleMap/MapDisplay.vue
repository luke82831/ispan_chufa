<template>
  <div class="map">
    <div id="map"></div>
    <div class="search">
      <PlaceSearch @place-selected="handlePlaceChanged" />
    </div>
    <!-- 傳遞 selectedPlace 到 PlaceDetail -->
    <PlaceDetail v-if="false" :place="selectedPlace" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import PlaceSearch from "@/components/planning/GoogleMap/PlaceSearch.vue";
import PlaceDetail from "./PlaceDetail.vue";

// 定義事件
const emit = defineEmits(["place-selected"]);

const map = ref(null); // 地圖實例
const markers = ref([]); // 存儲所有標記
const selectedPlace = ref(null); // 已選地點

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
    console.log("地點:", place);
    console.log("是否有placeId:", place.placeId);
    console.error("無效的地點或缺少幾何資料");
    return;
  }

  const { lat, lng } = {
    lat: place.latitude,
    lng: place.longitude,
  };

  map.value.setCenter({ lat, lng });
  map.value.setZoom(17);

  updateMarker({ lat, lng }, place);

  // 根據 priceLevel 數值轉換為中文描述
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

  // 根據營業時間字串轉換為物件格式
  const convertBusinessHours = (openingHours) => {
    if (!openingHours) return null;

    // 將字串切分並轉換為物件
    return openingHours.split("\n").reduce((acc, line) => {
      const [day, hours] = line.split(": "); // 用 ": " 分隔
      if (day && hours) {
        acc[day.trim()] = hours.trim(); // 去除多餘空白
      }
      return acc;
    }, {});
  };
  // const businessHours =
  //   "星期一: 24 小時營業\n星期二: 24 小時營業\n星期三: 24 小時營業\n星期四: 24 小時營業\n星期五: 24 小時營業\n星期六: 24 小時營業\n星期日: 24 小時營業";

  // const businessHoursObject = convertBusinessHours(businessHours);
  // console.log(businessHoursObject);

  // 更新 selectedPlace 並發送事件給父組件
  selectedPlace.value = {
    displayName: place.placeName || null,
    formattedAddress: place.placeAddress || null,
    location: { lat, lng },
    rating: place.rating || null,
    openingHours: convertBusinessHours(place.businessHours) || null, // 傳入轉換後的物件
    photos: place.photos || [],
    types: place.placeType || [],
    formattedPhoneNumber: place.placePhone || null,
    website: place.website || null,
    priceLevel: convertPriceLevel(place.priceLevel) || null, // 使用函式轉換價格等級
    addressComponents: place.address_components || [],
    reservation: place.reservation || null,
  };

  // 發送 place-selected 事件到父組件，傳遞 selectedPlace
  emit("place-selected", selectedPlace.value);
};

// 更新或新增標記並顯示資訊框
function updateMarker(position, place) {
  markers.value.forEach((marker) => marker.setMap(null));
  markers.value = [];

  const marker = new google.maps.Marker({
    map: map.value,
    position: position,
    animation: google.maps.Animation.DROP, // 啟用落下動畫
  });

  // 創建資訊框
  const infoWindow = new google.maps.InfoWindow({
    content: `<div><h3>${place.placeName}</h3><p>${place.placeAddress}</p></div>`,
  });

  // 設定標記動畫完成後顯示資訊框
  marker.addListener("animation_changed", () => {
    if (marker.getAnimation() === null) {
      // 落下動畫結束後顯示資訊框
      infoWindow.open(map.value, marker);
    }
  });

  markers.value.push(marker); // 新增到標記清單
}
</script>

<style>
.search {
  position: absolute;
  top: 20px; /* 距離容器頂部 20px，可根據需要調整 */
  left: 50%;
  transform: translateX(-50%);
  width: 400px; /* 固定寬度，可根據需求調整 */
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
  padding: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 添加陰影 */
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
