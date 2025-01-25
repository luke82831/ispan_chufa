<template>
  <h2>這裡放行程標題</h2>
  <p>行程日期</p>
  <div>
    <VueDraggableNext v-model="placeStore.itineraries" item-key="displayName">
      <div
        v-for="(itinerary, index) in placeStore.itineraries"
        :key="itinerary.displayName"
        class="itinerary-item"
      >
        <ol>
          <span class="itinerary-name">{{ itinerary.displayName }}</span>
          <span class="itinerary-address">{{ itinerary.formattedAddress }}</span>
          <!-- 刪除按鈕 -->
          <button @click="removeFromItinerary(index)">刪除</button>
        </ol>
      </div>
    </VueDraggableNext>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store
import { VueDraggableNext } from "vue-draggable-next";

const placeStore = usePlaceStore();

// 呼叫 store 中的 removeFromItinerary 方法刪除行程
const removeFromItinerary = (index) => {
  placeStore.removeFromItinerary(index);
};
</script>

<style scoped>
.itinerary-item {
  border: 3px solid #5f5f5f; /* 强制应用边框 */
  margin: 0px 20px 10px 20px;
  border-radius: 5px;
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
</style>
