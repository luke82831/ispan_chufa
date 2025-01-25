<template>
  <div>
    <div>
      <h2>這裡放行程標題</h2>
      <p>下面會放每日的行程內容...</p>
      <div>
        <h3>目前的行程：</h3>
        <ul>
          <li v-for="(itinerary, index) in placeStore.itineraries" :key="index">
            {{ itinerary.displayName }} - {{ itinerary.formattedAddress }}
            <!-- 刪除按鈕 -->
            <button @click="removeFromItinerary(index)">刪除</button>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store

const placeStore = usePlaceStore();

// 呼叫 store 中的 removeFromItinerary 方法刪除行程
const removeFromItinerary = (index) => {
  placeStore.removeFromItinerary(index); // 使用 index 刪除行程
};
</script>

<style></style>
