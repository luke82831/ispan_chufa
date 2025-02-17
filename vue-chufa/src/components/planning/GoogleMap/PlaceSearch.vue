<template>
  <div>
    <input id="search-input" type="text" v-model="searchInput" placeholder="請輸入地點" />
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from "vue";
import { usePlaceStore } from "@/stores/placeStore";

// 從剛才新建立的檔案匯入
import {
  initAutocomplete,
  checkPlaceInBackend,
  fetchPlaceDetailsFromGoogle,
} from "@/services/googleMapService.js"; // 自行調整路徑

const emit = defineEmits(["place-selected"]);
const placeStore = usePlaceStore();
const searchInput = ref("");

// onMounted 時初始化
onMounted(() => {
  initAutocomplete(
    "search-input", // 傳入你的 input ID
    placeStore,
    emit,
    checkPlaceInBackend,
    fetchPlaceDetailsFromGoogle
  );
});
</script>

<style scoped>
.search input {
  width: 90%;
  padding: 12px;
  font-size: 18px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
</style>
