<template>
  <div class="container">
    <!-- 搜尋區域 -->
    <div class="search">
      <h1>Google Maps 地點搜尋</h1>
      <PlaceSearch :onPlaceSelected="handlePlaceChanged" />
    </div>
    
    <!-- 地圖區域 -->
    <div class="map">
      <div id="map"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import PlaceSearch from "@/components/GoogleMap/PlaceSearch.vue";

export default {
  components: {
    PlaceSearch,
  },
  setup() {
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
      if (!place || !place.geometry || !place.geometry.location) {
        console.error("無效的地點或缺少幾何資料");
        return;
      }

      const { lat, lng } = {
        lat: place.geometry.location.lat(),
        lng: place.geometry.location.lng(),
      };

      map.value.setCenter(place.geometry.location);
      map.value.setZoom(17);

      

      updateMarker({ lat, lng }, place);

      selectedPlace.value = {
        displayName: place.name || "未知地點",
        formattedAddress: place.formatted_address || "無地址",
        location: { lat, lng },
      };
    };

    // 更新或新增標記並顯示資訊框
    const updateMarker = (position, place) => {
      markers.value.forEach(marker => marker.setMap(null));
      markers.value = [];

      const marker = new google.maps.Marker({
        map: map.value,
        position: position,
        animation: google.maps.Animation.DROP, // 啟用落下動畫
      });

      // 創建資訊框
      const infoWindow = new google.maps.InfoWindow({
        content: `<div><h3>${place.name}</h3><p>${place.formatted_address}</p></div>`,
      });

      // 設定標記動畫完成後顯示資訊框
      marker.addListener("animation_changed", () => {
        if (marker.getAnimation() === null) {
          // 落下動畫結束後顯示資訊框
          infoWindow.open(map.value, marker);
        }
      });

      markers.value.push(marker); // 新增到標記清單
    };


    return {
      map,
      markers,
      selectedPlace,
      handlePlaceChanged,
    };
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100%;
}

.search {
  height: 500px;
  width: 500px;
  padding: 20px;
  background-color: #e6e6e6e5;
  overflow-y: auto;
  border: 2px solid #ddd;
  border-radius: 5px;
}

.map {
  margin-left: 10px;
  flex-grow: 1;
  border: 2px solid #ddd;
  border-radius: 5px;
}

#map {
  height: 100%;
  width: 100%;
  align-items: center;
  justify-content: center;
}
</style>