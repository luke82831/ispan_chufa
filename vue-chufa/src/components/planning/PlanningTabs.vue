<template>
  <div class="container">
    <!-- 左邊表單 -->
    <div class="form-container">
      <!-- 表單內容放置於此 -->
      <h2>這裡放行程標題</h2>
      <p>下面會放每日的行程內容...</p>
    </div>

    <!-- 右邊地圖 -->
    <div class="map-container">
      <MapDisplay />
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import MapDisplay from "./GoogleMap/MapDisplay.vue";

export default defineComponent({
  components: {
    MapDisplay,
  },
});
</script>

<style>
.container {
  display: flex;
  height: 100vh; /* 占滿視窗高度 */
  width: 100%; /* 占滿視窗寬度 */
  margin: 0; /* 移除可能的外部間距 */
  gap: 0; /* 移除容器內部的間距 */
}

.form-container {
  flex: 0 0 40%; /* 固定 30% 寬度 */
  height: 100%; /* 滿高 */
  overflow-y: auto; /* 垂直滾動 */
  padding: 5px;
  box-shadow: 8px 0 16px rgba(0, 0, 0, 0.15); /* 陰影 */
  box-sizing: border-box; /* 確保 padding 不影響寬度計算 */
  position: relative; /* 確保 z-index 運作 */
  z-index: 10; /* 讓表單顯示在地圖上方 */
  background-color: #f9f9f9;
  scrollbar-width: thin;
  scrollbar-color: #ccc #f9f9f9;
}

.form-container::-webkit-scrollbar {
  width: 8px;
}

.form-container::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 4px;
}

.map-container {
}

/*螢幕較小的介面調整，例如:手機*/
@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
  .form-container {
    flex: none;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #ccc;
  }
  .map-container {
    flex: none;
    height: 300px; /* 固定地圖高度 */
  }
}
</style>
