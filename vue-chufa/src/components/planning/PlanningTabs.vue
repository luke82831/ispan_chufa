<template>
  <div>
    <div>
      <h2>這裡放行程標題</h2>
      <p>下面會放每日的行程內容...</p>

      <!-- 顯示選擇的地點 -->
      <div v-if="selectedPlace">
        <h3>選擇的地點：</h3>
        <p>{{ selectedPlace.displayName }}</p>
        <p>{{ selectedPlace.formattedAddress }}</p>
        <!-- 可以根據需要顯示更多的 selectedPlace 資料 -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { watch } from "vue";
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store

const placeStore = usePlaceStore();
const selectedPlace = placeStore.selectedPlace;

// 監聽 selectedPlace 變化
watch(
  () => placeStore.selectedPlace,
  (newPlace) => {
    console.log("選擇的地點:", newPlace);
  }
);
</script>

<style></style>
