<template>
  <h2>這裡放行程標題</h2>
  <p>行程日期</p>
  <div>
    <VueDraggableNext v-model="placeStore.itineraries" item-key="displayName">
      <div
        v-for="(itinerary, index) in placeStore.itineraries"
        :key="itinerary.displayName"
        class="itinerary-item"
        @dragover="onDragOver"
        @dragleave="onDragLeave"
      >
        <ol>
          <span class="itinerary-name">{{ itinerary.displayName }}</span>
          <span class="itinerary-address">{{ itinerary.formattedAddress }}</span>
          <!-- 刪除按鈕 -->
          <button @click="removeFromItinerary(index)" class="delete-btn">X</button>
        </ol>
      </div>
    </VueDraggableNext>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/placeStore"; // 引入 Pinia store
import { VueDraggableNext } from "vue-draggable-next";

const placeStore = usePlaceStore();

// 呼叫 store 中的 removeFromItinerary 方法刪除行程
const removeFromItinerary = (index) => {
  placeStore.removeFromItinerary(index);
};

// 處理拖曳事件，改變鼠標樣式
const onDragOver = (event) => {
  event.preventDefault(); // 允許拖曳進入目標區域
  event.target.style.cursor = "move"; // 改變鼠標樣式
};

const onDragLeave = (event) => {
  event.target.style.cursor = "default"; // 恢復默認鼠標樣式
};
</script>

<style scoped>
.itinerary-item {
  border: 3px solid #5f5f5f; /* 强制应用边框 */
  margin: 0px 20px 10px 20px;
  border-radius: 5px;
  position: relative; /* 讓刪除按鈕可以定位 */
}

.itinerary-name {
  display: block;
  font-weight: bold; /* 粗体 */
}

.itinerary-address {
  display: block;
  font-size: 0.8em; /* 字体稍小 */
  color: #666; /* 地址颜色 */
}

.delete-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent; /* 按鈕背景透明 */
  border: none;
  font-size: 1.2em; /* 增大字體以顯示為 X */
  color: #888; /* 按鈕顏色 */
  cursor: pointer; /* 鼠標為可點擊狀態 */
}

.delete-btn:hover {
  color: #f44336; /* 鼠標懸停時顏色改變 */
}
</style>
