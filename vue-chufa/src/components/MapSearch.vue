<template>
    <div>
    <h1>Google Maps 地點搜尋</h1>
    <input
        type="text"
        id="search-input"
        placeholder="搜尋地點"
    />
    <div id="map"></div>
    <button @click="saveSelectedPlace">儲存地點</button>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const map = ref(null);
    const marker = ref(null);
    const markerContent = ref(null);

    const initMap = async () => {
      // 確保 Google Maps API 已正確加載
      if (typeof google === "undefined" || !google.maps) {
        console.error("Google Maps API 未加載");
        return;
      }

      // 引入 Google Maps 模組
      const { Map } = await google.maps.importLibrary("maps");
      const { ColorScheme } = await google.maps.importLibrary("core");
      const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
      const myLatlng = { lat: 25.0339643, lng: 121.564468 };

      // 初始化地圖
      map.value = new google.maps.Map(document.getElementById("map"), {
        zoom: 12,
        center: myLatlng,
        mapId: "DEMO_MAP_ID",
        gestureHandling: "cooperative",
        zoomControl: true,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: true,
        colorScheme: ColorScheme.FOLLOW_SYSTEM,
      });

      // 建立自訂標記內容
      markerContent.value = document.createElement("div");
      markerContent.value.className = "drop"; // 使用 CSS 動畫樣式
      markerContent.value.innerHTML = `
        <div style="width: 24px; height: 24px; background-color: red; border-radius: 50%;"></div>
      `;

      // 建立標記
      marker.value = new google.maps.marker.AdvancedMarkerElement({
        position: myLatlng,
        map: map.value,
        title: "點擊以放大",
        content: markerContent.value, // 套用自訂內容
      });

      // 新增地圖中心移動監聽器
      map.value.addListener("center_changed", () => {
        window.setTimeout(() => {
          map.value.panTo(marker.value.position);
        }, 3000);
      });

      // 新增標記點擊事件
      marker.value.addListener("click", () => {
        map.value.setZoom(17);
        map.value.setCenter(marker.value.position);
      });
    };

    // 使用 onMounted 生命週期鉤子初始化地圖
    onMounted(() => {
      initMap();
    });

    return {
      map,
      marker,
    };
  },
};
</script>

<style>
html,
body {
  height: 100%; /* 確保頁面容器有高度 */
  margin: 0;
  padding: 0;
}

#map {
  height: 500px;
  width: 100%;
  margin-bottom: 10px;
}

/* set the default transition time */
:root {
  --delay-time: .5s;
}

@keyframes drop {
  0% {
    transform: translateY(-200px) scaleY(0.9);
    opacity: 0;
  }
  5% {
    opacity: 0.7;
  }
  50% {
    transform: translateY(0px) scaleY(1);
    opacity: 1;
  }
  65% {
    transform: translateY(-17px) scaleY(0.9);
    opacity: 1;
  }
  75% {
    transform: translateY(-22px) scaleY(0.9);
    opacity: 1;
  }
  100% {
    transform: translateY(0px) scaleY(1);
    opacity: 1;
  }
}
.drop {
  animation: drop 0.3s linear forwards var(--delay-time);
}

.ui-button {
  background-color: #fff;
  border: 0;
  border-radius: 2px;
  box-shadow: 0 1px 4px -1px rgba(0, 0, 0, 0.3);
  margin: 10px;
  padding: 0 0.5em;
  font: 400 18px Roboto, Arial, sans-serif;
  overflow: hidden;
  height: 40px;
  cursor: pointer;
}

.ui-button:hover {
  background: rgb(235, 235, 235);
}
</style>