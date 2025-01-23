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
import { watch, ref } from "vue";
import { usePlaceStore } from "@/stores/placestore"; // 引入 Pinia store

const placeStore = usePlaceStore();

// 透過 ref 初始化 selectedPlace，這樣我們可以在 watch 中修改它
const selectedPlace = ref(placeStore.selectedPlace);

// 使用 watch 來監聽 store 中的 selectedPlace
watch(
  () => placeStore.selectedPlace,
  (newPlace) => {
    selectedPlace.value = newPlace;
  }
);
</script>

<style></style>
