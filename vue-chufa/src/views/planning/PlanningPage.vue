<template>
  <div class="container">
    <!-- event -->
    <div class="form-container">
      <PlanningTabs></PlanningTabs>
    </div>

    <!-- placedetails (only show if a place is selected) -->
    <div v-if="placeSelected" class="place-container">
      <!-- 傳遞 selectedPlace 到 PlaceDetail 組件 -->
      <PlaceDetail :place="selectedPlace" @update:selectedPlace="handlePlaceSelected" />
    </div>

    <!-- map -->
    <div class="map-container">
      <MapDisplay @place-selected="handlePlaceSelected" />
    </div>
  </div>
</template>

<script>
import MapDisplay from "@/components/planning/GoogleMap/MapDisplay.vue";
import PlanningTabs from "@/components/planning/PlanningTabs.vue";
import PlaceDetail from "@/components/planning/GoogleMap/PlaceDetail.vue";

export default {
  components: {
    PlanningTabs,
    MapDisplay,
    PlaceDetail,
  },
  data() {
    return {
      placeSelected: false, // 控制是否顯示 PlaceDetail
      selectedPlace: null, // 存放選擇的地點資料
    };
  },
  methods: {
    // 處理地點選擇事件
    handlePlaceSelected(place) {
      this.placeSelected = true; // 顯示 PlaceDetail
      this.selectedPlace = place; // 保存選中的地點資料
    },
  },
};
</script>

<style>
.container {
  display: flex;
  height: 100vh; /* 填滿視窗高度 */
  width: 100%; /* 填滿視窗寬度 */
  margin: 0;
  padding: 0;
}

.form-container {
  flex: 0 0 25%; /* 固定 30% 寬度 */
  height: 100%; /* 滿高 */
  overflow-y: auto; /* 垂直滾動 */
  padding: 5px;
  box-shadow: 8px 0 16px rgba(0, 0, 0, 0.15); /* 陰影 */
  box-sizing: border-box; /* 確保 padding 不影響寬度計算 */
  position: relative; /* 確保 z-index 運作 */
  z-index: 10; /* 讓表單顯示在地圖上方 */
  background-color: #f9f9f9;
  overflow-y: auto; /* 當內容超出高度時，允許垂直滾動 */
  overflow-x: hidden; /* 隱藏水平滾動條 */
  box-sizing: border-box; /* 包括 padding 在內計算寬高 */
  word-wrap: break-word; /* 自動換行，避免溢出 */
  word-break: break-word; /* 將長單詞或字串強制換行 */
  white-space: normal; /* 確保內容不強制在一行內顯示 */
}

.map-container {
  height: 100%; /* 確保容器高度填滿父容器 */
  flex: 1; /* 自動填滿剩餘空間 */
  position: relative; /* 確保內部絕對定位基於 .map-container */
}

.place-container {
  position: relative;
  top: 0;
  width: 25%; /* 占地圖區域的寬度 */
  height: 100vh; /* 高度填滿視窗 */
  z-index: 15; /* 保證 PlaceDetail 覆蓋在地圖之上 */
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2); /* 輕微陰影 */
  overflow-y: auto; /* 當內容超出高度時，允許垂直滾動 */
  overflow-x: hidden; /* 隱藏水平滾動條 */
  padding: 16px; /* 內部間距 */
  box-sizing: border-box; /* 包括 padding 在內計算寬高 */
  word-wrap: break-word; /* 自動換行，避免溢出 */
  word-break: break-word; /* 將長單詞或字串強制換行 */
  white-space: normal; /* 確保內容不強制在一行內顯示 */
}
</style>
